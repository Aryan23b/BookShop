package com.example.libbook.data.remote


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchByIsbn(@Query("q") query: String): Response<GoogleBooksResponse>
}