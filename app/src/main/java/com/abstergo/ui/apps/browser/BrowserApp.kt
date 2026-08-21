package com.abstergo.ui.apps.browser

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BrowserApp(
    viewModel: BrowserViewModel = viewModel()
) {
    val currentUrl by viewModel.currentUrl.collectAsState()
    val urlInput by viewModel.urlInput.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var webView by remember { mutableStateOf<WebView?>(null) }

    val bookmarks = listOf(
        "Google" to "https://www.google.com",
        "Wikipedia" to "https://www.wikipedia.org",
        "GitHub" to "https://www.github.com"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // URL bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Back button
            IconButton(
                onClick = { webView?.goBack() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Forward button
            IconButton(
                onClick = { webView?.goForward() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // URL input
            OutlinedTextField(
                value = urlInput,
                onValueChange = { viewModel.updateUrlInput(it) },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF64B5F6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color(0xFF2D2D30),
                    unfocusedContainerColor = Color(0xFF2D2D30),
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.navigateToUrl()
                    }
                )
            )

            // Refresh button
            IconButton(
                onClick = { webView?.reload() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = Color(0xFF64B5F6),
                trackColor = Color.Transparent
            )
        }

        // Bookmarks bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bookmarks.forEach { (name, url) ->
                Text(
                    text = name,
                    fontSize = 10.sp,
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.clickable {
                        viewModel.loadUrl(url)
                    }
                )
            }
        }

        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            viewModel.setLoading(true)
                            url?.let { viewModel.updateCurrentUrl(it) }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            viewModel.setLoading(false)
                        }
                    }

                    loadUrl(currentUrl)
                    webView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
