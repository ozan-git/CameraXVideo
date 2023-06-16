package com.example.android.camerax.video.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VimeoApiClient {

    private const val BASE_URL = "https://api.vimeo.com/"


    private val retrofitClient: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    val vimeoService: VimeoService by lazy {
        retrofitClient.create(VimeoService::class.java)
    }
}
