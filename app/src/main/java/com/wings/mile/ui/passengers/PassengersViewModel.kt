package com.wings.mile.ui.passengers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PassengersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Passengers Page"
    }
    val text: LiveData<String> = _text
}