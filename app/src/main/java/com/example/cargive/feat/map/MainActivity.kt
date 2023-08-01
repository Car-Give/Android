package com.example.cargive.feat.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.cargive.databinding.ActivityMainBinding
import com.example.cargive.model.network.search.KakaoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private val repository = KakaoRepository()
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        key hash 확인용 코드
//        var packageInfo: PackageInfo? = null
//        try {
//            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
//        for (signature in packageInfo!!.signatures) {
//            try {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            } catch (e: NoSuchAlgorithmException) {
//                Log.d("KeyHash", "Unable to get MessageDigest. signature=$signature")
//
//            }
//        }
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        startTracking()

//        val marker = MapPOIItem()
//        marker.itemName = titleBar
//        marker.mapPoint = mapPoint
//        marker.markerType = MapPOIItem.MarkerType.RedPin
//        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
//        mapView.addPOIItem(marker)


    }
    @SuppressLint("MissingPermission")
    private fun startTracking() {
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        //위도 , 경도
        userNowLocation?.latitude?.let {
            latitude = it
        }
        userNowLocation?.longitude?.let {
            longitude = it
        }
        Log.d("x, y", "latitude: $latitude  longitude: $longitude")
//        val uNowPosition = MapPoint.mapPointWithGeoCoord(latitude!!, longitude!!)
//        val marker = MapPOIItem()
//        marker.itemName = "현 위치"
//        marker.mapPoint =uNowPosition
//        marker.markerType = MapPOIItem.MarkerType.RedPin
//        marker.selectedMarkerType = MapPOIItem.MarkerType.BluePin
//        binding.mapView.addPOIItem(marker)
//        searchPlaces("주차장")
    }

    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun searchPlaces(query: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing && longitude != 0.0 && latitude != 0.0 ) {
                val resultDeferred = coroutine.async {
                    repository.getPlaceInfoByQuery(query, longitude, latitude, page)
                }
                val result = resultDeferred.await()
                Log.d("결과", result.toString())
                Handler(Looper.getMainLooper()).postDelayed({stopTracking()},  2000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }


}