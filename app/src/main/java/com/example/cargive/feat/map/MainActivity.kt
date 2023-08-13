package com.example.cargive.feat.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.BuildConfig
import com.example.cargive.R
import com.example.cargive.databinding.ActivityMainBinding
import com.example.cargive.databinding.MainNavheaderBinding
import com.example.cargive.model.network.google.search.GooglePlaceSearchModel
import com.example.cargive.model.network.google.GoogleRepository
import com.example.cargive.model.network.google.search.Results
import com.example.cargive.model.network.naver.NaverRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, OnMarkerClickListener {
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET
    )
    private val googleRepository = GoogleRepository()
    private val naverRepository = NaverRepository()
    private val REQUEST_PERMISSION_CODE = 1
    private var googleMap: GoogleMap? = null
    private var cMarker: Marker? = null
    private var pMarker: Marker? = null
    private var currentId: String? = null
    private var placeId: String? = null
    private var location: Location? = null
    private var pLocation: Location? = null


    //    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //    private val defaultLocation = LatLng(37.3411, 126.7326)
//    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
//    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
//    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
//    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
    private var lastKnownLocation: Location? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: MainNavheaderBinding
//    private var placeListFragment: SearchParkingLotFragment? = null
//    private var polyline: Polyline? = null
    private var naverPolyline: Polyline? = null

    //    private var placeNavFragment: NavParkingLotFragment? = null
    private var lastSearch = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var locationManager: LocationManager
    private var backPressed: Long = 0
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            } else {
                if(binding.placeInfoFrame.visibility == View.VISIBLE) {
                    binding.placeInfoFrame.visibility = View.GONE
                    binding.searchResultFrame.visibility = View.VISIBLE
                } else {
                    if (binding.searchResultFrame.visibility == View.VISIBLE) {
                        binding.searchResultFrame.visibility = View.GONE
                        binding.selectFrame.visibility = View.VISIBLE
                        binding.currentLocaiton.visibility = View.VISIBLE
                        return
                    } else {
                        if(binding.choiceFrame.visibility == View.VISIBLE) {
                            binding.choiceFrame.visibility = View.GONE
                            binding.menuBtn.visibility = View.GONE
                            binding.selectFrame.visibility = View.VISIBLE
                            binding.toolbar.visibility = View.VISIBLE
                            binding.currentLocaiton.visibility = View.VISIBLE
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            return
                        } else {
                            if(binding.selectFrame.visibility == View.VISIBLE) {
                                googleMap?.clear()
                                val markerIcon = getMarkerIconFromDrawable(
                                    ContextCompat.getDrawable(
                                        this@MainActivity,
                                        R.drawable.point
                                    )
                                )
                                val marker = LatLng(latitude, longitude)
                                val markerOptions = MarkerOptions()
                                    .position(marker)
                                    .icon(markerIcon)
                                    .anchor(0.5f, 1.0f)
                                cMarker = googleMap?.addMarker(markerOptions)
                                naverPolyline = null
                                binding.selectFrame.visibility = View.VISIBLE
                                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                                binding.menuBtn.visibility = View.GONE
                                if (System.currentTimeMillis() > backPressed + 2500) {
                                    backPressed = System.currentTimeMillis()
                                    Toast.makeText(
                                        applicationContext,
                                        "Back 버튼을 한번 더 누르면 종료합니다.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return
                                }

                                if (System.currentTimeMillis() <= backPressed + 2500) {
                                    finishAffinity()
                                }
                            }
                        }
                    }
                }
            }
            if(binding.myCarFrame.visibility == View.VISIBLE) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.uiFrame)
                constraintSet.connect(binding.linearLayout.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.applyTo(binding.uiFrame)
                binding.myCarFrame.visibility = View.GONE
                binding.choiceFrame.visibility = View.GONE
                binding.menuBtn.visibility = View.GONE
                binding.selectFrame.visibility = View.VISIBLE
                binding.toolbar.visibility = View.VISIBLE
                binding.currentLocaiton.visibility = View.VISIBLE
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                return
            } else {
                if(binding.selectFrame.visibility == View.VISIBLE) {
                    if (System.currentTimeMillis() > backPressed + 2500) {
                        backPressed = System.currentTimeMillis()
                        Toast.makeText(
                            applicationContext,
                            "Back 버튼을 한번 더 누르면 종료합니다.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return
                    }

                    if (System.currentTimeMillis() <= backPressed + 2500) {
                        finishAffinity()
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        nav = MainNavheaderBinding.bind(binding.navigationView.getHeaderView(0))
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.list_stripe)
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.menuBtn.setOnClickListener {
//            binding.navigationView.bringToFront()
//            binding.drawerLayout.invalidate()
//            binding.drawerLayout.openDrawer(GravityCompat.START)
            if(binding.placeInfoFrame.visibility == View.VISIBLE) {
                binding.placeInfoFrame.visibility = View.GONE
                binding.choiceFrame.visibility = View.GONE
                binding.myCarFrame.visibility = View.GONE
                binding.searchResultFrame.visibility = View.VISIBLE
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                binding.toolbar.visibility = View.VISIBLE
                binding.menuBtn.visibility = View.GONE
                binding.placeInfoFrame.visibility = View.GONE
                binding.choiceFrame.visibility = View.GONE
                binding.searchResultFrame.visibility = View.GONE
                binding.myCarFrame.visibility = View.GONE
                binding.selectFrame.visibility = View.VISIBLE
                binding.currentLocaiton.visibility = View.VISIBLE
            }
        }
        setNavListener()

        Places.initialize(applicationContext, BuildConfig.appKey)
        placesClient = Places.createClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        this.onBackPressedDispatcher.addCallback(this, callback)
        if (checkPermissions()) {
//            setLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        binding.searchName.setOnClickListener {
            binding.searchName.isCursorVisible = true
        }
        binding.searchName.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.searchName.text.toString().isNotBlank()) {
                    binding.placeInfoFrame.visibility = View.GONE
                    searchPlaces(binding.searchName.text.toString())
                    lastSearch = binding.searchName.text.toString()
                    binding.searchName.setText("")
                }
            }
            true
        }
        binding.searchIcon.setOnClickListener {
            if (binding.searchName.text.toString().isNotBlank()) {
                binding.placeInfoFrame.visibility = View.GONE
                searchPlaces(binding.searchName.text.toString())
                lastSearch = binding.searchName.text.toString()
                binding.searchName.setText("")
            }
        }

        binding.findMyCar.setOnClickListener {
            binding.myCarFrame.visibility = View.VISIBLE
            binding.selectFrame.visibility = View.GONE
            binding.menuBtn.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.uiFrame)
            constraintSet.connect(binding.linearLayout.id, ConstraintSet.TOP, binding.myCarFrame.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(binding.uiFrame)
        }
        binding.parkCar.setOnClickListener {
            binding.currentLocaiton.visibility = View.GONE
            binding.selectFrame.visibility = View.GONE
            binding.menuBtn.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
            binding.choiceFrame.visibility = View.VISIBLE
        }

        binding.currentLocaiton.setOnClickListener {
            googleMap?.let {
                val marker = LatLng(latitude, longitude)
                it.moveCamera(CameraUpdateFactory.newLatLng(marker))
                it.moveCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }

        binding.searchResultFrame.setOnClickListener {
            when(binding.constraintLayout.visibility) {
                View.VISIBLE -> {
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.uiFrame)
                    constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.BOTTOM, binding.uiFrame.id, ConstraintSet.BOTTOM)
                    constraintSet.applyTo(binding.uiFrame)
                    val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//                    params.bottomMargin = 20
                    binding.searchResultFrame.layoutParams = params
                    binding.constraintLayout.visibility = View.GONE
                    binding.searchResult.visibility = View.GONE
                }
                View.GONE -> {
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.uiFrame)
                    constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.TOP, binding.uiFrame.id, ConstraintSet.TOP)
                    constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.BOTTOM, binding.uiFrame.id, ConstraintSet.BOTTOM)
                    constraintSet.applyTo(binding.uiFrame)
                    val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
//                    params.bottomMargin = 20
//                    params.topMargin = 120
                    binding.searchResultFrame.layoutParams = params
                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.searchResult.visibility = View.VISIBLE
                }
            }
        }

        binding.findCar.setOnClickListener {

        }
        binding.findParkingLot.setOnClickListener {

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


    private fun searchPlaces(keyword: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing && longitude != 0.0 && latitude != 0.0) {
                val resultDeferred = coroutine.async {
                    googleRepository.getPlaceInfoByQuery(
                        keyword = keyword,
                        latitude = latitude,
                        longitude = longitude
                    )
                }
                val result = resultDeferred.await()
                Log.d("search result", result.toString())
                result?.let {
                    withContext(Dispatchers.Main) {
                        closeKeyboard()
                        sortPlaceList(keyword, result)
                    }
                }
            }
        }
    }

    private fun routePlace(name: String, lat: Double, lng: Double) {
        naverPolyline?.remove()
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing) {
//                val resultDeferred = coroutine.async {
//                    googleRepository.getPlaceRouteResult(latitude, longitude, placeId!!)
//                }
//                val result = resultDeferred.await()
//                Log.d("search result", result.toString())
//                result?.let {
//                    withContext(Dispatchers.Main) {
//                        val polylineOptions = PolylineOptions()
//                        polylineOptions.addAll(PolyUtil.decode(it.routes[0].overview_polyline.points))
//                        polylineOptions.color(Color.BLUE)
//                        polyline = googleMap?.addPolyline(polylineOptions)
//                    }
//                }
                val placeLocation = Location(name)
                placeLocation.latitude = lat
                placeLocation.longitude = lng

                binding.placeInfoFrame.visibility = View.VISIBLE
                Log.d("naver", "cPlace: $location, pPlace: $pLocation")
                val naverDeferred = coroutine.async {
                    naverRepository.getNaverRoute(location!!, placeLocation)
                }
                val naver = naverDeferred.await()
                if(naver != null) {
                    Log.d("naver", "route: ${naver.route}")
                    val route = naver.route.trafast
                    val pathContainer : MutableList<LatLng> = mutableListOf()
                    for(path_cords in route){
                        for(path_cords_xy in path_cords.path){
                            //구한 경로를 하나씩 path_container에 추가해줌
                            pathContainer.add(LatLng(path_cords_xy[1], path_cords_xy[0]))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val naverPolyOptions = PolylineOptions()
                        naverPolyOptions.addAll(pathContainer)
                        naverPolyOptions.color(Color.BLUE)
                        naverPolyline = googleMap?.addPolyline(naverPolyOptions)
                    }

                }
            }
        }
    }

    private fun findMyCar() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!this@MainActivity.isFinishing) {
//                addMyCarMarker()
            }
        }
    }

    private fun sortPlaceList(keyword: String, result: GooglePlaceSearchModel) {
        binding.searchPlaceName.text = keyword
        location = Location("Current Location")
        location?.longitude = longitude
        location?.latitude = latitude
        binding.searchResultFrame.visibility = View.VISIBLE

        if (result.results.isNotEmpty()) {
            val sorted = result.results.sortedBy {
                val placeLocation = Location("place")
                placeLocation.latitude = it.geometry.location.lat
                placeLocation.longitude = it.geometry.location.lng
                location?.distanceTo(placeLocation)
            }
            Log.d("정렬됨", "sorted: $sorted")
            showPlaceList(sorted)
        } else {
            binding.searchResult.visibility = View.GONE
            binding.cautionFrame.visibility = View.VISIBLE
        }
    }

    fun showPlaceList(list: List<Results>) {
//        placeListFragment =
//            SearchParkingLotFragment(keyword, result.results, placesClient, pLatitude, pLongitude, this)
//        placeListFragment?.show(supportFragmentManager, placeListFragment?.tag)
        val arr1: MutableList<String> = mutableListOf("거리 순", "추천 순", "인기 순")
        binding.sortSpinner
        val spinnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1)
        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.setSelection(0)
        binding.sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {

                        }
                        1 -> {

                        }
                        2 -> {

                        }
                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        binding.selectFrame.visibility = View.GONE
        binding.searchResult.visibility = View.VISIBLE
        binding.menuBtn.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val adapter = ParkingLotListAdapter(location!!, placesClient, this)
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(this)
        adapter.submitList(list)
        adapter
        binding.drawerLayout.closeDrawers()
    }

    fun showPlaceNav(
        result: Results,
        distance: Int,
        bitmap: Bitmap?,
        phoneNumber: String,
        address: String,
        placeId: String
    ) {
        binding.searchResultFrame.visibility = View.GONE
        binding.placeName.text = result.name
        naverPolyline?.remove()

        bitmap?.let {
            binding.placeImg.setImageBitmap(it)
        }
        this.placeId = placeId
        binding.internalCall.text = phoneNumber
        binding.detailAddress.text = address

        binding.placeDistance.text = distance.toString() + "m"
        binding.placeInfoFrame.visibility = View.VISIBLE
        pLocation = Location(result.name)
        pLocation?.latitude = result.geometry.location.lat
        pLocation?.longitude = result.geometry.location.lng
        binding.navigatePlace.setOnClickListener {
            Log.d("lat,lng","latitude: ${pLocation!!.latitude}, longitude: ${pLocation!!.longitude}")
            routePlace(binding.placeName.text.toString(), pLocation!!.latitude, pLocation!!.longitude)
        }

//        placeNavFragment = NavParkingLotFragment(result, placesClient, cLocation)
//        placeNavFragment?.show(supportFragmentManager, placeNavFragment?.tag)
    }


    private fun setNavListener() {
        nav.profileLink.setOnClickListener {
//            Toast.makeText(this@MainActivity.applicationContext, "프로필 눌림!", Toast.LENGTH_SHORT)
//                .show()
//            Log.d("btn", "프로필 링크 눌림!")
            //프로필 화면으로 전환
        }
        nav.aroundBtn.setOnClickListener {
            //주변 주차장 검색
            searchPlaces("주차장")
            binding.currentLocaiton.visibility = View.GONE
        }
        nav.mycarBtn.setOnClickListener {
            //내 차 화면으로 전환
        }
        nav.favoriteBtn.setOnClickListener {
            //즐겨찾기 화면으로 전환
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

    override fun onSaveInstanceState(outState: Bundle) {
        googleMap?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
        return false
    }

    private fun checkPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setLocationUpdates()
//        initMap()
//        updateLocation()
//        getDeviceLocation()
    }


    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        Log.d("map", "map ready!")
        if (checkPermissions()) {
//            setLocationUpdates()
            googleMap?.setOnMarkerClickListener(this)
            requestLocation()
//            showCurrentPlace()
//            updateLocation()
//            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // location 객체를 사용하여 현재 위치 정보를 얻어옵니다.
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("location request", "latitude: $latitude  and longitude: $longitude")
                    googleMap?.let {
                        if (longitude != 0.0 && latitude != 0.0) {
                            val markerIcon = getMarkerIconFromDrawable(
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.point
                                )
                            )
                            val marker = LatLng(latitude, longitude)
                            val markerOptions = MarkerOptions()
                                .position(marker)
                                .icon(markerIcon)
                                .anchor(0.5f, 1.0f)
                            cMarker = it.addMarker(markerOptions)
                            it.moveCamera(CameraUpdateFactory.newLatLng(marker))
                            it.moveCamera(CameraUpdateFactory.zoomTo(15f))
                            val placesClient = Places.createClient(this)
                            val request = FindCurrentPlaceRequest.builder(listOf(Place.Field.ID))
                                .build()

                            placesClient.findCurrentPlace(request)
                                .addOnSuccessListener { response ->
                                    if (response.placeLikelihoods.isNotEmpty()) {
                                        currentId = response.placeLikelihoods[0].place.id
                                        Log.d("place", currentId.toString())
                                        // 얻은 placeId를 사용하여 장소의 세부 정보를 요청하거나 다른 작업을 수행할 수 있습니다.
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // 현재 위치 검색 실패 시 처리
                                }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 위치 정보 가져오기 실패 처리
            }
    }

    @SuppressLint("MissingPermission")
    fun setLocationUpdates() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            100,
            5.0f,
            locationListener
        )
    }

    val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("location", location.toString())
            // 위치가 변경되었을 때 호출됩니다.
            latitude = location.latitude
            longitude = location.longitude
            googleMap?.let {
                Log.d("listener", "latitude: $latitude  and longitude: $longitude")
                if (longitude != 0.0 && latitude != 0.0) {
                    if (cMarker != null) {
                        cMarker?.remove()
                    }

                    val markerIcon = getMarkerIconFromDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.point
                        )
                    )
                    val marker = LatLng(latitude, longitude)
                    val markerOptions = MarkerOptions()
                        .position(marker)
                        .icon(markerIcon)
                        .anchor(0.5f, 1.0f)
                    cMarker = it.addMarker(markerOptions)
//                    it.moveCamera(CameraUpdateFactory.newLatLng(marker))
//                    it.moveCamera(CameraUpdateFactory.zoomTo(15f))
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

    fun getMarkerIconFromDrawable(drawable: Drawable?): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0,
            drawable?.intrinsicHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun addPlaceMarker(pLat: Double, pLng: Double, name: String, call: String, address: String, distance: Int, bitmap: Bitmap?) {
        googleMap?.let {
            val markerIcon = getMarkerIconFromDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.parking_location
                )
            )
            val marker = LatLng(pLat, pLng)
            val markerOptions = MarkerOptions()
                .position(marker)
                .title(name)
                .icon(markerIcon)
                .snippet("call:$call,address:$address,distance:$distance,bitmap:$bitmap,lat:$pLat,lng:$pLng")
                .anchor(0.5f, 1.0f)
            val placeMarker = it.addMarker(markerOptions)

            pLocation = Location(name)
            pLocation?.latitude = pLat
            pLocation?.longitude = pLng
            Log.d("location place", "lat: $pLat, lng: $pLng")
        }
    }

    fun addMyCarMarker(pLat: Double, pLng: Double) {
        googleMap?.let {
            val markerIcon = getMarkerIconFromDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mycar_location
                )
            )
            val marker = LatLng(pLat, pLng)
            val markerOptions = MarkerOptions()
                .position(marker)
                .title("내 차!")
                .icon(markerIcon)
                .anchor(0.5f, 1.0f)
            pMarker = it.addMarker(markerOptions)
        }
    }

    fun removePlaceMarker() {
        googleMap?.clear()
        val markerIcon = getMarkerIconFromDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.point
            )
        )
        val marker = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(marker)
            .icon(markerIcon)
            .anchor(0.5f, 1.0f)
        cMarker = googleMap?.addMarker(markerOptions)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    //    edittext의 키보드 제거
    fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchName.windowToken, 0)
        binding.searchName.isCursorVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.navigationView.bringToFront()
                binding.drawerLayout.invalidate()
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }

//            R.id.refresh_button -> {
//                if(refresh) {
//                    refresh = false
//                    callTokenHistory()
//                }
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        binding.searchResultFrame.visibility = View.GONE
        binding.placeName.text = p0.title
        val split = p0.snippet?.split(",")
        Log.d("split", split.toString())
        var lat = ""
        var lng = ""
        split?.forEach {
            val values = it.split(":")
            Log.d("split", "valeus: ${values.toString()}")
            when(values[0]) {
                "call" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.internalCall.text = values[1]
                    }
                }
                "address" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.detailAddress.text = values[1]
                    }
                }
                "distance" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.placeDistance.text = values[1]+"m"
                    }
                }
                "bitmap" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.placeImg.setImageBitmap(convertStringToBitmap(values[1]))
                    }
                }
                "lat" -> {
                    if(!values[1].isNullOrBlank()) {
                        lat = values[1]
                    }
                }
                "lng" -> {
                    if(!values[1].isNullOrBlank()) {
                        lng = values[1]
                    }
                }
            }
        }
        pLocation = Location(p0.title)
        pLocation?.latitude = lat.toDouble()
        pLocation?.longitude = lng.toDouble()
        binding.placeInfoFrame.visibility = View.VISIBLE
        Log.d("lat,lng", "latitude: ${pLocation!!.latitude}, longitude: ${pLocation!!.longitude}")
        Log.d("marker", p0.title.toString())
        binding.navigatePlace.setOnClickListener {
            Log.d("lat,lng","latitude: ${pLocation!!.latitude}, longitude: ${pLocation!!.longitude}")
            routePlace(binding.placeName.text.toString(), pLocation!!.latitude, pLocation!!.longitude)
        }
//        showPlaceNav()


        return true
    }

    fun convertStringToBitmap(encodedBitmap: String): Bitmap? {
        try {
            val decodedBytes = Base64.decode(encodedBitmap, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}