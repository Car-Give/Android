package com.example.cargive.feat.map

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.cargive.databinding.ActivityMainBinding
import com.example.cargive.databinding.MainNavheaderBinding
import com.example.cargive.model.network.search.KakaoRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)


    private val REQUEST_PERMISSION_CODE = 1
    private var googleMap: GoogleMap? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: MainNavheaderBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var locationManager: LocationManager
    private var backPressed: Long = 0
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }else{
                if (System.currentTimeMillis() > backPressed + 2500) {
                    backPressed = System.currentTimeMillis()
                    Toast.makeText(applicationContext, "Back 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                if (System.currentTimeMillis() <= backPressed + 2500) {
                    finishAffinity()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        nav = MainNavheaderBinding.bind(binding.navigationView.getHeaderView(0))
        setContentView(binding.root)

        binding.mapView.getMapAsync(this)

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.menuBtn.setOnClickListener {
            binding.navigationView.bringToFront()
            binding.drawerLayout.invalidate()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        setNavListener()
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        this.onBackPressedDispatcher.addCallback(this, callback)
        if (checkPermissions()) {
//            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }


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





    }


//    private fun getCurrentLocation() {
//        if(::locationManager.isInitialized) {
//            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        }
//
//        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        if(isGpsEnabled) {
//            when {
//            }
//        }
//    }


    private fun searchPlaces(query: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing && longitude != 0.0 && latitude != 0.0 ) {

            }
        }
    }

    private fun setNavListener() {
        nav.profileLink.setOnClickListener {
            Toast.makeText(this@MainActivity.applicationContext, "프로필 눌림!", Toast.LENGTH_SHORT).show()
            Log.d("btn", "프로필 링크 눌림!")
        }
        nav.aroundBtn.setOnClickListener {

        }
        nav.mycarBtn.setOnClickListener {

        }
        nav.favoriteBtn.setOnClickListener {

        }
        nav.useInfo.setOnClickListener {

        }
        nav.useHistory.setOnClickListener {

        }
        nav.announcement.setOnClickListener {

        }
        nav.customerService.setOnClickListener {

        }
        nav.appSettings.setOnClickListener {

        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
        return false
    }

    private fun checkPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

//        initMap()
    }





    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        if (checkPermissions()) {
            setLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    fun setLocationUpdates() {
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10.0f, locationListener)
    }
    val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            // 위치가 변경되었을 때 호출됩니다.
            latitude = location.latitude
            longitude = location.longitude
            googleMap?.let {
                if (longitude != 0.0 && latitude != 0.0) {
                    val marker = LatLng(latitude, longitude)
                    it.addMarker(MarkerOptions().position(marker).title("여기"))
                    it.moveCamera(CameraUpdateFactory.newLatLng(marker))
                    it.moveCamera(CameraUpdateFactory.zoomTo(15f))
                }
            }
        }

        override fun onLocationChanged(locations: MutableList<Location>) {
            super.onLocationChanged(locations)
            //위치가 변경되어 위치가 일괄 전달될 때 호출됩니다.

        }
        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            // 사용자가 GPS를 끄는 등의 행동을 해서 위치값에 접근할 수 없을 때 호출됩니다.
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            // 사용자가 GPS를 on하는 등의 행동을 해서 위치값에 접근할 수 있게 되었을 때 호출됩니다.
        }

        override fun onFlushComplete(requestCode: Int) {
            super.onFlushComplete(requestCode)
            //플러시 작업이 완료되고 플러시된 위치가 전달된 후 호출됩니다.
        }
    }


}