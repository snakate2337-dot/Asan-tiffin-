package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Customer CRUD operations.
 */
@Dao
interface CustomerDao {

  // ==========================================
  // CREATE (Insert operations)
  // ==========================================

  /**
   * Inserts a single customer. If conflict exists, replaces the existing record.
   * @return The auto-generated row ID of the inserted customer.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity): Long

  /**
   * Inserts multiple customers in bulk.
   * @return List of auto-generated row IDs.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomers(customers: List<CustomerEntity>): List<Long>

  // ==========================================
  // READ (Query operations)
  // ==========================================

  /**
   * Retrieves all customers ordered alphabetically by name.
   */
  @Query("SELECT * FROM customers ORDER BY name ASC")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  /**
   * Retrieves a single customer by their primary key ID.
   */
  @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
  suspend fun getCustomerById(id: Long): CustomerEntity?

  /**
   * Observes a single customer by ID as a reactive Flow.
   */
  @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
  fun getCustomerByIdFlow(id: Long): Flow<CustomerEntity?>

  /**
   * Finds a customer by phone number.
   */
  @Query("SELECT * FROM customers WHERE phone_number = :phoneNumber LIMIT 1")
  suspend fun getCustomerByPhone(phoneNumber: String): CustomerEntity?

  /**
   * Retrieves all customers filtered by their subscription status (e.g. "ACTIVE", "PAUSED", "EXPIRED").
   */
  @Query("SELECT * FROM customers WHERE subscription_status = :status ORDER BY name ASC")
  fun getCustomersBySubscriptionStatus(status: String): Flow<List<CustomerEntity>>

  /**
   * Searches customers by name, address, phone number, or assigned tiffin box number.
   */
  @Query(
    """
    SELECT * FROM customers 
    WHERE name LIKE '%' || :query || '%' 
       OR address LIKE '%' || :query || '%' 
       OR phone_number LIKE '%' || :query || '%'
       OR assigned_box_no LIKE '%' || :query || '%'
    ORDER BY name ASC
    """
  )
  fun searchCustomers(query: String): Flow<List<CustomerEntity>>

  /**
   * Counts the total number of customers.
   */
  @Query("SELECT COUNT(*) FROM customers")
  fun getTotalCustomerCount(): Flow<Int>

  /**
   * Counts active subscription customers.
   */
  @Query("SELECT COUNT(*) FROM customers WHERE subscription_status = 'ACTIVE'")
  fun getActiveSubscriptionCount(): Flow<Int>

  // ==========================================
  // UPDATE operations
  // ==========================================

  /**
   * Updates an existing customer record.
   */
  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  /**
   * Updates only the subscription status for a specific customer.
   */
  @Query("UPDATE customers SET subscription_status = :newStatus WHERE id = :id")
  suspend fun updateSubscriptionStatus(id: Long, newStatus: String)

  /**
   * Updates assigned tiffin box number for a customer.
   */
  @Query("UPDATE customers SET assigned_box_no = :boxNo WHERE id = :id")
  suspend fun updateAssignedBoxNo(id: Long, boxNo: String)

  // ==========================================
  // DELETE operations
  // ==========================================

  /**
   * Deletes a specific customer.
   */
  @Delete
  suspend fun deleteCustomer(customer: CustomerEntity)

  /**
   * Deletes a customer by their primary key ID.
   */
  @Query("DELETE FROM customers WHERE id = :id")
  suspend fun deleteCustomerById(id: Long)

  /**
   * Deletes all customer records (useful for testing or full reset).
   */
  @Query("DELETE FROM customers")
  suspend fun deleteAllCustomers()
}
