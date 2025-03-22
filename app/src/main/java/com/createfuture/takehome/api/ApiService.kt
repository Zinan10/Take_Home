package com.createfuture.takehome.api


import com.createfuture.takehome.models.ApiCharacter
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header



// ApiService.kt
interface ApiService {
    @GET("/characters")
    suspend fun getCharacters(@Header("Authorization") token: String): Response<List<ApiCharacter>>
}

// RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "https://yj8ke8qonl.execute-api.eu-west-1.amazonaws.com"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(ApiService::class.java)
    }
}