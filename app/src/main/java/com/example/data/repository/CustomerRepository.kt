package com.example.data.repository

import com.example.data.local.dao.CustomerDao
import com.example.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern implementation for Customer data abstraction.
 */
class CustomerRepository(private val customerDao: CustomerDao) {

  val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

  val totalCustomerCount: Flow<Int> = customerDao.getTotalCustomerCount()

  val activeSubscriptionCount: Flow<Int> = customerDao.getActiveSubscriptionCount()

  suspend fun getCustomerById(id: Long): CustomerEntity? = customerDao.getCustomerById(id)

  fun getCustomerByIdFlow(id: Long): Flow<CustomerEntity?> = customerDao.getCustomerByIdFlow(id)

  suspend fun getCustomerByPhone(phone: String): CustomerEntity? = customerDao.getCustomerByPhone(phone)

  fun getCustomersByStatus(status: String): Flow<List<CustomerEntity>> =
    customerDao.getCustomersBySubscriptionStatus(status)

  fun searchCustomers(query: String): Flow<List<CustomerEntity>> =
    customerDao.searchCustomers(query)

  suspend fun insertCustomer(customer: CustomerEntity): Long =
    customerDao.insertCustomer(customer)

  suspend fun insertCustomers(customers: List<CustomerEntity>): List<Long> =
    customerDao.insertCustomers(customers)

  suspend fun updateCustomer(customer: CustomerEntity) =
    customerDao.updateCustomer(customer)

  suspend fun updateSubscriptionStatus(id: Long, status: String) =
    customerDao.updateSubscriptionStatus(id, status)

  suspend fun updateAssignedBoxNo(id: Long, boxNo: String) =
    customerDao.updateAssignedBoxNo(id, boxNo)

  suspend fun deleteCustomer(customer: CustomerEntity) =
    customerDao.deleteCustomer(customer)

  suspend fun deleteCustomerById(id: Long) =
    customerDao.deleteCustomerById(id)

  suspend fun deleteAllCustomers() =
    customerDao.deleteAllCustomers()
}
