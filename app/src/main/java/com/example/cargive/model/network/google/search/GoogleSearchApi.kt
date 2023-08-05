package com.example.cargive.model.network.google.search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GoogleSearchApi {
    @GET("json")
    fun getPlaceInfo(@QueryMap query: Map<String, String>) : Call<GooglePlaceSearchModel>


}