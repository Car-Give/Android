package com.example.cargive.model.network.naver

import android.location.Location
import android.util.Log
import com.example.cargive.BuildConfig
import com.example.cargive.model.network.naver.route.NaverRouteModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NaverRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(18, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.naverUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(NaverApis::class.java)

    fun getNaverRoute(start: Location, goal: Location): NaverRouteModel? {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("start", "${start.longitude},${start.latitude}")
        queryMap.put("goal", "${goal.longitude},${goal.latitude}")
        queryMap.put("option", "trafast")



        val places = api.getPlaceRouteInfo(BuildConfig.naverClient, BuildConfig.naverSecret, queryMap)
        return try {
            val result = places.execute()
            Log.d("naver route success", "네이버 경로 검색 결과: ${result.code()}")
            Log.d("naver route success", "네이버 경로 검색 검색 결과: ${result.message()}")
            result.body()
        } catch (e: Exception) {
            Log.d("naver route error", "네이버 경로 검색 검색 error: ${e.message}")
            null
        }
    }
}