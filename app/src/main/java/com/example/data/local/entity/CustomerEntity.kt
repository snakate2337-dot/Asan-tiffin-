package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity representing a Customer in Asan Tiffin.
 *
 * @param id Unique identifier (auto-generated primary key).
 * @param name Customer's full name.
 * @param address Customer's delivery address / location in Pandharpur.
 * @param phoneNumber Contact phone number for delivery coordination & WhatsApp alerts.
 * @param subscriptionStatus Current subscription status: "ACTIVE", "PAUSED", "EXPIRED", or "TRIAL".
 * @param assignedBoxNo Unique tiffin box number assigned to this customer (e.g. "T-101").
 * @param area Delivery neighborhood / area in Pandharpur (e.g. "स्टेशन रोड", "इसबावी").
 * @param mealPreference Meal preference ("Lunch", "Dinner", or "Both").
 * @param notes Any special dietary preferences or delivery instructions.
 * @param createdAt Timestamp of when customer record was created.
 */
@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,

  @ColumnInfo(name = "name")
  val name: String,

  @ColumnInfo(name = "address")
  val address: String,

  @ColumnInfo(name = "phone_number")
  val phoneNumber: String,

  @ColumnInfo(name = "subscription_status")
  val subscriptionStatus: String = STATUS_ACTIVE,

  @ColumnInfo(name = "assigned_box_no")
  val assignedBoxNo: String = "",

  @ColumnInfo(name = "area")
  val area: String = "",

  @ColumnInfo(name = "meal_preference")
  val mealPreference: String = "Both",

  @ColumnInfo(name = "rotis_per_meal")
  val rotisPerMeal: Int = 3,

  @ColumnInfo(name = "notes")
  val notes: String = "",

  @ColumnInfo(name = "created_at")
  val createdAt: Long = System.currentTimeMillis()
) {
  companion object {
    const val STATUS_ACTIVE = "ACTIVE"
    const val STATUS_ON_LEAVE = "ON_LEAVE"
    const val STATUS_INACTIVE = "INACTIVE"
    const val STATUS_PAUSED = "PAUSED"
    const val STATUS_EXPIRED = "EXPIRED"
    const val STATUS_TRIAL = "TRIAL"
  }
}
