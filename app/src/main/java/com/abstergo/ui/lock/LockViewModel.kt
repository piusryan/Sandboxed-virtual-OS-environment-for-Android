package com.abstergo.ui.lock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abstergo.data.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LockViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)

    private val _storedPin = MutableStateFlow(SettingsDataStore.DEFAULT_PIN)
    val storedPin: StateFlow<String> = _storedPin.asStateFlow()

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _shakeTrigger = MutableStateFlow(0)
    val shakeTrigger: StateFlow<Int> = _shakeTrigger.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.pin.collect { pin ->
                _storedPin.value = pin
            }
        }
    }

    fun appendDigit(digit: String) {
        if (_enteredPin.value.length < 6) {
            _enteredPin.value += digit
        }
    }

    fun deleteDigit() {
        if (_enteredPin.value.isNotEmpty()) {
            _enteredPin.value = _enteredPin.value.dropLast(1)
        }
    }

    fun clearPin() {
        _enteredPin.value = ""
    }

    fun checkPin(): Boolean {
        val isCorrect = _enteredPin.value == _storedPin.value
        if (!isCorrect) {
            _shakeTrigger.value += 1
            clearPin()
        }
        return isCorrect
    }
}
