package com.example.focuspanda.viewmodels



import android.hardware.Sensor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PomodoroViewModel : ViewModel() {
    // Timer state
    private val _minutes = MutableStateFlow(25)
    private val _seconds = MutableStateFlow(0)
    private val _isRunning = MutableStateFlow(false)

    // Sensor states
    private val _useProximitySensor = MutableStateFlow(true)
    private val _useMotionSensors = MutableStateFlow(true)
    private val _phoneMoved = MutableStateFlow(false)
    private val _showMovementWarning = MutableStateFlow(false)
    private val _currentMotionValue = MutableStateFlow(0f)
    private val _maxMotionValue = MutableStateFlow(0f)
    private val _proximityValue = MutableStateFlow(0f)
    private val _maxProximityValue = MutableStateFlow(0f)

    // History
    private val _completedSessions = MutableStateFlow<List<CompletedSession>>(emptyList())

    // UI state
    private val _showCustomTimeDialog = MutableStateFlow(false)
    private val _showHistoryDialog = MutableStateFlow(false)


    // Public state flows
    val minutes: StateFlow<Int> = _minutes.asStateFlow()
    val seconds: StateFlow<Int> = _seconds.asStateFlow()
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    val useProximitySensor: StateFlow<Boolean> = _useProximitySensor.asStateFlow()
    val useMotionSensors: StateFlow<Boolean> = _useMotionSensors.asStateFlow()
    val phoneMoved: StateFlow<Boolean> = _phoneMoved.asStateFlow()
    val showMovementWarning: StateFlow<Boolean> = _showMovementWarning.asStateFlow()
    val currentMotionValue: StateFlow<Float> = _currentMotionValue.asStateFlow()
    val maxMotionValue: StateFlow<Float> = _maxMotionValue.asStateFlow()
    val proximityValue: StateFlow<Float> = _proximityValue.asStateFlow()
    val maxProximityValue: StateFlow<Float> = _maxProximityValue.asStateFlow()
    val completedSessions: StateFlow<List<CompletedSession>> = _completedSessions.asStateFlow()
    val showCustomTimeDialog: StateFlow<Boolean> = _showCustomTimeDialog.asStateFlow()
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    // Timer logic
    init {
        viewModelScope.launch {
            while (true) {
                if (_isRunning.value) {
                    delay(1000L)
                    if (_seconds.value == 0) {
                        if (_minutes.value > 0) {
                            _minutes.value--
                            _seconds.value = 59
                        } else {
                            _isRunning.value = false
                            if (!_phoneMoved.value) {
                                val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
                                val newSession = CompletedSession(
                                    duration = "${25 - _minutes.value}:${59 - _seconds.value}",
                                    date = dateFormat.format(Date()),
                                    wasSuccessful = true
                                )
                                _completedSessions.value = _completedSessions.value + newSession
                            }
                            _phoneMoved.value = false
                        }
                    } else {
                        _seconds.value--
                    }
                } else {
                    delay(1000L)
                }
            }
        }
    }

    // Actions
    fun toggleTimer() {
        if (!_isRunning.value) {
            _phoneMoved.value = false
            _maxMotionValue.value = 0f
            _maxProximityValue.value = 0f
        }
        _isRunning.value = !_isRunning.value
    }

    fun resetTimer() {
        _isRunning.value = false
        _minutes.value = 25
        _seconds.value = 0
        _phoneMoved.value = false
        _maxMotionValue.value = 0f
        _maxProximityValue.value = 0f
    }

    fun setCustomTime(minutes: Int, seconds: Int) {
        _minutes.value = minutes
        _seconds.value = seconds
        _showCustomTimeDialog.value = false
    }

    fun updateProximityValue(value: Float) {
        _proximityValue.value = value
        _maxProximityValue.value = maxOf(_maxProximityValue.value, value)

        // Most proximity sensors report 0 when near, max when far
        if (value < 2f && _isRunning.value && _useProximitySensor.value) {
            _phoneMoved.value = true
            _showMovementWarning.value = true
        }
    }

    fun updateMotionValue(value: Float) {
        _currentMotionValue.value = value
        _maxMotionValue.value = maxOf(_maxMotionValue.value, value)

        if (value > 1.5f && _isRunning.value && _useMotionSensors.value) {
            _phoneMoved.value = true
            _showMovementWarning.value = true
        }
    }

    fun toggleProximitySensor(enabled: Boolean) {
        _useProximitySensor.value = enabled
    }

    fun toggleMotionSensors(enabled: Boolean) {
        _useMotionSensors.value = enabled
    }

    fun showCustomTimeDialog() {
        _showCustomTimeDialog.value = true
    }

    fun dismissCustomTimeDialog() {
        _showCustomTimeDialog.value = false
    }

    fun showHistoryDialog() {
        _showHistoryDialog.value = true
    }

    fun dismissHistoryDialog() {
        _showHistoryDialog.value = false
    }

    fun dismissMovementWarning() {
        _showMovementWarning.value = false
    }
}

data class CompletedSession(
    val duration: String,
    val date: String,
    val wasSuccessful: Boolean
)