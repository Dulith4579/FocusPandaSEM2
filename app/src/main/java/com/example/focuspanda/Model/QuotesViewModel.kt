package com.example.focuspanda.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focuspanda.Data.QuoteResponse
import com.example.focuspanda.Data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuotesViewModel : ViewModel() {
    private val _quotes = MutableStateFlow<List<QuoteResponse>>(emptyList())
    val quotes = _quotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchQuotes()
    }

    fun fetchQuotes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getQuotes() // No manual key needed
                _quotes.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
                _quotes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}