package com.example

import com.example.model.DailyRotiSummary
import com.example.model.SubscriptionStatus
import com.example.model.TiffinCustomerProfile
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for Daily Roti Calculation logic:
 * Verifies calculation filters active users and excludes those marked on leave or inactive.
 */
class RotiCalculationTest {

  @Test
  fun testActiveCustomersIncludedInRotiCount() {
    val activeCustomer = TiffinCustomerProfile(
      id = "1",
      name = "राहुल कुलकर्णी",
      phone = "9822112233",
      area = "स्टेशन रोड",
      assignedBoxNo = "T-101",
      defaultRotisPerMeal = 3,
      mealTime = "दोन्ही (Both)",
      status = SubscriptionStatus.ACTIVE
    )

    // Both meals = 2 meals * 3 rotis = 6 rotis
    assertEquals(6, activeCustomer.calculateRotisForMeal("All"))
    assertEquals(3, activeCustomer.calculateRotisForMeal("दुपार (Lunch)"))
    assertEquals(3, activeCustomer.calculateRotisForMeal("रात्र (Dinner)"))
  }

  @Test
  fun testOnLeaveCustomersExcludedFromRotiCount() {
    val leaveCustomer = TiffinCustomerProfile(
      id = "2",
      name = "अमित जोशी",
      phone = "9890123456",
      area = "नवी पेठ",
      assignedBoxNo = "T-103",
      defaultRotisPerMeal = 4,
      mealTime = "दोन्ही (Both)",
      status = SubscriptionStatus.ON_LEAVE,
      leaveReason = "गावी गेले आहेत"
    )

    // Status is ON_LEAVE -> must return 0 rotis
    assertEquals(0, leaveCustomer.calculateRotisForMeal("All"))
    assertEquals(0, leaveCustomer.calculateRotisForMeal("दुपार (Lunch)"))
    assertEquals(0, leaveCustomer.calculateRotisForMeal("रात्र (Dinner)"))
  }

  @Test
  fun testInactiveCustomersExcludedFromRotiCount() {
    val inactiveCustomer = TiffinCustomerProfile(
      id = "3",
      name = "विजय साळुंखे",
      phone = "9765432100",
      area = "ताकपीठ गल्ली",
      assignedBoxNo = "T-105",
      defaultRotisPerMeal = 4,
      mealTime = "दुपार (Lunch)",
      status = SubscriptionStatus.INACTIVE
    )

    // Status is INACTIVE -> must return 0 rotis
    assertEquals(0, inactiveCustomer.calculateRotisForMeal("All"))
    assertEquals(0, inactiveCustomer.calculateRotisForMeal("दुपार (Lunch)"))
  }

  @Test
  fun testAggregatedSummaryFiltersCorrectly() {
    val customers = listOf(
      // Active 1: Lunch only, 3 rotis
      TiffinCustomerProfile(
        id = "1",
        name = "सचिन",
        phone = "9422001122",
        area = "इसबावी",
        assignedBoxNo = "T-102",
        defaultRotisPerMeal = 3,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.ACTIVE
      ),
      // Active 2: Both meals, 2 rotis per meal = 4 rotis
      TiffinCustomerProfile(
        id = "2",
        name = "सुनीता",
        phone = "9823456789",
        area = "शिवाजी चौक",
        assignedBoxNo = "T-104",
        defaultRotisPerMeal = 2,
        mealTime = "दोन्ही (Both)",
        status = SubscriptionStatus.ACTIVE
      ),
      // On Leave: Excluded (would have been 4 rotis)
      TiffinCustomerProfile(
        id = "3",
        name = "अमित",
        phone = "9890123456",
        area = "नवी पेठ",
        assignedBoxNo = "T-103",
        defaultRotisPerMeal = 4,
        mealTime = "दोन्ही (Both)",
        status = SubscriptionStatus.ON_LEAVE
      ),
      // Inactive: Excluded (would have been 3 rotis)
      TiffinCustomerProfile(
        id = "4",
        name = "विजय",
        phone = "9765432100",
        area = "ताकपीठ गल्ली",
        assignedBoxNo = "T-105",
        defaultRotisPerMeal = 3,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.INACTIVE
      )
    )

    val activeList = customers.filter { it.status == SubscriptionStatus.ACTIVE }
    val onLeaveList = customers.filter { it.status == SubscriptionStatus.ON_LEAVE }
    val inactiveList = customers.filter { it.status == SubscriptionStatus.INACTIVE }

    val totalRotis = activeList.sumOf { it.calculateRotisForMeal("All") }
    val lunchRotis = activeList.sumOf { it.calculateRotisForMeal("दुपार (Lunch)") }
    val dinnerRotis = activeList.sumOf { it.calculateRotisForMeal("रात्र (Dinner)") }

    val summary = DailyRotiSummary(
      totalRotis = totalRotis,
      lunchRotis = lunchRotis,
      dinnerRotis = dinnerRotis,
      activeCustomerCount = activeList.size,
      onLeaveCustomerCount = onLeaveList.size,
      inactiveCustomerCount = inactiveList.size,
      totalRegisteredCustomers = customers.size
    )

    // Expected: Active 1 (3 rotis) + Active 2 (4 rotis) = 7 rotis
    assertEquals(7, summary.totalRotis)
    // Lunch: Sachin (3) + Sunita (2) = 5
    assertEquals(5, summary.lunchRotis)
    // Dinner: Sunita (2) = 2
    assertEquals(2, summary.dinnerRotis)
    assertEquals(2, summary.activeCustomerCount)
    assertEquals(1, summary.onLeaveCustomerCount)
    assertEquals(1, summary.inactiveCustomerCount)
    assertEquals(4, summary.totalRegisteredCustomers)
  }
}
