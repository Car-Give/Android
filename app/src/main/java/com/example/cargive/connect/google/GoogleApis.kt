package com.example.cargive.connect.google

import com.example.cargive.data.google.search.GooglePlaceSearchModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GoogleApis {
    @GET("json")
    fun getPlaceInfo(@QueryMap query: Map<String, String>) : Call<GooglePlaceSearchModel>


}