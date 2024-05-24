package com.example.bugunneyesem.yapayzeka

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OneriYemeklerTask(private val context: Context) {


    fun execute(malzemeler: List<String>, callback: (List<String>) -> Unit) {
        val request = MalzemelerRequest(malzemeler)
        val call = RetrofitClient.instance.getYemekOnerileri(request)

        call.enqueue(object : Callback<YemekResponse> {
            override fun onResponse(call: Call<YemekResponse>, response: Response<YemekResponse>) {
                if (response.isSuccessful) {
                    val yemeklerList = response.body()?.onerilen_yemekler ?: emptyList()
                    callback(yemeklerList)
                } else {

                }
            }

            override fun onFailure(call: Call<YemekResponse>, t: Throwable) {

            }
        })
    }


}
