package com.example.moncloaplus.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://us-central1-moncloaplus-593f6.cloudfunctions.net/"

    val api: CloudFunctionsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudFunctionsApi::class.java)
    }

}
