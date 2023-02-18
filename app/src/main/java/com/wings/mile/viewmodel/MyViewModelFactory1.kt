package com.wings.mile.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModelFactory1 constructor(private val repository: MainRepository1): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel1::class.java)) {
            MainViewModel1(this.repository, Application()) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}