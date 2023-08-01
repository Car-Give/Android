package com.example.cargive.model.network.search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface KaKaoSearchApi {
    @GET("keyword.json")
    fun getPlaceInfo(@QueryMap query: Map<String, String>, @Header("Authorization")token: String) : Call<KaKaoSearchResultModel>


}