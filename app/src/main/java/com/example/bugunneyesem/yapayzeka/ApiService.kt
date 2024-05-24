package com.example.bugunneyesem.yapayzeka

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class MalzemelerRequest(val malzemeler: List<String>)

data class YemekResponse(val onerilen_yemekler: List<String>)

interface ApiService {
    @Headers("Cache-Control: no-cache")
    @POST("/oneri-yemekler")
    fun getYemekOnerileri(@Body request: MalzemelerRequest): Call<YemekResponse>
}