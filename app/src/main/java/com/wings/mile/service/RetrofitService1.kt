package com.wings.mile.service


import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.logging.Level


interface RetrofitService1 {




    @GET
    suspend fun updateeuser(
        @Url userid: String
    ): JsonElement



    companion object {

        //https: //mileapi2.azurewebsites.net/
        var retrofitService: RetrofitService1? = null
        var gson = GsonBuilder()
            .setLenient()
            .create()

        fun getInstance(): RetrofitService1 {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            if (retrofitService == null) {

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.smsgatewayhub.com/api/mt/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build()
                retrofitService = retrofit.create(RetrofitService1::class.java)

            }
            return retrofitService!!
        }
    }

}



