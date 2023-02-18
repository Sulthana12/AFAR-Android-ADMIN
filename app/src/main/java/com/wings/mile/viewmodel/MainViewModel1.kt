package com.wings.mile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.wings.mile.Utils.Resource
import kotlinx.coroutines.Dispatchers


class MainViewModel1 constructor(val repository: MainRepository1, application: Application) : AndroidViewModel(application) {



    fun updateuser(message: String) = liveData(Dispatchers.IO) {
        try {
            val response = repository.updateuser(message)
            emit(Resource.success(data = repository.updateuser(message)))
        } catch (e: Exception) {
            emit(null)
        }
    }


}

