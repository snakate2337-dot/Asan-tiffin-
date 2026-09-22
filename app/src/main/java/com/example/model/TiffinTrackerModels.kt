package com.example.model

import androidx.compose.ui.graphics.Color

enum class DeliveryStatus(val label: String, val icon: String, val badgeColor: Color) {
  PENDING("प्रलंबित (Pending)", "⏳", Color(0xFFE65100)),
  COOKING("किचन (Cooking)", "🍳", Color(0xFF0288D1)),
  PACKED("पॅक झाले (Packed)", "📦", Color(0xFF7B1FA2)),
  DELIVERED("डिलिव्हर्ड (Delivered)", "✅", Color(0xFF2E7D32))
}

enum class BoxTrackingStatus(
  val label: String,
  val marathiLabel: String,
  val badgeColor: Color,
  val icon: String
) {
  IN_KITCHEN("In Kitchen", "किचनमध्ये जमा (In Kitchen)", Color(0xFF2E7D32), "📥"),
  CHECKED_OUT("Checked Out", "ग्राहकाकडे (Checked Out)", Color(0xFFE65100), "🚴"),
  OVERDUE("Overdue", "गहाळ धोका (Overdue - २+ दिवस)", Color(0xFFC62828), "⚠️")
}

data class CustomerBoxAssignment(
  val customerId: String,
  var customerName: String,
  var phone: String,
  var area: String,
  var assignedBoxNo: String,             // Unique permanent tiffin box number (e.g., T-101)
  var trackingStatus: BoxTrackingStatus = BoxTrackingStatus.CHECKED_OUT,
  var lastCheckOutTime: String = "आज सकाळी ११:३०",
  var lastCheckInTime: String = "",
  var daysOutCount: Int = 1,              // Days since box was checked out
  var notes: String = "",
  var totalLossAlerts: Int = 0
)

data class BoxLogEntry(
  val id: String,
  val boxNo: String,
  val customerName: String,
  val action: String, // "CHECK_IN" or "CHECK_OUT"
  val timestamp: String,
  val remarks: String = ""
)

data class TiffinDelivery(
  val id: String,
  val serial: Int,
  val customerName: String,
  val phone: String,
  val area: String,
  val mealType: String, // "दुपार (Lunch)", "रात्र (Dinner)", "दोन्ही (Both)"
  val plan: String,     // "Basic", "Standard", "Premium"
  val foodType: String, // "Veg", "Non-Veg"
  val rotis: Int,
  var status: DeliveryStatus,
  val notes: String = "",
  var deliveredBoxNo: String = "",    // आज दिलेला टिफिन डबा क्र. (उदा. T-101)
  var returnedBoxNo: String = "",     // परत आलेला / घ्यावयाचा डबा क्र. (उदा. T-94)
  var isBoxReturned: Boolean = false, // डबा परत जमा झाला का?
  var returnRemarks: String = ""      // शेरा / अडचण
)
