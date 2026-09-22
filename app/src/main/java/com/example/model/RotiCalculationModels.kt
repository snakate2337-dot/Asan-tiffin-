package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Subscription and Daily Attendance status for Tiffin Customers.
 */
enum class SubscriptionStatus(
  val code: String,
  val marathiLabel: String,
  val englishLabel: String,
  val color: Color,
  val countsForDailyKitchenPrep: Boolean
) {
  ACTIVE("ACTIVE", "सक्रिय (Active)", "Active", Color(0xFF2E7D32), true),
  ON_LEAVE("ON_LEAVE", "रजेवर (On Leave / सुट्टी)", "On Leave", Color(0xFFF57C00), false),
  INACTIVE("INACTIVE", "बंद / निष्क्रिय (Inactive)", "Inactive", Color(0xFF757575), false),
  PAUSED("PAUSED", "तात्पुरते थांबवले (Paused)", "Paused", Color(0xFF0288D1), false);

  companion object {
    fun fromCode(code: String): SubscriptionStatus {
      return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ACTIVE
    }
  }
}

/**
 * Customer Profile containing daily roti requirements, meal preference,
 * and active/leave/inactive status.
 */
data class TiffinCustomerProfile(
  val id: String,
  val name: String,
  val phone: String,
  val area: String,
  val address: String = "",
  val assignedBoxNo: String,
  val defaultRotisPerMeal: Int = 3,
  val mealTime: String = "दुपार (Lunch)", // "दुपार (Lunch)", "रात्र (Dinner)", "दोन्ही (Both)"
  var status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
  var leaveReason: String = "",
  var notes: String = ""
) {
  /**
   * Calculates how many rotis this customer needs today based on their active status
   * and meal schedule. Excludes customers on leave, inactive, or paused.
   */
  fun calculateRotisForMeal(filterMeal: String = "All"): Int {
    if (!status.countsForDailyKitchenPrep) return 0

    val mealsToday = when {
      filterMeal == "दुपार (Lunch)" && (mealTime.contains("दुपार") || mealTime.contains("दोन्ही")) -> 1
      filterMeal == "रात्र (Dinner)" && (mealTime.contains("रात्र") || mealTime.contains("दोन्ही")) -> 1
      filterMeal == "All" || filterMeal == "दोन्ही (Both)" -> {
        if (mealTime.contains("दोन्ही")) 2 else 1
      }
      mealTime.contains(filterMeal) -> 1
      else -> 0
    }

    return defaultRotisPerMeal * mealsToday
  }
}

/**
 * Aggregated daily roti forecast and customer statistics summary.
 */
data class DailyRotiSummary(
  val totalRotis: Int,
  val lunchRotis: Int,
  val dinnerRotis: Int,
  val activeCustomerCount: Int,
  val onLeaveCustomerCount: Int,
  val inactiveCustomerCount: Int,
  val totalRegisteredCustomers: Int
)
