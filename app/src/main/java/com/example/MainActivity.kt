package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme

import java.io.File

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      // Clean up any previously misplaced or corrupt WebView cache directories
      // so Chromium can initialize its internal cache cleanly without version upgrade errors
      val corruptCodeCache = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache")
      if (corruptCodeCache.exists()) {
        corruptCodeCache.deleteRecursively()
      }
      val corruptIndexDir = File(cacheDir, "WebView/Default/HTTP Cache/index-dir")
      if (corruptIndexDir.exists()) {
        corruptIndexDir.deleteRecursively()
      }
    } catch (_: Throwable) {}

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AsanTiffinApp()
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AsanTiffinApp() {
  var currentApp by remember { mutableStateOf("home") } // "home", "owner", "swapnali"
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  BackHandler(enabled = currentApp != "home" || webViewRef?.canGoBack() == true) {
    if (webViewRef?.canGoBack() == true) {
      webViewRef?.goBack()
    } else {
      currentApp = "home"
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Top selector bar to switch between Home Deliveries, Owner App, and Swapnali App
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = currentApp == "home",
            onClick = { currentApp = "home" },
            label = { Text("🏠 आजच्या ऑर्डर्स") },
            modifier = Modifier.testTag("home_deliveries_tab"),
            colors = FilterChipDefaults.filterChipColors()
          )
          FilterChip(
            selected = currentApp == "owner",
            onClick = { currentApp = "owner" },
            label = { Text("👨‍💼 मालक ॲप") },
            modifier = Modifier.testTag("owner_app_tab"),
            colors = FilterChipDefaults.filterChipColors()
          )
          FilterChip(
            selected = currentApp == "swapnali",
            onClick = { currentApp = "swapnali" },
            label = { Text("👩‍🍳 स्वप्नाली व्ह्यू") },
            modifier = Modifier.testTag("swapnali_app_tab"),
            colors = FilterChipDefaults.filterChipColors()
          )
        }
      }

      if (currentApp == "home") {
        HomeScreen(
          modifier = Modifier.fillMaxSize(),
          onNavigateToOwner = { currentApp = "owner" },
          onNavigateToKitchen = { currentApp = "swapnali" }
        )
      } else {
        val assetUrl = if (currentApp == "owner") {
          "file:///android_asset/asan-tiffin-owner.html"
        } else {
          "file:///android_asset/asan-tiffin-swapnali.html"
        }

        AndroidView(
          modifier = Modifier
            .fillMaxSize()
            .testTag("tiffin_webview"),
          factory = { context ->
            WebView(context).apply {
              // Software layer eliminates Mesa GPU rendernode errors in emulator environments
              setLayerType(View.LAYER_TYPE_SOFTWARE, null)
              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
              }
              webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                  view: WebView?,
                  request: WebResourceRequest?,
                  error: WebResourceError?
                ) {
                  super.onReceivedError(view, request, error)
                }
              }
              webChromeClient = WebChromeClient()
              loadUrl(assetUrl)
              webViewRef = this
            }
          },
          update = { webView ->
            if (webView.url != assetUrl) {
              webView.loadUrl(assetUrl)
            }
            webViewRef = webView
          }
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
