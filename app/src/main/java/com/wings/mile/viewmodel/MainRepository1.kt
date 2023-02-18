package com.wings.mile.viewmodel

import com.wings.mile.service.RetrofitService1

class MainRepository1 constructor(private val retrofitService1: RetrofitService1) {



    suspend fun updateuser(message: String) = retrofitService1.updateeuser(message)
}