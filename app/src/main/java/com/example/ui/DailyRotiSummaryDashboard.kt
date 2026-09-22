package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyRotiSummary
import com.example.model.SubscriptionStatus
import com.example.model.TiffinCustomerProfile

/**
 * Summary Dashboard Component that calculates daily total rotis required.
 * Filters only active users and strictly excludes those marked as On Leave or Inactive/Paused.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyRotiSummaryDashboard(
  customerProfiles: MutableList<TiffinCustomerProfile>,
  modifier: Modifier = Modifier,
  onStatusChange: (TiffinCustomerProfile, SubscriptionStatus, String) -> Unit = { _, _, _ -> }
) {
  var selectedMealFilter by remember { mutableStateOf("सर्व (All)") }
  var searchQuery by remember { mutableStateOf("") }
  var activeTabFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE_ONLY", "ON_LEAVE_ONLY", "INACTIVE_ONLY"
  var editingCustomerStatus by remember { mutableStateOf<TiffinCustomerProfile?>(null) }
  var isExpandedBreakdown by remember { mutableStateOf(true) }

  // Calculation Logic: Active users included, On Leave & Inactive strictly excluded
  val dailySummary by remember(customerProfiles.toList(), selectedMealFilter) {
    derivedStateOf {
      val activeCustomers = customerProfiles.filter { it.status == SubscriptionStatus.ACTIVE }
      val onLeaveCustomers = customerProfiles.filter { it.status == SubscriptionStatus.ON_LEAVE }
      val inactiveCustomers = customerProfiles.filter {
        it.status == SubscriptionStatus.INACTIVE || it.status == SubscriptionStatus.PAUSED
      }

      val filterMealParam = when (selectedMealFilter) {
        "दुपार (Lunch)" -> "दुपार (Lunch)"
        "रात्र (Dinner)" -> "रात्र (Dinner)"
        else -> "All"
      }

      val totalRotis = activeCustomers.sumOf { it.calculateRotisForMeal(filterMealParam) }

      val lunchRotis = activeCustomers.sumOf { it.calculateRotisForMeal("दुपार (Lunch)") }
      val dinnerRotis = activeCustomers.sumOf { it.calculateRotisForMeal("रात्र (Dinner)") }

      DailyRotiSummary(
        totalRotis = totalRotis,
        lunchRotis = lunchRotis,
        dinnerRotis = dinnerRotis,
        activeCustomerCount = activeCustomers.size,
        onLeaveCustomerCount = onLeaveCustomers.size,
        inactiveCustomerCount = inactiveCustomers.size,
        totalRegisteredCustomers = customerProfiles.size
      )
    }
  }

  // Filtered customer list for display
  val displayedCustomers by remember(customerProfiles.toList(), searchQuery, activeTabFilter, selectedMealFilter) {
    derivedStateOf {
      customerProfiles.filter { customer ->
        val matchesSearch = searchQuery.isBlank() ||
            customer.name.contains(searchQuery, ignoreCase = true) ||
            customer.area.contains(searchQuery, ignoreCase = true) ||
            customer.assignedBoxNo.contains(searchQuery, ignoreCase = true) ||
            customer.phone.contains(searchQuery)

        val matchesTab = when (activeTabFilter) {
          "ACTIVE_ONLY" -> customer.status == SubscriptionStatus.ACTIVE
          "ON_LEAVE_ONLY" -> customer.status == SubscriptionStatus.ON_LEAVE
          "INACTIVE_ONLY" -> customer.status == SubscriptionStatus.INACTIVE || customer.status == SubscriptionStatus.PAUSED
          else -> true
        }

        val matchesMeal = when (selectedMealFilter) {
          "दुपार (Lunch)" -> customer.mealTime.contains("दुपार") || customer.mealTime.contains("दोन्ही")
          "रात्र (Dinner)" -> customer.mealTime.contains("रात्र") || customer.mealTime.contains("दोन्ही")
          else -> true
        }

        matchesSearch && matchesTab && matchesMeal
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("roti_summary_dashboard"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Dashboard Header
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "किचन नियोजन • पोळी अंदाजपत्रक",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "🫓 आजच्या एकूण पोळ्यांची गरज",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f))
          ) {
            Text(
              text = "✅ अचूक गणना",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF2E7D32)
            )
          }
        }
      }
    }

    // 2. Primary Summary Hero Card (Total Rotis Count)
    item {
      ElevatedCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("roti_total_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
              ) {
                Text("🫓", fontSize = 24.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "आज एकूण पोळ्या (Daily Required)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                  text = "${dailySummary.totalRotis} पोळ्या",
                  fontSize = 32.sp,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }

            // Flour / Dough requirement estimator (Pandharpur kitchen rule: ~35-40 rotis per kg flour)
            val approxFlourKg = String.format("%.1f", dailySummary.totalRotis / 35.0)
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("गहू पीठ अंदाज", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("~$approxFlourKg किलो", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
          Spacer(modifier = Modifier.height(12.dp))

          // Lunch vs Dinner Breakdown
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            RotiSessionItem(
              icon = "☀️",
              title = "दुपार (Lunch)",
              rotis = dailySummary.lunchRotis,
              modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            RotiSessionItem(
              icon = "🌙",
              title = "रात्र (Dinner)",
              rotis = dailySummary.dinnerRotis,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 3. User Filtering Breakdown (Active vs On Leave vs Inactive Status Cards)
    item {
      ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("📊", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ग्राहक स्थिती वर्गीकरण (Filter Status)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
            IconButton(
              onClick = { isExpandedBreakdown = !isExpandedBreakdown },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = if (isExpandedBreakdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Expand"
              )
            }
          }

          AnimatedVisibility(visible = isExpandedBreakdown) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              Text(
                text = "💡 केवळ सक्रिय ग्राहकांच्या पोळ्या मोजल्या जातात. रजेवरील किंवा बंद ग्राहकांच्या पोळ्या आपोआप वगळल्या आहेत.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Active Card
                StatusCountCard(
                  label = "सक्रिय ग्राहक",
                  count = dailySummary.activeCustomerCount,
                  badge = "समाविष्ट",
                  color = Color(0xFF2E7D32),
                  isSelected = activeTabFilter == "ACTIVE_ONLY",
                  onClick = {
                    activeTabFilter = if (activeTabFilter == "ACTIVE_ONLY") "ALL" else "ACTIVE_ONLY"
                  },
                  modifier = Modifier.weight(1f)
                )

                // On Leave Card
                StatusCountCard(
                  label = "रजेवर (Leave)",
                  count = dailySummary.onLeaveCustomerCount,
                  badge = "वगळले",
                  color = Color(0xFFF57C00),
                  isSelected = activeTabFilter == "ON_LEAVE_ONLY",
                  onClick = {
                    activeTabFilter = if (activeTabFilter == "ON_LEAVE_ONLY") "ALL" else "ON_LEAVE_ONLY"
                  },
                  modifier = Modifier.weight(1f)
                )

                // Inactive Card
                StatusCountCard(
                  label = "बंद / निष्क्रीय",
                  count = dailySummary.inactiveCustomerCount,
                  badge = "वगळले",
                  color = Color(0xFF757575),
                  isSelected = activeTabFilter == "INACTIVE_ONLY",
                  onClick = {
                    activeTabFilter = if (activeTabFilter == "INACTIVE_ONLY") "ALL" else "INACTIVE_ONLY"
                  },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    }

    // 4. Meal Session Filter Row (All, Lunch, Dinner)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "सत्र निवडा (Select Meal Session):",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("सर्व (All)", "दुपार (Lunch)", "रात्र (Dinner)").forEach { mealFilter ->
            FilterChip(
              selected = selectedMealFilter == mealFilter,
              onClick = { selectedMealFilter = mealFilter },
              label = { Text(mealFilter, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors()
            )
          }
        }
      }
    }

    // 5. Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_roti_customer_input"),
        placeholder = { Text("ग्राहकाचे नाव, परिसर किंवा डबा क्र. शोधा...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
      )
    }

    // Customer Roster Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ग्राहक यादी व पोळी वाटप (${displayedCustomers.size})",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )

        if (activeTabFilter != "ALL") {
          TextButton(onClick = { activeTabFilter = "ALL" }) {
            Text("सर्व पहा (Show All)", fontSize = 11.sp)
          }
        }
      }
    }

    // 6. Customer List Items with Quick Leave / Active Status Toggle
    items(displayedCustomers, key = { it.id }) { customer ->
      CustomerRotiItemCard(
        customer = customer,
        selectedMealFilter = selectedMealFilter,
        onStatusClick = { editingCustomerStatus = customer },
        onQuickToggleLeave = {
          val newStatus = if (customer.status == SubscriptionStatus.ON_LEAVE) {
            SubscriptionStatus.ACTIVE
          } else {
            SubscriptionStatus.ON_LEAVE
          }
          val reason = if (newStatus == SubscriptionStatus.ON_LEAVE) "आज सुट्टी कळवली" else ""
          customer.status = newStatus
          customer.leaveReason = reason
          onStatusChange(customer, newStatus, reason)
        }
      )
    }
  }

  // Status Change Dialog (Active, On Leave, Inactive)
  editingCustomerStatus?.let { customer ->
    var currentStatusSelection by remember { mutableStateOf(customer.status) }
    var leaveNote by remember { mutableStateOf(customer.leaveReason) }

    AlertDialog(
      onDismissRequest = { editingCustomerStatus = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("📝 ग्राहक स्थिती बदला", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "${customer.name} (डबा: ${customer.assignedBoxNo})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "पोळ्या प्रमाण: ${customer.defaultRotisPerMeal} पोळ्या/वेळ (${customer.mealTime})",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text("नवीन स्थिती निवडा:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

          SubscriptionStatus.entries.forEach { statusOption ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (currentStatusSelection == statusOption) {
                statusOption.color.copy(alpha = 0.15f)
              } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              },
              border = BorderStroke(
                width = if (currentStatusSelection == statusOption) 2.dp else 1.dp,
                color = if (currentStatusSelection == statusOption) statusOption.color else Color.Transparent
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { currentStatusSelection = statusOption }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = statusOption.marathiLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = statusOption.color
                  )
                  Text(
                    text = if (statusOption.countsForDailyKitchenPrep) "✅ पोळ्या मोजल्या जातील" else "❌ पोळ्या वगळल्या जातील",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                if (currentStatusSelection == statusOption) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = statusOption.color,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }

          if (currentStatusSelection == SubscriptionStatus.ON_LEAVE) {
            OutlinedTextField(
              value = leaveNote,
              onValueChange = { leaveNote = it },
              label = { Text("रजेचे कारण / तारीख (Leave Note)") },
              placeholder = { Text("उदा. आज गावी गेले आहेत") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            customer.status = currentStatusSelection
            customer.leaveReason = leaveNote
            onStatusChange(customer, currentStatusSelection, leaveNote)
            editingCustomerStatus = null
          }
        ) {
          Text("जतन करा (Save)", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { editingCustomerStatus = null }) {
          Text("रद्द करा")
        }
      }
    )
  }
}

@Composable
private fun RotiSessionItem(
  icon: String,
  title: String,
  rotis: Int,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(icon, fontSize = 20.sp)
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$rotis पोळ्या", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
      }
    }
  }
}

@Composable
private fun StatusCountCard(
  label: String,
  count: Int,
  badge: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) color else Color.Transparent
    ),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = color
      )
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(2.dp))
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
      ) {
        Text(
          text = badge,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = color,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
      }
    }
  }
}

@Composable
private fun CustomerRotiItemCard(
  customer: TiffinCustomerProfile,
  selectedMealFilter: String,
  onStatusClick: () -> Unit,
  onQuickToggleLeave: () -> Unit
) {
  val calculatedRotis = customer.calculateRotisForMeal(
    when (selectedMealFilter) {
      "दुपार (Lunch)" -> "दुपार (Lunch)"
      "रात्र (Dinner)" -> "रात्र (Dinner)"
      else -> "All"
    }
  )

  ElevatedCard(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = if (customer.status == SubscriptionStatus.ACTIVE) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      }
    ),
    elevation = CardDefaults.elevatedCardElevation(
      defaultElevation = if (customer.status == SubscriptionStatus.ACTIVE) 2.dp else 0.dp
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Customer Details
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (customer.status == SubscriptionStatus.ACTIVE) {
            MaterialTheme.colorScheme.primary
          } else {
            customer.status.color
          },
          contentColor = Color.White
        ) {
          Text(
            text = customer.assignedBoxNo,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = customer.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "📍 ${customer.area} • ${customer.mealTime}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Status Badge with clickable trigger to edit
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onStatusClick() }
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = customer.status.color.copy(alpha = 0.15f),
              border = BorderStroke(0.5.dp, customer.status.color)
            ) {
              Text(
                text = customer.status.marathiLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = customer.status.color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            if (customer.status == SubscriptionStatus.ON_LEAVE && customer.leaveReason.isNotBlank()) {
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "(${customer.leaveReason})",
                fontSize = 10.sp,
                color = Color(0xFFF57C00),
                maxLines = 1
              )
            }
          }
        }
      }

      // Roti Count & Quick Toggle Action
      Column(
        horizontalAlignment = Alignment.End
      ) {
        if (customer.status == SubscriptionStatus.ACTIVE) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          ) {
            Text(
              text = "$calculatedRotis पोळ्या",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          TextButton(
            onClick = onQuickToggleLeave,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
            modifier = Modifier.height(26.dp)
          ) {
            Text("सुट्टी नोंदवा", fontSize = 10.sp, color = Color(0xFFF57C00))
          }
        } else {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEEEEEE)
          ) {
            Text(
              text = "० पोळ्या (वगळले)",
              fontSize = 11.sp,
              color = Color.Gray,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          TextButton(
            onClick = onQuickToggleLeave,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
            modifier = Modifier.height(26.dp)
          ) {
            Text("सक्रिय करा", fontSize = 10.sp, color = Color(0xFF2E7D32))
          }
        }
      }
    }
  }
}
