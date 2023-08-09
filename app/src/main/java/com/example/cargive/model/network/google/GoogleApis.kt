package com.example.cargive.model.network.google

import com.example.cargive.model.network.google.route.PlaceRouteResponseModel
import com.example.cargive.model.network.google.search.GooglePlaceSearchModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GoogleApis {
    @GET("json")
    fun getPlaceInfo(@QueryMap query: Map<String, String>) : Call<GooglePlaceSearchModel>

    @GET("json")
    fun getPlaceRouteInfo(@QueryMap query: Map<String, String>) : Call<PlaceRouteResponseModel>

}