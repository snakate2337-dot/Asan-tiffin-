package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.dao.CustomerDao
import com.example.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomerDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var customerDao: CustomerDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    customerDao = database.customerDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testInsertAndGetCustomerById() = runBlocking {
    val customer = CustomerEntity(
      name = "राहुल कुलकर्णी (Rahul Kulkarni)",
      address = "स्टेशन रोड, पंढरपूर",
      phoneNumber = "9822112233",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE,
      assignedBoxNo = "T-101",
      area = "स्टेशन रोड"
    )

    val id = customerDao.insertCustomer(customer)
    assertTrue(id > 0)

    val retrieved = customerDao.getCustomerById(id)
    assertNotNull(retrieved)
    assertEquals("राहुल कुलकर्णी (Rahul Kulkarni)", retrieved?.name)
    assertEquals("9822112233", retrieved?.phoneNumber)
    assertEquals(CustomerEntity.STATUS_ACTIVE, retrieved?.subscriptionStatus)
    assertEquals("T-101", retrieved?.assignedBoxNo)
  }

  @Test
  fun testGetAllCustomersFlow() = runBlocking {
    val customer1 = CustomerEntity(
      name = "अमित जोशी",
      address = "नवी पेठ, पंढरपूर",
      phoneNumber = "9890123456",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE
    )
    val customer2 = CustomerEntity(
      name = "सचिन गायकवाड",
      address = "इसबावी, पंढरपूर",
      phoneNumber = "9422001122",
      subscriptionStatus = CustomerEntity.STATUS_PAUSED
    )

    customerDao.insertCustomer(customer1)
    customerDao.insertCustomer(customer2)

    val list = customerDao.getAllCustomers().first()
    assertEquals(2, list.size)
  }

  @Test
  fun testUpdateCustomerAndStatus() = runBlocking {
    val customer = CustomerEntity(
      name = "विजय साळुंखे",
      address = "ताकपीठ गल्ली, पंढरपूर",
      phoneNumber = "9765432100",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE
    )

    val id = customerDao.insertCustomer(customer)
    val saved = customerDao.getCustomerById(id)!!

    // Test update
    val updated = saved.copy(
      address = "नवीन पत्ता, ताकपीठ गल्ली",
      subscriptionStatus = CustomerEntity.STATUS_EXPIRED
    )
    customerDao.updateCustomer(updated)

    val afterUpdate = customerDao.getCustomerById(id)
    assertEquals("नवीन पत्ता, ताकपीठ गल्ली", afterUpdate?.address)
    assertEquals(CustomerEntity.STATUS_EXPIRED, afterUpdate?.subscriptionStatus)

    // Test status update query
    customerDao.updateSubscriptionStatus(id, CustomerEntity.STATUS_ACTIVE)
    val afterStatusUpdate = customerDao.getCustomerById(id)
    assertEquals(CustomerEntity.STATUS_ACTIVE, afterStatusUpdate?.subscriptionStatus)
  }

  @Test
  fun testDeleteCustomer() = runBlocking {
    val customer = CustomerEntity(
      name = "सुनीता कदम",
      address = "शिवाजी चौक, पंढरपूर",
      phoneNumber = "9823456789",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE
    )

    val id = customerDao.insertCustomer(customer)
    assertNotNull(customerDao.getCustomerById(id))

    customerDao.deleteCustomerById(id)
    assertNull(customerDao.getCustomerById(id))
  }

  @Test
  fun testSearchCustomers() = runBlocking {
    val c1 = CustomerEntity(
      name = "महेश शेटे",
      address = "महाद्वार घाट",
      phoneNumber = "9850123456",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE,
      assignedBoxNo = "T-107"
    )
    val c2 = CustomerEntity(
      name = "ओंकार शिंदे",
      address = "वाखरी नाका",
      phoneNumber = "9922334455",
      subscriptionStatus = CustomerEntity.STATUS_ACTIVE,
      assignedBoxNo = "T-108"
    )

    customerDao.insertCustomers(listOf(c1, c2))

    val searchResult = customerDao.searchCustomers("शेटे").first()
    assertEquals(1, searchResult.size)
    assertEquals("महेश शेटे", searchResult[0].name)

    val boxSearchResult = customerDao.searchCustomers("T-108").first()
    assertEquals(1, boxSearchResult.size)
    assertEquals("ओंकार शिंदे", boxSearchResult[0].name)
  }
}
