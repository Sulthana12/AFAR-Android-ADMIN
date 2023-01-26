package com.wings.mile.ui.payments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaymentsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Payments Page"
    }
    val text: LiveData<String> = _text
}