package com.abstergo.ui.apps.browser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BrowserViewModel : ViewModel() {

    private val _currentUrl = MutableStateFlow("https://www.google.com")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _urlInput = MutableStateFlow("https://www.google.com")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateUrlInput(url: String) {
        _urlInput.value = url
    }

    fun updateCurrentUrl(url: String) {
        _currentUrl.value = url
        _urlInput.value = url
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun navigateToUrl() {
        var url = _urlInput.value.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        _currentUrl.value = url
    }

    fun loadUrl(url: String) {
        _urlInput.value = url
        _currentUrl.value = url
    }
}
