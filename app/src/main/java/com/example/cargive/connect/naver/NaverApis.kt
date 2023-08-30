package com.example.cargive.connect.naver

import com.example.cargive.data.naver.route.NaverRouteModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface NaverApis {
    @GET("driving")
    fun getPlaceRouteInfo(
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyID: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String,
        @QueryMap query: Map<String, String>
    ): Call<NaverRouteModel>
}