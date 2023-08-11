package com.example.cargive.model.network.google

import android.util.Log
import com.example.cargive.BuildConfig
import com.example.cargive.model.network.google.route.PlaceRouteResponseModel
import com.example.cargive.model.network.google.search.GooglePlaceSearchModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GoogleRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(18, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.google)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(GoogleApis::class.java)

    private val directionRetrofit = Retrofit.Builder().baseUrl(BuildConfig.directionUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var directionApi = directionRetrofit.create(GoogleApis::class.java)


    fun getPlaceInfoByQuery(
        keyword: String,
        latitude: Double,
        longitude: Double
    ): GooglePlaceSearchModel? {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("keyword", keyword)
        queryMap.put("type", "parking")
        queryMap.put("radius", "2000")
        queryMap.put("location", "$latitude,$longitude")
//        queryMap.put("rankby","distance")
        queryMap.put("key", BuildConfig.webKey)


        val places = api.getPlaceInfo(queryMap)
        return try {
            val result = places.execute()
            Log.d("search success", "장소 검색 결과: ${result.code()}")
            Log.d("search success", "장소 검색 결과: ${result.message()}")
            result.body()
        } catch (e: Exception) {
            Log.d("search error", "레포 필터없는 검색 error: ${e.message}")
            null
        }
    }

    fun getPlaceRouteResult(latitude: Double, longitude: Double,origin: String): PlaceRouteResponseModel? {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("destination", "$latitude,$longitude")
        queryMap.put("origin", "place_id:$origin")
        queryMap.put("key", BuildConfig.webKey)
        queryMap.put("mode", "transit")

        val routes = directionApi.getPlaceRouteInfo(queryMap)
        return try {
            val result = routes.execute()
            Log.d("route success", "경로 검색 결과: ${result.code()}")
            Log.d("route success", "경로 검색 결과: ${result.message()}")
            result.body()
        } catch (e: Exception) {
            Log.d("search error", "경로 검색 error: ${e.message}")
            null
        }
    }

}