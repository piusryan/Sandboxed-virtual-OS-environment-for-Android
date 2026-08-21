package com.abstergo.ui.apps.sandbox

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.abstergo.model.AppSource
import com.abstergo.model.AppType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages data isolation and flushing for sandboxed web apps.
 * Uses string-based keys to support both built-in and cloned apps.
 */
object DataFlushManager {

    private val _flushVersionMap = mutableMapOf<String, MutableStateFlow<Int>>()

    fun getFlushVersion(appId: String): StateFlow<Int> {
        return _flushVersionMap.getOrPut(appId) { MutableStateFlow(0) }.asStateFlow()
    }

    fun flushAppData(context: Context, appId: String) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        WebStorage.getInstance().deleteAllData()

        _flushVersionMap.getOrPut(appId) { MutableStateFlow(0) }.value += 1
    }

    fun flushAllData(context: Context) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        WebStorage.getInstance().deleteAllData()

        _flushVersionMap.forEach { (_, flow) ->
            flow.value += 1
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SandboxedWebApp(
    appSource: AppSource
) {
    val launchUrl = appSource.launchUrl ?: return
    val appId = appSource.id
    val displayName = appSource.displayName

    val context = LocalContext.current
    val flushVersion by DataFlushManager.getFlushVersion(appId).collectAsState()
    var webView by remember(appId) { mutableStateOf<WebView?>(null) }
    var currentUrl by remember(appId) { mutableStateOf(launchUrl) }
    var isLoading by remember(appId) { mutableStateOf(false) }
    var showFlushDialog by remember { mutableStateOf(false) }
    var showUrlBar by remember { mutableStateOf(false) }

    // Reload WebView when flush happens
    LaunchedEffect(flushVersion) {
        if (flushVersion > 0) {
            webView?.let { wv ->
                wv.clearCache(true)
                wv.clearHistory()
                wv.clearFormData()
                wv.loadUrl(launchUrl)
            }
            currentUrl = launchUrl
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D2D30))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // App name badge
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF1A73E8).copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = displayName,
                    fontSize = 10.sp,
                    color = Color(0xFF64B5F6),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // URL display (collapsible)
            if (showUrlBar) {
                Text(
                    text = currentUrl,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showUrlBar = !showUrlBar }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Flush data button
            IconButton(
                onClick = { showFlushDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Flush Data",
                    tint = Color(0xFFE74C3C),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Refresh
            IconButton(
                onClick = { webView?.reload() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Toggle URL bar
            IconButton(
                onClick = { showUrlBar = !showUrlBar },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (showUrlBar) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle URL",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Loading bar
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = Color(0xFF64B5F6),
                trackColor = Color.Transparent
            )
        }

        // Sandboxed WebView
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        allowFileAccess = false
                        allowContentAccess = false
                        mediaPlaybackRequiresUserGesture = false
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/120.0.0.0 Mobile Safari/537.36"
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            isLoading = true
                            url?.let { currentUrl = it }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            isLoading = newProgress < 100
                        }
                    }

                    loadUrl(launchUrl)
                    webView = this
                }
            },
            update = { _ -> },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Flush confirmation dialog
    if (showFlushDialog) {
        AlertDialog(
            onDismissRequest = { showFlushDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE74C3C)
                )
            },
            title = {
                Text("Flush $displayName Data?", color = Color.White)
            },
            text = {
                Column {
                    Text(
                        "This will permanently delete:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Login sessions & cookies",
                        "Saved passwords in this session",
                        "Local storage & cached data",
                        "Form data & history"
                    ).forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFFE74C3C),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You will be signed out and all data will be wiped clean.",
                        color = Color(0xFFE74C3C).copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    DataFlushManager.flushAppData(context, appId)
                    showFlushDialog = false
                }) {
                    Text("FLUSH DATA", color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlushDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF2D2D30)
        )
    }
}
