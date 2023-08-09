package com.example.cargive.model.network.kakao.search

import android.util.Log
import com.example.cargive.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class KakaoRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(18, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.api)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(KaKaoSearchApi::class.java)

    fun getPlaceInfoByQuery(query: String, x: Double, y: Double, page: Int): KaKaoSearchResultModel? {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("query",query)
        queryMap.put("category_group_code", "PK6")
        queryMap.put("radius", "2000")
        queryMap.put("x", x.toString())
        queryMap.put("y", y.toString())
        queryMap.put("sort", "distance")
        queryMap.put("page", page.toString())

        Log.d("장소 검색 호출", "$page 페이지 검색")

        val places = api.getPlaceInfo(queryMap, "KakaoAK ${BuildConfig.restKey}")
        return try{
            val result = places.execute()
            Log.d("result", "레포 필터없는 검색 결과: ${result.code()}")
            Log.d("result", "레포 필터없는 검색 결과: ${result.message()}")
            result.body()
        }catch (e : Exception){
            Log.d("error", "레포 필터없는 검색 error: ${e.message}")
            null
        }
    }

}