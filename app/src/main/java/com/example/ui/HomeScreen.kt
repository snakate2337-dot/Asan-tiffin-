package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.model.BoxLogEntry
import com.example.model.BoxTrackingStatus
import com.example.model.CustomerBoxAssignment
import com.example.model.DeliveryStatus
import com.example.model.SubscriptionStatus
import com.example.model.TiffinCustomerProfile
import com.example.model.TiffinDelivery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  onNavigateToOwner: () -> Unit = {},
  onNavigateToKitchen: () -> Unit = {}
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Sample initial deliveries for Pandharpur, Maharashtra
  val deliveries = remember {
    mutableStateListOf(
      TiffinDelivery(
        id = "ord_1",
        serial = 1,
        customerName = "राहुल कुलकर्णी (Rahul Kulkarni)",
        phone = "9822112233",
        area = "स्टेशन रोड (Station Road)",
        mealType = "दुपार (Lunch)",
        plan = "Standard",
        foodType = "Veg",
        rotis = 3,
        status = DeliveryStatus.PENDING,
        notes = "२ रस्सा भाजी हवी आहे",
        deliveredBoxNo = "T-101",
        returnedBoxNo = "T-94",
        isBoxReturned = false,
        returnRemarks = "कालचा डबा अद्याप मिळालेला नाही"
      ),
      TiffinDelivery(
        id = "ord_2",
        serial = 2,
        customerName = "सचिन गायकवाड (Sachin Gaikwad)",
        phone = "9422001122",
        area = "इसबावी (Isbavi)",
        mealType = "दुपार (Lunch)",
        plan = "Premium",
        foodType = "Non-Veg",
        rotis = 4,
        status = DeliveryStatus.COOKING,
        notes = "मटन रस्सा व भात",
        deliveredBoxNo = "T-102",
        returnedBoxNo = "T-88",
        isBoxReturned = true,
        returnRemarks = "डबा स्वच्छ परत मिळाला"
      ),
      TiffinDelivery(
        id = "ord_3",
        serial = 3,
        customerName = "अमित जोशी (Amit Joshi)",
        phone = "9890123456",
        area = "नवी पेठ (Navi Peth)",
        mealType = "दुपार (Lunch)",
        plan = "Standard",
        foodType = "Veg",
        rotis = 3,
        status = DeliveryStatus.PACKED,
        notes = "दुपारी १२:३० पर्यंत हवे",
        deliveredBoxNo = "T-103",
        returnedBoxNo = "T-72",
        isBoxReturned = true,
        returnRemarks = "डबा जमा झाला"
      ),
      TiffinDelivery(
        id = "ord_4",
        serial = 4,
        customerName = "डॉ. स्नेहा पाटील (Dr. Sneha Patil)",
        phone = "9823456789",
        area = "शिवाजी चौक (Shivaji Chowk)",
        mealType = "रात्र (Dinner)",
        plan = "Basic",
        foodType = "Veg",
        rotis = 2,
        status = DeliveryStatus.PENDING,
        notes = "कमी तिखट",
        deliveredBoxNo = "T-104",
        returnedBoxNo = "T-60",
        isBoxReturned = false,
        returnRemarks = "दुपारी क्लिनिकमध्ये डबा जमा करायचा आहे"
      ),
      TiffinDelivery(
        id = "ord_5",
        serial = 5,
        customerName = "विजय साळुंखे (Vijay Salunkhe)",
        phone = "9765432100",
        area = "ताकपीठ गल्ली (Takpith Galli)",
        mealType = "दुपार (Lunch)",
        plan = "Premium",
        foodType = "Non-Veg",
        rotis = 4,
        status = DeliveryStatus.PENDING,
        notes = "गरम भात",
        deliveredBoxNo = "T-105",
        returnedBoxNo = "T-53",
        isBoxReturned = false,
        returnRemarks = "डबा उद्या देणार असे सांगितले"
      ),
      TiffinDelivery(
        id = "ord_6",
        serial = 6,
        customerName = "प्रा. देशपांडे (Prof. Deshpande)",
        phone = "9421098765",
        area = "लिंक रोड (Link Road)",
        mealType = "दोन्ही (Both)",
        plan = "Standard",
        foodType = "Veg",
        rotis = 3,
        status = DeliveryStatus.PENDING,
        notes = "पहिला मजला, फ्लॅट ४",
        deliveredBoxNo = "T-106",
        returnedBoxNo = "T-41",
        isBoxReturned = true,
        returnRemarks = "डबा परत मिळाला"
      ),
      TiffinDelivery(
        id = "ord_7",
        serial = 7,
        customerName = "महेश शेटे (Mahesh Shete)",
        phone = "9850123456",
        area = "महाद्वार घाट (Mahadwar Ghat)",
        mealType = "दुपार (Lunch)",
        plan = "Basic",
        foodType = "Veg",
        rotis = 2,
        status = DeliveryStatus.PENDING,
        notes = "दुकान क्र. ३",
        deliveredBoxNo = "T-107",
        returnedBoxNo = "T-35",
        isBoxReturned = false,
        returnRemarks = "दुकानातून डबा घ्यायचा बाकी आहे"
      ),
      TiffinDelivery(
        id = "ord_8",
        serial = 8,
        customerName = "ओंकार शिंदे (Omkar Shinde)",
        phone = "9922334455",
        area = "वाखरी नाका (Wakhari Naka)",
        mealType = "रात्र (Dinner)",
        plan = "Standard",
        foodType = "Veg",
        rotis = 3,
        status = DeliveryStatus.PENDING,
        notes = "रात्री ८:०० वाजता",
        deliveredBoxNo = "T-108",
        returnedBoxNo = "T-22",
        isBoxReturned = false,
        returnRemarks = "कालचा डबा बाकी"
      )
    )
  }

  // Active customer unique assigned tiffin box registry
  val customerAssignments = remember {
    mutableStateListOf(
      CustomerBoxAssignment(
        customerId = "c1",
        customerName = "राहुल कुलकर्णी (Rahul Kulkarni)",
        phone = "9822112233",
        area = "स्टेशन रोड (Station Road)",
        assignedBoxNo = "T-101",
        trackingStatus = BoxTrackingStatus.OVERDUE,
        lastCheckOutTime = "काल सकाळी ११:३०",
        lastCheckInTime = "-",
        daysOutCount = 2,
        notes = "कालचा डबा अद्याप परत आलेला नाही"
      ),
      CustomerBoxAssignment(
        customerId = "c2",
        customerName = "सचिन गायकवाड (Sachin Gaikwad)",
        phone = "9422001122",
        area = "इसबावी (Isbavi)",
        assignedBoxNo = "T-102",
        trackingStatus = BoxTrackingStatus.CHECKED_OUT,
        lastCheckOutTime = "आज सकाळी ११:४५",
        lastCheckInTime = "काल दुपारी ०३:००",
        daysOutCount = 1,
        notes = "स्वच्छ डबा परत मिळतो"
      ),
      CustomerBoxAssignment(
        customerId = "c3",
        customerName = "अमित जोशी (Amit Joshi)",
        phone = "9890123456",
        area = "नवी पेठ (Navi Peth)",
        assignedBoxNo = "T-103",
        trackingStatus = BoxTrackingStatus.IN_KITCHEN,
        lastCheckOutTime = "काल सकाळी ११:००",
        lastCheckInTime = "आज सकाळी ०९:१५",
        daysOutCount = 0,
        notes = "डबा किचनमध्ये सुरक्षित जमा आहे"
      ),
      CustomerBoxAssignment(
        customerId = "c4",
        customerName = "सुनीता कदम (Sunita Kadam)",
        phone = "9823456789",
        area = "शिवाजी चौक (Shivaji Chowk)",
        assignedBoxNo = "T-104",
        trackingStatus = BoxTrackingStatus.IN_KITCHEN,
        lastCheckOutTime = "काल सकाळी ११:१५",
        lastCheckInTime = "आज सकाळी ०९:३०",
        daysOutCount = 0,
        notes = "डबा किचनमध्ये उपलब्ध"
      ),
      CustomerBoxAssignment(
        customerId = "c5",
        customerName = "विजय साळुंखे (Vijay Salunkhe)",
        phone = "9765432100",
        area = "ताकपीठ गल्ली (Takpith Galli)",
        assignedBoxNo = "T-105",
        trackingStatus = BoxTrackingStatus.OVERDUE,
        lastCheckOutTime = "२ दिवसांपूर्वी सकाळी ११:३०",
        lastCheckInTime = "-",
        daysOutCount = 2,
        notes = "२ दिवस झाले डबा परत आलेला नाही"
      ),
      CustomerBoxAssignment(
        customerId = "c6",
        customerName = "प्रा. देशपांडे (Prof. Deshpande)",
        phone = "9421098765",
        area = "लिंक रोड (Link Road)",
        assignedBoxNo = "T-106",
        trackingStatus = BoxTrackingStatus.IN_KITCHEN,
        lastCheckOutTime = "काल दुपारी १२:००",
        lastCheckInTime = "आज सकाळी १०:००",
        daysOutCount = 0,
        notes = "किचनमध्ये जमा"
      ),
      CustomerBoxAssignment(
        customerId = "c7",
        customerName = "महेश शेटे (Mahesh Shete)",
        phone = "9850123456",
        area = "महाद्वार घाट (Mahadwar Ghat)",
        assignedBoxNo = "T-107",
        trackingStatus = BoxTrackingStatus.CHECKED_OUT,
        lastCheckOutTime = "आज दुपारी १२:१५",
        lastCheckInTime = "काल दुपारी ०३:३०",
        daysOutCount = 1,
        notes = "दुकानातून डबा गोळा करणे"
      ),
      CustomerBoxAssignment(
        customerId = "c8",
        customerName = "ओंकार शिंदे (Omkar Shinde)",
        phone = "9922334455",
        area = "वाखरी नाका (Wakhari Naka)",
        assignedBoxNo = "T-108",
        trackingStatus = BoxTrackingStatus.CHECKED_OUT,
        lastCheckOutTime = "आज दुपारी १२:३०",
        lastCheckInTime = "काल संध्याकाळी ०४:००",
        daysOutCount = 1,
        notes = "डिलिव्हरी चालू"
      )
    )
  }

  // Audit Logs for Check-In / Check-Out
  val boxLogs = remember {
    mutableStateListOf(
      BoxLogEntry("l1", "T-103", "अमित जोशी", "CHECK_IN", "आज सकाळी ०९:१५", "रिकाम्या डब्याची तपासणी पूर्ण"),
      BoxLogEntry("l2", "T-104", "सुनीता कदम", "CHECK_IN", "आज सकाळी ०९:३०", "डबा स्वच्छ जमा"),
      BoxLogEntry("l3", "T-106", "प्रा. देशपांडे", "CHECK_IN", "आज सकाळी १०:००", "किचनमध्ये जमा"),
      BoxLogEntry("l4", "T-101", "राहुल कुलकर्णी", "CHECK_OUT", "काल सकाळी ११:३०", "दुपारच्या जेवणासाठी रवाना"),
      BoxLogEntry("l5", "T-105", "विजय साळुंखे", "CHECK_OUT", "२ दिवसांपूर्वी सकाळी ११:३०", "डिलिव्हरी बॉयने नेला")
    )
  }

  // Master Customer Registry with Subscription Status (Active, On Leave, Inactive)
  // and Roti requirement configurations for daily kitchen planning
  val customerProfiles = remember {
    mutableStateListOf(
      TiffinCustomerProfile(
        id = "c1",
        name = "राहुल कुलकर्णी (Rahul Kulkarni)",
        phone = "9822112233",
        area = "स्टेशन रोड (Station Road)",
        address = "फ्लॅट १०२, साई अपार्टमेंट",
        assignedBoxNo = "T-101",
        defaultRotisPerMeal = 3,
        mealTime = "दोन्ही (Both)",
        status = SubscriptionStatus.ACTIVE,
        notes = "नियमित दुपार व रात्र"
      ),
      TiffinCustomerProfile(
        id = "c2",
        name = "सचिन गायकवाड (Sachin Gaikwad)",
        phone = "9422001122",
        area = "इसबावी (Isbavi)",
        address = "प्लॉट १२, विठ्ठल नगर",
        assignedBoxNo = "T-102",
        defaultRotisPerMeal = 4,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.ACTIVE,
        notes = "जास्त भूक असते"
      ),
      TiffinCustomerProfile(
        id = "c3",
        name = "अमित जोशी (Amit Joshi)",
        phone = "9890123456",
        area = "नवी पेठ (Navi Peth)",
        address = "घर नं. ४५, पेठ गल्ली",
        assignedBoxNo = "T-103",
        defaultRotisPerMeal = 3,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.ON_LEAVE,
        leaveReason = "गावी गेले आहेत (Leave till tomorrow)",
        notes = "आज डबा नको"
      ),
      TiffinCustomerProfile(
        id = "c4",
        name = "सुनीता कदम (Sunita Kadam)",
        phone = "9823456789",
        area = "शिवाजी चौक (Shivaji Chowk)",
        address = "दुकान नं. ५ जवळ",
        assignedBoxNo = "T-104",
        defaultRotisPerMeal = 2,
        mealTime = "रात्र (Dinner)",
        status = SubscriptionStatus.ACTIVE,
        notes = "कमी तिखट, २ पोळ्या"
      ),
      TiffinCustomerProfile(
        id = "c5",
        name = "विजय साळुंखे (Vijay Salunkhe)",
        phone = "9765432100",
        area = "ताकपीठ गल्ली (Takpith Galli)",
        address = "साळुंखे निवास",
        assignedBoxNo = "T-105",
        defaultRotisPerMeal = 4,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.INACTIVE,
        leaveReason = "सबस्क्रिप्शन संपले (Expired)",
        notes = "नूतनीकरण बाकी"
      ),
      TiffinCustomerProfile(
        id = "c6",
        name = "प्रा. देशपांडे (Prof. Deshpande)",
        phone = "9421098765",
        area = "लिंक रोड (Link Road)",
        address = "पहिला मजला, फ्लॅट ४",
        assignedBoxNo = "T-106",
        defaultRotisPerMeal = 3,
        mealTime = "दोन्ही (Both)",
        status = SubscriptionStatus.ACTIVE,
        notes = "दोन्ही वेळ नियमित"
      ),
      TiffinCustomerProfile(
        id = "c7",
        name = "महेश शेटे (Mahesh Shete)",
        phone = "9850123456",
        area = "महाद्वार घाट (Mahadwar Ghat)",
        address = "दुकान क्र. ३",
        assignedBoxNo = "T-107",
        defaultRotisPerMeal = 2,
        mealTime = "दुपार (Lunch)",
        status = SubscriptionStatus.ACTIVE,
        notes = "दुकानात पोहोचवा"
      ),
      TiffinCustomerProfile(
        id = "c8",
        name = "ओंकार शिंदे (Omkar Shinde)",
        phone = "9922334455",
        area = "वाखरी नाका (Wakhari Naka)",
        address = "रूम क्र. ८",
        assignedBoxNo = "T-108",
        defaultRotisPerMeal = 3,
        mealTime = "रात्र (Dinner)",
        status = SubscriptionStatus.ON_LEAVE,
        leaveReason = "आज रात्री बाहेर जेवण",
        notes = "उद्या सुरू करा"
      )
    )
  }

  var currentSubTab by remember { mutableStateOf("deliveries") } // "deliveries", "roti_summary", "checkin_checkout", "customer_registry"
  var showAddOrderDialog by remember { mutableStateOf(false) }
  var selectedDeliveryForExchange by remember { mutableStateOf<TiffinDelivery?>(null) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedMealFilter by remember { mutableStateOf("सर्व (All)") }
  var selectedStatusFilter by remember { mutableStateOf("सर्व प्रलंबित (Pending)") }

  // Filtered deliveries for LazyColumn
  val filteredDeliveries by remember {
    derivedStateOf {
      deliveries.filter { delivery ->
        // Status & Tiffin Return filter
        val matchesStatus = when (selectedStatusFilter) {
          "सर्व प्रलंबित (Pending)" -> delivery.status != DeliveryStatus.DELIVERED
          "⚠️ डबा परत बाकी (Box Pending)" -> !delivery.isBoxReturned
          "पॅक झाले (Packed)" -> delivery.status == DeliveryStatus.PACKED
          "पूर्ण झाले (Delivered)" -> delivery.status == DeliveryStatus.DELIVERED
          else -> true
        }

        // Meal filter
        val matchesMeal = when (selectedMealFilter) {
          "दुपार (Lunch)" -> delivery.mealType.contains("दुपार") || delivery.mealType.contains("दोन्ही")
          "रात्र (Dinner)" -> delivery.mealType.contains("रात्र") || delivery.mealType.contains("दोन्ही")
          else -> true
        }

        // Search query (matches name, area, phone, or tiffin box number!)
        val matchesSearch = if (searchQuery.isBlank()) {
          true
        } else {
          delivery.customerName.contains(searchQuery, ignoreCase = true) ||
              delivery.area.contains(searchQuery, ignoreCase = true) ||
              delivery.phone.contains(searchQuery) ||
              delivery.deliveredBoxNo.contains(searchQuery, ignoreCase = true) ||
              delivery.returnedBoxNo.contains(searchQuery, ignoreCase = true)
        }

        matchesStatus && matchesMeal && matchesSearch
      }
    }
  }

  // Summary statistics
  val totalPendingCount = remember(deliveries.size, deliveries.map { it.status }) {
    deliveries.count { it.status != DeliveryStatus.DELIVERED }
  }
  val totalUnreturnedBoxes = remember(deliveries.size, deliveries.map { it.isBoxReturned }) {
    deliveries.count { !it.isBoxReturned }
  }
  val totalPackedCount = remember(deliveries.size, deliveries.map { it.status }) {
    deliveries.count { it.status == DeliveryStatus.PACKED }
  }
  val totalDeliveredCount = remember(deliveries.size, deliveries.map { it.status }) {
    deliveries.count { it.status == DeliveryStatus.DELIVERED }
  }
  // Daily Rotis Required: Calculated dynamically from ACTIVE customers only,
  // strictly excluding customers marked as ON_LEAVE, INACTIVE, or PAUSED
  val totalRotisNeeded = remember(customerProfiles.toList()) {
    customerProfiles
      .filter { it.status == SubscriptionStatus.ACTIVE }
      .sumOf { it.calculateRotisForMeal("All") }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      if (currentSubTab == "deliveries") {
        ExtendedFloatingActionButton(
          onClick = { showAddOrderDialog = true },
          icon = { Icon(Icons.Default.Add, contentDescription = "नवीन ऑर्डर") },
          text = { Text("नवीन ऑर्डर (Add Order)", fontWeight = FontWeight.Bold) },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.testTag("add_order_fab")
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Sub-Tabs Header: Deliveries, Check-In/Out Desk, Customer Unique Box Registry
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = currentSubTab == "deliveries",
            onClick = { currentSubTab = "deliveries" },
            label = { Text("🚚 आजच्या डिलिव्हरी", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("subtab_deliveries")
          )
          FilterChip(
            selected = currentSubTab == "roti_summary",
            onClick = { currentSubTab = "roti_summary" },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🫓 पोळी डॅशबोर्ड", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primary
                ) {
                  Text(
                    text = "$totalRotisNeeded",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            },
            modifier = Modifier.testTag("subtab_roti_summary")
          )
          FilterChip(
            selected = currentSubTab == "checkin_checkout",
            onClick = { currentSubTab = "checkin_checkout" },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🍱 डबा चेक-इन/आऊट", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                val overdueBoxes = customerAssignments.count { it.trackingStatus == BoxTrackingStatus.OVERDUE || it.daysOutCount >= 2 }
                if (overdueBoxes > 0) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFC62828)
                  ) {
                    Text(
                      text = "$overdueBoxes",
                      color = Color.White,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }
            },
            modifier = Modifier.testTag("subtab_checkin_checkout")
          )
          FilterChip(
            selected = currentSubTab == "customer_registry",
            onClick = { currentSubTab = "customer_registry" },
            label = { Text("👥 युनिक डबा नोंदणी", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("subtab_customer_registry")
          )
        }
      }

      when (currentSubTab) {
        "deliveries" -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("pending_delivery_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
      // Header & Title
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "आसन टिफिन • पंढरपूर",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "आजच्या प्रलंबित डिलिव्हरी",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
              )
            }

            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
              Text(
                text = "$totalPendingCount प्रलंबित",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Stats Metric Card with Tiffin Box Tracking Indicator
      item {
        ElevatedCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            MetricItem(
              label = "प्रलंबित",
              value = "$totalPendingCount",
              color = Color(0xFFE65100),
              icon = "⏳"
            )
            MetricItem(
              label = "डबा बाकी",
              value = "$totalUnreturnedBoxes",
              color = Color(0xFFD32F2F),
              icon = "🍱"
            )
            MetricItem(
              label = "पॅक झाले",
              value = "$totalPackedCount",
              color = Color(0xFF7B1FA2),
              icon = "📦"
            )
            MetricItem(
              label = "डिलिव्हर्ड",
              value = "$totalDeliveredCount",
              color = Color(0xFF2E7D32),
              icon = "✅"
            )
            MetricItem(
              label = "पोळ्या",
              value = "$totalRotisNeeded",
              color = MaterialTheme.colorScheme.primary,
              icon = "🫓",
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { currentSubTab = "roti_summary" }
                .padding(4.dp)
                .testTag("metric_item_rotis")
            )
          }
        }
      }

      // Search Field (Supports searching by Name, Area, Phone, or Tiffin Box Number)
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_order_input"),
          placeholder = { Text("नाव, भाग किंवा टिफिन डबा क्र. (उदा. T-101)...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )
      }

      // Meal and Status Filters Row
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Meal Filter Chips
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

          // Status & Tiffin Box Filter Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              "सर्व प्रलंबित (Pending)",
              "⚠️ डबा परत बाकी (Box Pending)",
              "पॅक झाले (Packed)",
              "पूर्ण झाले (Delivered)"
            ).forEach { statusFilter ->
              FilterChip(
                selected = selectedStatusFilter == statusFilter,
                onClick = { selectedStatusFilter = statusFilter },
                label = { Text(statusFilter, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors()
              )
            }
          }
        }
      }

      // Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "डिलिव्हरी यादी (${filteredDeliveries.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "🍱 डबा ट्रॅकिंग सुरू 📍",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Empty state
      if (filteredDeliveries.isEmpty()) {
        item {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "🎉",
                fontSize = 42.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "कोणतीही प्रलंबित डिलिव्हरी नाही!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "या फिल्टरनुसार सर्व ऑर्डर्स पूर्ण झाल्या आहेत किंवा नवीन ऑर्डर जोडा.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      } else {
        // Delivery items in LazyColumn
        items(
          items = filteredDeliveries,
          key = { it.id }
        ) { delivery ->
          DeliveryCardItem(
            delivery = delivery,
            onStatusChange = { newStatus ->
              val index = deliveries.indexOfFirst { it.id == delivery.id }
              if (index != -1) {
                deliveries[index] = deliveries[index].copy(status = newStatus)
                scope.launch {
                  snackbarHostState.showSnackbar("#${delivery.serial} ${delivery.customerName} - ${newStatus.label}")
                }
              }
            },
            onCallCustomer = { phone ->
              try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(intent)
              } catch (_: Exception) {}
            },
            onToggleBoxReturn = { targetDelivery ->
              val index = deliveries.indexOfFirst { it.id == targetDelivery.id }
              if (index != -1) {
                val updatedReturned = !targetDelivery.isBoxReturned
                deliveries[index] = targetDelivery.copy(isBoxReturned = updatedReturned)
                // Synchronize with Customer Box Registry & Audit Log
                val matchingAssignment = customerAssignments.firstOrNull {
                  it.assignedBoxNo.equals(targetDelivery.returnedBoxNo, ignoreCase = true) ||
                      it.customerName.equals(targetDelivery.customerName, ignoreCase = true)
                }
                if (matchingAssignment != null) {
                  if (updatedReturned) {
                    matchingAssignment.trackingStatus = BoxTrackingStatus.IN_KITCHEN
                    matchingAssignment.daysOutCount = 0
                    matchingAssignment.lastCheckInTime = "आजच जमा"
                    boxLogs.add(0, BoxLogEntry(
                      id = "log_${System.currentTimeMillis()}",
                      boxNo = targetDelivery.returnedBoxNo.ifBlank { matchingAssignment.assignedBoxNo },
                      customerName = targetDelivery.customerName,
                      action = "CHECK_IN",
                      timestamp = "आजच जमा",
                      remarks = "डिलिव्हरी कार्डवरून डबा किचनमध्ये जमा"
                    ))
                  } else {
                    matchingAssignment.trackingStatus = BoxTrackingStatus.CHECKED_OUT
                    matchingAssignment.daysOutCount = 1
                  }
                }
                scope.launch {
                  val msg = if (updatedReturned) {
                    "✅ डबा #${targetDelivery.returnedBoxNo} ग्राहकाकडून जमा झाला!"
                  } else {
                    "⚠️ डबा #${targetDelivery.returnedBoxNo} परत बाकी म्हणून नोंदवले."
                  }
                  snackbarHostState.showSnackbar(msg)
                }
              }
            },
            onOpenExchange = { targetDelivery ->
              selectedDeliveryForExchange = targetDelivery
            }
          )
        }
      }
    }
  }
        "roti_summary" -> {
          DailyRotiSummaryDashboard(
            customerProfiles = customerProfiles,
            modifier = Modifier.fillMaxSize(),
            onStatusChange = { profile, newStatus, reason ->
              scope.launch {
                snackbarHostState.showSnackbar(
                  "ℹ️ ${profile.name}: स्थिती ${newStatus.marathiLabel} म्हणून अपडेट झाली."
                )
              }
            }
          )
        }
        "checkin_checkout" -> {
          TiffinCheckInOutScreen(
            customerAssignments = customerAssignments,
            boxLogs = boxLogs,
            modifier = Modifier.fillMaxSize()
          )
        }
        "customer_registry" -> {
          CustomerBoxRegistryScreen(
            customerAssignments = customerAssignments,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }

  // Tiffin Exchange / Box Number Edit Dialog
  selectedDeliveryForExchange?.let { currentDelivery ->
    TiffinExchangeDialog(
      delivery = currentDelivery,
      onDismiss = { selectedDeliveryForExchange = null },
      onSave = { deliveredBox, returnedBox, isReturned, remarks ->
        val index = deliveries.indexOfFirst { it.id == currentDelivery.id }
        if (index != -1) {
          deliveries[index] = currentDelivery.copy(
            deliveredBoxNo = deliveredBox,
            returnedBoxNo = returnedBox,
            isBoxReturned = isReturned,
            returnRemarks = remarks
          )
          // Also sync customer assignment box if needed
          val ca = customerAssignments.firstOrNull { it.customerName.equals(currentDelivery.customerName, ignoreCase = true) }
          if (ca != null) {
            ca.assignedBoxNo = deliveredBox
            if (isReturned) {
              ca.trackingStatus = BoxTrackingStatus.IN_KITCHEN
              ca.daysOutCount = 0
            }
          }
          scope.launch {
            snackbarHostState.showSnackbar("🍱 डबा नोंद अद्ययावत केली: दिला #$deliveredBox, परत #$returnedBox")
          }
        }
        selectedDeliveryForExchange = null
      }
    )
  }

  // Add Order Dialog
  if (showAddOrderDialog) {
    AddOrderDialog(
      nextSerial = (deliveries.maxOfOrNull { it.serial } ?: 0) + 1,
      customerAssignments = customerAssignments,
      onDismiss = { showAddOrderDialog = false },
      onAddOrder = { newDelivery ->
        deliveries.add(0, newDelivery)
        // Ensure customer is registered in unique box registry
        val existingCa = customerAssignments.firstOrNull { it.customerName.equals(newDelivery.customerName, ignoreCase = true) }
        if (existingCa == null) {
          customerAssignments.add(
            CustomerBoxAssignment(
              customerId = "c_${System.currentTimeMillis()}",
              customerName = newDelivery.customerName,
              phone = newDelivery.phone,
              area = newDelivery.area,
              assignedBoxNo = newDelivery.deliveredBoxNo,
              trackingStatus = BoxTrackingStatus.CHECKED_OUT,
              lastCheckOutTime = "आजच रवाना",
              lastCheckInTime = if (newDelivery.isBoxReturned) "मागील डबा जमा" else "-",
              daysOutCount = 1,
              notes = newDelivery.notes
            )
          )
        } else {
          existingCa.assignedBoxNo = newDelivery.deliveredBoxNo
          existingCa.trackingStatus = BoxTrackingStatus.CHECKED_OUT
          existingCa.daysOutCount = 1
        }
        // Add log
        boxLogs.add(0, BoxLogEntry(
          id = "log_${System.currentTimeMillis()}",
          boxNo = newDelivery.deliveredBoxNo,
          customerName = newDelivery.customerName,
          action = "CHECK_OUT",
          timestamp = "आजच रवाना",
          remarks = "नवीन ऑर्डर डिलिव्हरीसाठी डबा रवाना"
        ))
        showAddOrderDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("✅ नवीन ऑर्डर जोडली: डबा #${newDelivery.deliveredBoxNo}")
        }
      }
    )
  }
}

@Composable
fun MetricItem(
  label: String,
  value: String,
  color: Color,
  icon: String,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Text(text = icon, fontSize = 16.sp)
    Text(
      text = value,
      fontSize = 18.sp,
      fontWeight = FontWeight.ExtraBold,
      color = color
    )
    Text(
      text = label,
      fontSize = 10.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun DeliveryCardItem(
  delivery: TiffinDelivery,
  onStatusChange: (DeliveryStatus) -> Unit,
  onCallCustomer: (String) -> Unit,
  onToggleBoxReturn: (TiffinDelivery) -> Unit,
  onOpenExchange: (TiffinDelivery) -> Unit
) {
  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("delivery_card_${delivery.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = if (delivery.status == DeliveryStatus.DELIVERED) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Row: Serial, Name, Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          // Serial number badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
          ) {
            Text(
              text = "#${delivery.serial}",
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = delivery.customerName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Status Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = delivery.status.badgeColor.copy(alpha = 0.15f),
          contentColor = delivery.status.badgeColor
        ) {
          Text(
            text = "${delivery.status.icon} ${delivery.status.label.split(" ")[0]}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Middle Row: Area & Meal
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.LocationOn,
          contentDescription = "Area",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = delivery.area,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tags Row: Plan, Meal, Food Type, Rotis
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Food Type Badge
        val isVeg = delivery.foodType == "Veg"
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = if (isVeg) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
          border = BorderStroke(
            1.dp,
            if (isVeg) Color(0xFFA5D6A7) else Color(0xFFEF9A9A)
          )
        ) {
          Text(
            text = if (isVeg) "🥦 व्हेज" else "🍗 नॉन-व्हेज",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isVeg) Color(0xFF2E7D32) else Color(0xFFC62828)
          )
        }

        // Plan Badge
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "${delivery.plan} प्लॅन",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Meal Badge
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = delivery.mealType,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Roti count
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
          Text(
            text = "🫓 ${delivery.rotis} पोळ्या",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // --- Tiffin Box Number & Return Tracking Card ---
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .testTag("tiffin_box_card_${delivery.id}"),
        color = if (!delivery.isBoxReturned) Color(0xFFFFF8E1) else Color(0xFFF1F8E9),
        border = BorderStroke(
          1.dp,
          if (!delivery.isBoxReturned) Color(0xFFFFB300) else Color(0xFFA5D6A7)
        )
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          // Row with Delivered Box # and Returned Box #
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Delivered Box #
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🍱 दिलेला डबा: ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
              ) {
                Text(
                  text = delivery.deliveredBoxNo.ifBlank { "नियोजित नाही" },
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }

            // Returned Box #
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔄 परत डबा: ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (delivery.isBoxReturned) Color(0xFF2E7D32) else Color(0xFFC62828),
                contentColor = Color.White
              ) {
                Text(
                  text = if (delivery.isBoxReturned) {
                    "${delivery.returnedBoxNo.ifBlank { "-" }} (जमा ✅)"
                  } else {
                    "${delivery.returnedBoxNo.ifBlank { "-" }} (बाकी ⚠️)"
                  },
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }
          }

          if (delivery.returnRemarks.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "💬 शेरा: ${delivery.returnRemarks}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Tiffin Action Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (!delivery.isBoxReturned) {
              Text(
                text = "⚠️ कालचा डबा #${delivery.returnedBoxNo} ग्राहकाकडे आहे!",
                fontSize = 11.sp,
                color = Color(0xFFD84315),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
              )
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                  onClick = { onOpenExchange(delivery) },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.testTag("btn_edit_box_${delivery.id}")
                ) {
                  Text("✏️ अदलाबदल", fontSize = 11.sp)
                }
                FilledTonalButton(
                  onClick = { onToggleBoxReturn(delivery) },
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                  shape = RoundedCornerShape(6.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                  ),
                  modifier = Modifier.testTag("btn_return_box_${delivery.id}")
                ) {
                  Text("📥 जमा झाला", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            } else {
              Text(
                text = "✅ मागील डबा #${delivery.returnedBoxNo} सुरक्षित जमा झाला आहे",
                fontSize = 11.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
              )
              OutlinedButton(
                onClick = { onOpenExchange(delivery) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("btn_edit_box_${delivery.id}")
              ) {
                Text("✏️ डबा नोंद बदला", fontSize = 11.sp)
              }
            }
          }
        }
      }

      // Notes if present
      if (delivery.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "📝 टीप: ${delivery.notes}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons Row (Call & Advance Status)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Call Button
        OutlinedButton(
          onClick = { onCallCustomer(delivery.phone) },
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("कॉल (${delivery.phone})", fontSize = 12.sp)
        }

        // Next Stage Action Button
        val nextStatus = when (delivery.status) {
          DeliveryStatus.PENDING -> DeliveryStatus.COOKING
          DeliveryStatus.COOKING -> DeliveryStatus.PACKED
          DeliveryStatus.PACKED -> DeliveryStatus.DELIVERED
          DeliveryStatus.DELIVERED -> DeliveryStatus.PENDING
        }

        val buttonLabel = when (delivery.status) {
          DeliveryStatus.PENDING -> "🍳 तयार करा"
          DeliveryStatus.COOKING -> "📦 पॅक करा"
          DeliveryStatus.PACKED -> "🚴 डिलिव्हर करा"
          DeliveryStatus.DELIVERED -> "↩ पूर्ववत करा"
        }

        Button(
          onClick = { onStatusChange(nextStatus) },
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (delivery.status == DeliveryStatus.PACKED) {
              Color(0xFF2E7D32)
            } else {
              MaterialTheme.colorScheme.primary
            }
          ),
          modifier = Modifier.testTag("advance_status_btn_${delivery.id}")
        ) {
          Text(buttonLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun TiffinExchangeDialog(
  delivery: TiffinDelivery,
  onDismiss: () -> Unit,
  onSave: (deliveredBox: String, returnedBox: String, isReturned: Boolean, remarks: String) -> Unit
) {
  var deliveredBox by remember { mutableStateOf(delivery.deliveredBoxNo) }
  var returnedBox by remember { mutableStateOf(delivery.returnedBoxNo) }
  var isReturned by remember { mutableStateOf(delivery.isBoxReturned) }
  var remarks by remember { mutableStateOf(delivery.returnRemarks) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = "🍱 टिफिन डबा अदलाबदल नोंद",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "#${delivery.serial} ${delivery.customerName} (${delivery.area})",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "💡 सूचना: डबा हरवू नये यासाठी आज कोणता डबा दिला (उदा. T-101) आणि कालचा कोणता डबा परत घेतला (उदा. T-94) याची अचूक नोंद ठेवा.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
          )
        }

        // Delivered Tiffin Box Number
        OutlinedTextField(
          value = deliveredBox,
          onValueChange = { deliveredBox = it },
          label = { Text("आज दिलेला टिफिन डबा क्र. (Delivered Box #)*") },
          placeholder = { Text("उदा. T-101 किंवा 101") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_delivered_box_no")
        )

        // Returned Tiffin Box Number
        OutlinedTextField(
          value = returnedBox,
          onValueChange = { returnedBox = it },
          label = { Text("परत आलेला / घ्यावयाचा डबा क्र. (Returned Box #)*") },
          placeholder = { Text("उदा. T-94 किंवा 94") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_returned_box_no")
        )

        // Switch: Was previous box returned?
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isReturned) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
          border = BorderStroke(1.dp, if (isReturned) Color(0xFFA5D6A7) else Color(0xFFFFB74D)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isReturned) "✅ कालचा डबा परत मिळाला आहे" else "⚠️ डबा अजून ग्राहकाकडे बाकी आहे",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isReturned) Color(0xFF2E7D32) else Color(0xFFE65100)
              )
              Text(
                text = if (isReturned) "डबा सुरक्षित स्वयंपाकघरात जमा" else "डिलिव्हरी बॉयने डबा परत आणायचा आहे",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = isReturned,
              onCheckedChange = { isReturned = it },
              modifier = Modifier.testTag("toggle_is_box_returned")
            )
          }
        }

        // Remarks / Notes
        OutlinedTextField(
          value = remarks,
          onValueChange = { remarks = it },
          label = { Text("डब्याबद्दल शेरा (उदा. उद्या देणार / झाकण खराब)") },
          placeholder = { Text("टीप किंवा शेरा लिहा...") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(deliveredBox.trim(), returnedBox.trim(), isReturned, remarks.trim())
        },
        modifier = Modifier.testTag("save_tiffin_exchange_btn")
      ) {
        Text("डबा नोंद सेव्ह करा", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("रद्द करा")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderDialog(
  nextSerial: Int,
  customerAssignments: List<CustomerBoxAssignment> = emptyList(),
  onDismiss: () -> Unit,
  onAddOrder: (TiffinDelivery) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var area by remember { mutableStateOf("स्टेशन रोड (Station Road)") }
  var mealType by remember { mutableStateOf("दुपार (Lunch)") }
  var plan by remember { mutableStateOf("Standard") }
  var foodType by remember { mutableStateOf("Veg") }
  var customRotis by remember { mutableStateOf("3") }
  var notes by remember { mutableStateOf("") }
  
  // Calculate next suggested unique box number
  val autoBoxSuggestion = remember(customerAssignments.size) {
    val maxNum = customerAssignments.mapNotNull { it.assignedBoxNo.removePrefix("T-").toIntOrNull() }.maxOrNull() ?: 100
    "T-${maxNum + 1}"
  }
  var deliveredBoxNo by remember { mutableStateOf(autoBoxSuggestion) }
  var returnedBoxNo by remember { mutableStateOf("T-${nextSerial + 80}") }
  var isBoxReturned by remember { mutableStateOf(false) }

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

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("➕ नवीन टिफिन ऑर्डर", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = "#$nextSerial",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Customer Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("ग्राहकाचे नाव (Customer Name)*") },
          placeholder = { Text("उदा. सागर मोरे") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("order_name_input")
        )

        // Phone Number
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("मोबाईल नंबर (Phone Number)*") },
          placeholder = { Text("उदा. 9822001122") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("order_phone_input")
        )

        // Delivery Area Dropdown
        ExposedDropdownMenuBox(
          expanded = isAreaDropdownExpanded,
          onExpandedChange = { isAreaDropdownExpanded = !isAreaDropdownExpanded }
        ) {
          OutlinedTextField(
            value = area,
            onValueChange = {},
            readOnly = true,
            label = { Text("डिलिव्हरी भाग (Area / Location)*") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAreaDropdownExpanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("order_area_input")
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

        // Tiffin Box Number Assignment Card
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "🍱 टिफिन डबा क्र. नोंद (Tiffin Box Assignment)",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = deliveredBoxNo,
                onValueChange = { deliveredBoxNo = it },
                label = { Text("दिलेला डबा क्र.*") },
                placeholder = { Text("उदा. T-101") },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("add_delivered_box_input")
              )
              OutlinedTextField(
                value = returnedBoxNo,
                onValueChange = { returnedBoxNo = it },
                label = { Text("परत डबा क्र.") },
                placeholder = { Text("उदा. T-94") },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("add_returned_box_input")
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isBoxReturned) "✅ मागील डबा आधीच परत आला" else "⚠️ मागील डबा परत येणे बाकी",
                fontSize = 12.sp,
                color = if (isBoxReturned) Color(0xFF2E7D32) else Color(0xFFE65100),
                fontWeight = FontWeight.SemiBold
              )
              Switch(
                checked = isBoxReturned,
                onCheckedChange = { isBoxReturned = it },
                modifier = Modifier.testTag("add_box_returned_switch")
              )
            }
          }
        }

        // Meal Type Selector
        Text(
          text = "जेवणाची वेळ (Meal Preference)*",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("दुपार (Lunch)", "रात्र (Dinner)", "दोन्ही (Both)").forEach { mType ->
            FilterChip(
              selected = mealType == mType,
              onClick = { mealType = mType },
              label = { Text(mType, fontSize = 11.sp) }
            )
          }
        }

        // Plan Selector
        Text(
          text = "प्लॅन (Plan)*",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Basic", "Standard", "Premium").forEach { pName ->
            FilterChip(
              selected = plan == pName,
              onClick = {
                plan = pName
                customRotis = when (pName) {
                  "Basic" -> "2"
                  "Standard" -> "3"
                  "Premium" -> "4"
                  else -> "3"
                }
              },
              label = {
                Text(
                  text = when (pName) {
                    "Basic" -> "Basic (2 पोळ्या)"
                    "Standard" -> "Std (3 पोळ्या)"
                    "Premium" -> "Prem (4 पोळ्या)"
                    else -> pName
                  },
                  fontSize = 11.sp
                )
              }
            )
          }
        }

        // Food Type (Veg / Non-Veg)
        Text(
          text = "अन्न प्रकार (Food Type)*",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          FilterChip(
            selected = foodType == "Veg",
            onClick = { foodType = "Veg" },
            label = { Text("🥦 व्हेज (Veg)") }
          )
          FilterChip(
            selected = foodType == "Non-Veg",
            onClick = { foodType = "Non-Veg" },
            label = { Text("🍗 नॉन-व्हेज (Non-Veg)") }
          )
        }

        // Roti Count
        OutlinedTextField(
          value = customRotis,
          onValueChange = { customRotis = it },
          label = { Text("पोळी संख्या (Rotis Count)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Notes / Special Instructions
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("विशेष सूचना / टीप (Notes)") },
          placeholder = { Text("उदा. कमी तिखट किंवा लवकर हवे") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val newDelivery = TiffinDelivery(
              id = "ord_${System.currentTimeMillis()}",
              serial = nextSerial,
              customerName = name.trim(),
              phone = phone.ifBlank { "9999999999" },
              area = area,
              mealType = mealType,
              plan = plan,
              foodType = foodType,
              rotis = customRotis.toIntOrNull() ?: 3,
              status = DeliveryStatus.PENDING,
              notes = notes.trim(),
              deliveredBoxNo = deliveredBoxNo.trim().ifBlank { "T-${nextSerial + 100}" },
              returnedBoxNo = returnedBoxNo.trim().ifBlank { "T-${nextSerial + 80}" },
              isBoxReturned = isBoxReturned,
              returnRemarks = ""
            )
            onAddOrder(newDelivery)
          }
        },
        enabled = name.isNotBlank(),
        modifier = Modifier.testTag("confirm_add_order_btn")
      ) {
        Text("ऑर्डर जोडा (Add Order)", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("रद्द करा (Cancel)")
      }
    }
  )
}
