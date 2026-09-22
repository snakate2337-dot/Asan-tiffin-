package com.example.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoxTrackingStatus
import com.example.model.CustomerBoxAssignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerBoxRegistryScreen(
  customerAssignments: MutableList<CustomerBoxAssignment>,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var showAddCustomerDialog by remember { mutableStateOf(false) }
  var editingCustomer by remember { mutableStateOf<CustomerBoxAssignment?>(null) }

  // Unique box numbers set for fast lookup
  val existingBoxNumbers by remember(customerAssignments.toList()) {
    derivedStateOf { customerAssignments.map { it.assignedBoxNo.trim().uppercase() }.toSet() }
  }

  // Filtered customer list
  val filteredCustomers by remember(searchQuery, customerAssignments.toList()) {
    derivedStateOf {
      if (searchQuery.isBlank()) {
        customerAssignments
      } else {
        customerAssignments.filter {
          it.customerName.contains(searchQuery, ignoreCase = true) ||
              it.assignedBoxNo.contains(searchQuery, ignoreCase = true) ||
              it.area.contains(searchQuery, ignoreCase = true) ||
              it.phone.contains(searchQuery)
        }
      }
    }
  }

  val totalAssignedBoxes = customerAssignments.map { it.assignedBoxNo }.distinct().size

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("customer_box_registry_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header
      item {
        Column {
          Text(
            text = "ग्राहकांसाठी कायमस्वरूपी डबा वाटप",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "👥 ग्राहक युनिक डबा नोंदणी",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      // Information Card explaining unique box allocation
      item {
        ElevatedCard(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("💡", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "प्रत्येक ॲक्टिव्ह ग्राहकाचा युनिक डबा क्रमांक",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "एकूण $totalAssignedBoxes युनिक डबे ॲक्टिव्ह ग्राहकांना वाटप केले आहेत. यामुळे डबे एकमेकांत मिसळत नाहीत व डब्यांचे नुकसान टळते.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ग्राहकाचे नाव, डबा क्र. (उदा. T-101) किंवा परिसर शोधा...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_customer_registry"),
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
      }

      // Customers List
      if (filteredCustomers.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("कोणताही ग्राहक आढळला नाही", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      } else {
        items(filteredCustomers, key = { it.customerId }) { customer ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("customer_registry_card_${customer.assignedBoxNo}"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primary,
                  contentColor = Color.White
                ) {
                  Text(
                    text = customer.assignedBoxNo,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = customer.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Text(
                    text = "📞 ${customer.phone} • 📍 ${customer.area}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  )
                  Text(
                    text = if (customer.trackingStatus == BoxTrackingStatus.IN_KITCHEN) {
                      "📥 सद्यस्थिती: किचनमध्ये सुरक्षित जमा"
                    } else {
                      "🚴 सद्यस्थिती: ग्राहकाकडे (${customer.daysOutCount} दिवस)"
                    },
                    fontSize = 11.sp,
                    color = if (customer.trackingStatus == BoxTrackingStatus.IN_KITCHEN) Color(0xFF2E7D32) else Color(0xFFE65100),
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              OutlinedButton(
                onClick = { editingCustomer = customer },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("edit_customer_box_btn_${customer.assignedBoxNo}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("डबा बदला", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // Floating Button to Add New Customer with Unique Box Number
    ExtendedFloatingActionButton(
      onClick = { showAddCustomerDialog = true },
      icon = { Icon(Icons.Default.Add, contentDescription = "Add Customer") },
      text = { Text("नवीन ग्राहक डबा नोंदवा", fontWeight = FontWeight.Bold) },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("add_customer_fab")
    )
  }

  // Dialog: Add New Customer with Unique Box Assignment
  if (showAddCustomerDialog) {
    // Generate next unique box number suggestion
    val nextBoxNumber = remember(customerAssignments.size) {
      val maxNum = customerAssignments.mapNotNull {
        it.assignedBoxNo.removePrefix("T-").toIntOrNull()
      }.maxOrNull() ?: 100
      "T-${maxNum + 1}"
    }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("स्टेशन रोड (Station Road)") }
    var boxNumber by remember { mutableStateOf(nextBoxNumber) }
    var notes by remember { mutableStateOf("") }

    val pandharpurAreas = listOf(
      "स्टेशन रोड (Station Road)",
      "इसबावी (Isbavi)",
      "नवी पेठ (Navi Peth)",
      "शिवाजी चौक (Shivaji Chowk)",
      "लिंक रोड (Link Road)",
      "ताकपीठ गल्ली (Takpith Galli)",
      "महाद्वार घाट (Mahadwar Ghat)",
      "वाखरी नाका (Wakhari Naka)",
      "सांगोला रोड (Sangola Road)",
      "कुंभार गल्ली (Kumbhar Galli)"
    )
    var isAreaDropdownExpanded by remember { mutableStateOf(false) }

    val isBoxDuplicate = customerAssignments.any { it.assignedBoxNo.equals(boxNumber.trim(), ignoreCase = true) }

    AlertDialog(
      onDismissRequest = { showAddCustomerDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Person, contentDescription = "Add")
          Spacer(modifier = Modifier.width(8.dp))
          Text("➕ नवीन ग्राहक युनिक डबा वाटप", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("ग्राहकाचे नाव (Customer Name)*") },
            placeholder = { Text("उदा. सागर मोरे") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_customer_name_input")
          )

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("मोबाईल नंबर (Phone)*") },
            placeholder = { Text("उदा. 9822112233") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_customer_phone_input")
          )

          // Area Dropdown
          ExposedDropdownMenuBox(
            expanded = isAreaDropdownExpanded,
            onExpandedChange = { isAreaDropdownExpanded = !isAreaDropdownExpanded }
          ) {
            OutlinedTextField(
              value = area,
              onValueChange = {},
              readOnly = true,
              label = { Text("डिलिव्हरी परिसर (Area)*") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAreaDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = isAreaDropdownExpanded,
              onDismissRequest = { isAreaDropdownExpanded = false }
            ) {
              pandharpurAreas.forEach { itemArea ->
                DropdownMenuItem(
                  text = { Text(itemArea) },
                  onClick = {
                    area = itemArea
                    isAreaDropdownExpanded = false
                  }
                )
              }
            }
          }

          // Unique Tiffin Box Number Assignment
          OutlinedTextField(
            value = boxNumber,
            onValueChange = { boxNumber = it },
            label = { Text("वाटप करावयाचा युनिक डबा क्र.*") },
            placeholder = { Text("उदा. T-109") },
            singleLine = true,
            isError = isBoxDuplicate,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_customer_box_input")
          )

          if (isBoxDuplicate) {
            Text(
              text = "⚠️ हा डबा क्रमांक आधीच एका ग्राहकाला दिला आहे! कृपया दुसरा युनिक क्रमांक निवडा.",
              color = MaterialTheme.colorScheme.error,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          } else {
            Text(
              text = "✅ हा युनिक डबा क्रमांक उपलब्ध आहे.",
              color = Color(0xFF2E7D32),
              fontSize = 11.sp
            )
          }

          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("शेरा / टीप (Notes)") },
            placeholder = { Text("उदा. रोज दुपारी १ वाजता हवा") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank() && boxNumber.isNotBlank() && !isBoxDuplicate) {
              val newCustomer = CustomerBoxAssignment(
                customerId = "cust_${System.currentTimeMillis()}",
                customerName = name.trim(),
                phone = phone.trim().ifBlank { "9999999999" },
                area = area,
                assignedBoxNo = boxNumber.trim().uppercase(),
                trackingStatus = BoxTrackingStatus.IN_KITCHEN,
                lastCheckOutTime = "-",
                lastCheckInTime = "आजच वाटप केले",
                daysOutCount = 0,
                notes = notes.trim()
              )
              customerAssignments.add(newCustomer)
              showAddCustomerDialog = false
            }
          },
          enabled = name.isNotBlank() && boxNumber.isNotBlank() && !isBoxDuplicate,
          modifier = Modifier.testTag("confirm_add_customer_box_btn")
        ) {
          Text("युनिक डबा वाटप करा", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCustomerDialog = false }) {
          Text("रद्द करा")
        }
      }
    )
  }

  // Dialog: Edit / Reassign Unique Box Number
  editingCustomer?.let { customer ->
    var editedBoxNo by remember(customer.assignedBoxNo) { mutableStateOf(customer.assignedBoxNo) }
    val isDuplicate = customerAssignments.any {
      it.customerId != customer.customerId && it.assignedBoxNo.equals(editedBoxNo.trim(), ignoreCase = true)
    }

    AlertDialog(
      onDismissRequest = { editingCustomer = null },
      title = {
        Text("✏️ डबा क्रमांक बदला / दुरुस्त करा", fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "ग्राहक: ${customer.customerName} (${customer.area})",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          OutlinedTextField(
            value = editedBoxNo,
            onValueChange = { editedBoxNo = it },
            label = { Text("नवीन युनिक डबा क्र.*") },
            placeholder = { Text("उदा. T-101") },
            singleLine = true,
            isError = isDuplicate,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("edit_box_no_input")
          )
          if (isDuplicate) {
            Text(
              text = "⚠️ हा क्रमांक दुसऱ्या ग्राहकाकडे आधीच नोंदणीकृत आहे!",
              color = MaterialTheme.colorScheme.error,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedBoxNo.isNotBlank() && !isDuplicate) {
              customer.assignedBoxNo = editedBoxNo.trim().uppercase()
              editingCustomer = null
            }
          },
          enabled = editedBoxNo.isNotBlank() && !isDuplicate,
          modifier = Modifier.testTag("save_reassigned_box_btn")
        ) {
          Text("सेव्ह करा")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingCustomer = null }) {
          Text("रद्द करा")
        }
      }
    )
  }
}
