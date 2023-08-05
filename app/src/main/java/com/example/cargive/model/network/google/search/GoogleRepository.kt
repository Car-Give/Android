package com.example.cargive.model.network.google.search

import android.util.Log
import com.example.cargive.BuildConfig
import com.example.cargive.model.network.search.KaKaoSearchApi
import com.example.cargive.model.network.search.KaKaoSearchResultModel
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

    private var api = retrofit.create(GoogleSearchApi::class.java)

    fun getPlaceInfoByQuery(keyword: String, latitude: Double, longitude: Double): GooglePlaceSearchModel? {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("keyword","주차장")
        queryMap.put("type", "parking")
        queryMap.put("radius", "2000")
        queryMap.put("location","37.3411436,126.732770474")
//        queryMap.put("rankby","distance")
        queryMap.put("key", BuildConfig.webKey)


        val places = api.getPlaceInfo(queryMap)
        return try{
            val result = places.execute()
            Log.d("search success", "레포 필터없는 검색 결과: ${result.code()}")
            Log.d("search success", "레포 필터없는 검색 결과: ${result.message()}")
            result.body()
        }catch (e : Exception){
            Log.d("search error", "레포 필터없는 검색 error: ${e.message}")
            null
        }
    }

}