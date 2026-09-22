package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoxLogEntry
import com.example.model.BoxTrackingStatus
import com.example.model.CustomerBoxAssignment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TiffinCheckInOutScreen(
  customerAssignments: MutableList<CustomerBoxAssignment>,
  boxLogs: MutableList<BoxLogEntry>,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf("सर्व डबे (All)") }
  var quickBoxInput by remember { mutableStateOf("") }
  var showQuickActionConfirmation by remember { mutableStateOf<Pair<CustomerBoxAssignment, String>?>(null) }
  var showHistoryDialog by remember { mutableStateOf(false) }

  val currentTime = remember {
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
  }

  // Derived statistics
  val totalBoxesCount = customerAssignments.size
  val inKitchenCount = customerAssignments.count { it.trackingStatus == BoxTrackingStatus.IN_KITCHEN }
  val checkedOutCount = customerAssignments.count { it.trackingStatus == BoxTrackingStatus.CHECKED_OUT }
  val overdueCount = customerAssignments.count { it.trackingStatus == BoxTrackingStatus.OVERDUE || it.daysOutCount >= 2 }

  // Filtered customer box list
  val filteredList by remember(searchQuery, selectedStatusFilter, customerAssignments.toList()) {
    derivedStateOf {
      customerAssignments.filter { assignment ->
        val matchesFilter = when (selectedStatusFilter) {
          "📥 किचनमध्ये (In Kitchen)" -> assignment.trackingStatus == BoxTrackingStatus.IN_KITCHEN
          "🚴 ग्राहकाकडे (Checked-Out)" -> assignment.trackingStatus == BoxTrackingStatus.CHECKED_OUT
          "⚠️ गहाळ धोका (Overdue)" -> assignment.trackingStatus == BoxTrackingStatus.OVERDUE || assignment.daysOutCount >= 2
          else -> true
        }

        val matchesSearch = if (searchQuery.isBlank()) {
          true
        } else {
          assignment.assignedBoxNo.contains(searchQuery, ignoreCase = true) ||
              assignment.customerName.contains(searchQuery, ignoreCase = true) ||
              assignment.area.contains(searchQuery, ignoreCase = true) ||
              assignment.phone.contains(searchQuery)
        }

        matchesFilter && matchesSearch
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("tiffin_checkin_checkout_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Title & Loss Prevention Subheading
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "डबा चोरी व गहाळ प्रतिबंध",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "🍱 चेक-इन / चेक-आऊट ट्रॅकिंग",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold
            )
          }

          OutlinedButton(
            onClick = { showHistoryDialog = true },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("view_logs_button")
          ) {
            Icon(Icons.Default.History, contentDescription = "Log", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("इतिहास (${boxLogs.size})", fontSize = 12.sp)
          }
        }
      }
    }

    // Summary Metric Dashboard
    item {
      ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CheckInOutMetricItem(
            label = "एकूण डबे",
            value = "$totalBoxesCount",
            color = MaterialTheme.colorScheme.primary,
            icon = "🍱"
          )
          CheckInOutMetricItem(
            label = "किचनमध्ये",
            value = "$inKitchenCount",
            color = Color(0xFF2E7D32),
            icon = "📥"
          )
          CheckInOutMetricItem(
            label = "ग्राहकाकडे",
            value = "$checkedOutCount",
            color = Color(0xFFE65100),
            icon = "🚴"
          )
          CheckInOutMetricItem(
            label = "गहाळ धोका",
            value = "$overdueCount",
            color = Color(0xFFC62828),
            icon = "⚠️"
          )
        }
      }
    }

    // Quick Action Bar: Rapid Check-In / Check-Out by typing/scanning Box Number
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "⚡ जलद डबा चेक-इन / चेक-आऊट (Quick Scan & Action)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = quickBoxInput,
              onValueChange = { quickBoxInput = it },
              placeholder = { Text("डबा क्र. (उदा. T-101 किंवा 101)...", fontSize = 12.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("quick_box_input"),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
              shape = RoundedCornerShape(8.dp)
            )

            // Rapid Check-In Button
            Button(
              onClick = {
                val inputQuery = quickBoxInput.trim()
                val target = customerAssignments.firstOrNull {
                  it.assignedBoxNo.equals(inputQuery, ignoreCase = true) ||
                      it.assignedBoxNo.equals("T-$inputQuery", ignoreCase = true)
                }
                if (target != null) {
                  target.trackingStatus = BoxTrackingStatus.IN_KITCHEN
                  target.lastCheckInTime = "आज $currentTime"
                  target.daysOutCount = 0
                  boxLogs.add(
                    0,
                    BoxLogEntry(
                      id = "log_${System.currentTimeMillis()}",
                      boxNo = target.assignedBoxNo,
                      customerName = target.customerName,
                      action = "CHECK_IN",
                      timestamp = "आज $currentTime",
                      remarks = "किचनमध्ये जमा"
                    )
                  )
                  quickBoxInput = ""
                }
              },
              enabled = quickBoxInput.isNotBlank(),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("quick_checkin_btn")
            ) {
              Text("📥 चेक-इन", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Rapid Check-Out Button
            Button(
              onClick = {
                val inputQuery = quickBoxInput.trim()
                val target = customerAssignments.firstOrNull {
                  it.assignedBoxNo.equals(inputQuery, ignoreCase = true) ||
                      it.assignedBoxNo.equals("T-$inputQuery", ignoreCase = true)
                }
                if (target != null) {
                  target.trackingStatus = BoxTrackingStatus.CHECKED_OUT
                  target.lastCheckOutTime = "आज $currentTime"
                  target.daysOutCount = 1
                  boxLogs.add(
                    0,
                    BoxLogEntry(
                      id = "log_${System.currentTimeMillis()}",
                      boxNo = target.assignedBoxNo,
                      customerName = target.customerName,
                      action = "CHECK_OUT",
                      timestamp = "आज $currentTime",
                      remarks = "डिलिव्हरीसाठी बाहेर पडला"
                    )
                  )
                  quickBoxInput = ""
                }
              },
              enabled = quickBoxInput.isNotBlank(),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("quick_checkout_btn")
            ) {
              Text("🚴 चेक-आऊट", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // High Risk Overdue Alert Box (if any)
    if (overdueCount > 0) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("loss_prevention_alert_card"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
          border = BorderStroke(1.dp, Color(0xFFEF5350))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Warning,
                contentDescription = "Alert",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "⚠️ टिफिन गहाळ होण्याचा धोका ($overdueCount डबे २+ दिवस बाकी!)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFC62828)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "खालील ग्राहकांकडे २ किंवा अधिक दिवसांपासून आसन टिफिनचा डबा परत आलेला नाही. एका क्लिकवर व्हॉट्सॲप किंवा कॉल करून डबा परत मागवा:",
              fontSize = 12.sp,
              color = Color(0xFF5D4037)
            )
          }
        }
      }
    }

    // Search and Filter Bar
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("डबा क्र., ग्राहकाचे नाव किंवा परिसर शोधा...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_tiffin_box_input"),
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )

        // Status Filter Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "सर्व डबे (All)",
            "📥 किचनमध्ये (In Kitchen)",
            "🚴 ग्राहकाकडे (Checked-Out)",
            "⚠️ गहाळ धोका (Overdue)"
          ).forEach { chipLabel ->
            FilterChip(
              selected = selectedStatusFilter == chipLabel,
              onClick = { selectedStatusFilter = chipLabel },
              label = { Text(chipLabel, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors()
            )
          }
        }
      }
    }

    // List of Customer Box Assignments
    if (filteredList.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("कोणताही डबा आढळला नाही", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    } else {
      items(filteredList, key = { it.customerId }) { assignment ->
        CustomerBoxCard(
          assignment = assignment,
          onCheckIn = {
            assignment.trackingStatus = BoxTrackingStatus.IN_KITCHEN
            assignment.lastCheckInTime = "आज $currentTime"
            assignment.daysOutCount = 0
            boxLogs.add(
              0,
              BoxLogEntry(
                id = "log_${System.currentTimeMillis()}",
                boxNo = assignment.assignedBoxNo,
                customerName = assignment.customerName,
                action = "CHECK_IN",
                timestamp = "आज $currentTime",
                remarks = "किचनमध्ये सुरक्षित जमा"
              )
            )
          },
          onCheckOut = {
            assignment.trackingStatus = BoxTrackingStatus.CHECKED_OUT
            assignment.lastCheckOutTime = "आज $currentTime"
            assignment.daysOutCount = 1
            boxLogs.add(
              0,
              BoxLogEntry(
                id = "log_${System.currentTimeMillis()}",
                boxNo = assignment.assignedBoxNo,
                customerName = assignment.customerName,
                action = "CHECK_OUT",
                timestamp = "आज $currentTime",
                remarks = "डिलिव्हरीसाठी डिस्पॅच केला"
              )
            )
          },
          onSendWhatsAppReminder = {
            val message = "नमस्कार ${assignment.customerName} जी, आसन टिफिन (पंढरपूर) कडून विनंती: आपला टिफिन डबा #${assignment.assignedBoxNo} कालपासून आपल्याकडे जमा आहे. टिफिन सेवा सुरळीत चालण्यासाठी कृपया रिकामे डबे आज डिलिव्हरी कर्मचाऱ्याकडे जमा करावेत. धन्यवाद!"
            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://api.whatsapp.com/send?phone=91${assignment.phone}&text=${Uri.encode(message)}")
            }
            try {
              context.startActivity(intent)
            } catch (_: Exception) {
              val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${assignment.phone}"))
              context.startActivity(dialIntent)
            }
          },
          onCallCustomer = {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${assignment.phone}"))
            context.startActivity(dialIntent)
          }
        )
      }
    }
  }

  // Audit Log Dialog
  if (showHistoryDialog) {
    AlertDialog(
      onDismissRequest = { showHistoryDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = "History")
          Spacer(modifier = Modifier.width(8.dp))
          Text("📜 चेक-इन / आऊट इतिहास", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (boxLogs.isEmpty()) {
            Text("अद्याप कोणतीही नोंद नाही.")
          } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              items(boxLogs) { log ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = if (log.action == "CHECK_IN") "📥 चेक-इन" else "🚴 चेक-आऊट",
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp,
                          color = if (log.action == "CHECK_IN") Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                          Text(
                            text = log.boxNo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                          )
                        }
                      }
                      Text(
                        text = "${log.customerName} • ${log.remarks}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Text(
                      text = log.timestamp,
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.outline
                    )
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(onClick = { showHistoryDialog = false }) {
          Text("बंद करा")
        }
      }
    )
  }
}

@Composable
fun CustomerBoxCard(
  assignment: CustomerBoxAssignment,
  onCheckIn: () -> Unit,
  onCheckOut: () -> Unit,
  onSendWhatsAppReminder: () -> Unit,
  onCallCustomer: () -> Unit
) {
  val isOverdue = assignment.trackingStatus == BoxTrackingStatus.OVERDUE || assignment.daysOutCount >= 2
  val isInKitchen = assignment.trackingStatus == BoxTrackingStatus.IN_KITCHEN

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("customer_box_card_${assignment.assignedBoxNo}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isOverdue) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      1.dp,
      if (isOverdue) Color(0xFFFFB74D) else if (isInKitchen) Color(0xFFA5D6A7) else MaterialTheme.colorScheme.outlineVariant
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top Row: Box Number Badge, Customer Name & Area, Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          // Unique Box Number Tag
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
          ) {
            Text(
              text = assignment.assignedBoxNo,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = assignment.customerName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "📍 ${assignment.area}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }
        }

        // Tracking Status Badge
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isOverdue) Color(0xFFFFCDD2) else if (isInKitchen) Color(0xFFC8E6C9) else Color(0xFFFFE0B2),
          contentColor = if (isOverdue) Color(0xFFB71C1C) else if (isInKitchen) Color(0xFF1B5E20) else Color(0xFFE65100)
        ) {
          Text(
            text = if (isOverdue) "⚠️ धोका (${assignment.daysOutCount} दिवस)" else if (isInKitchen) "📥 किचनमध्ये जमा" else "🚴 ग्राहकाकडे",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Status Detail: Last Check-out / Check-in info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isInKitchen) {
          Text(
            text = "✅ डबा किचनमध्ये सुरक्षित जमा आहे. पुढील डिलिव्हरीसाठी तयार.",
            fontSize = 11.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Medium
          )
        } else {
          Text(
            text = "🚴 बाहेर पडल्याची वेळ: ${assignment.lastCheckOutTime} (${assignment.daysOutCount} दिवस झाले)",
            fontSize = 11.sp,
            color = if (isOverdue) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Quick Call & WhatsApp reminders for customer
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onCallCustomer,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
          }

          if (!isInKitchen) {
            FilledTonalButton(
              onClick = onSendWhatsAppReminder,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              shape = RoundedCornerShape(6.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
              ),
              modifier = Modifier.testTag("whatsapp_reminder_btn_${assignment.assignedBoxNo}")
            ) {
              Icon(Icons.Default.Send, contentDescription = "WhatsApp", modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("डबा आठवण", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Primary State Transition Button: Check-In vs Check-Out
        if (!isInKitchen) {
          Button(
            onClick = onCheckIn,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            modifier = Modifier.testTag("checkin_btn_${assignment.assignedBoxNo}")
          ) {
            Text("📥 किचनमध्ये जमा (Check-In)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onCheckOut,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
            modifier = Modifier.testTag("checkout_btn_${assignment.assignedBoxNo}")
          ) {
            Text("🚴 ग्राहकाला द्या (Check-Out)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun CheckInOutMetricItem(
  label: String,
  value: String,
  color: Color,
  icon: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(icon, fontSize = 16.sp)
    Text(
      text = value,
      fontSize = 18.sp,
      fontWeight = FontWeight.ExtraBold,
      color = color
    )
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
