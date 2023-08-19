package com.example.cargive.feat.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.BuildConfig
import com.example.cargive.R
import com.example.cargive.databinding.ActivityMainBinding
import com.example.cargive.databinding.MainNavheaderBinding
import com.example.cargive.feat.etc.AnnoucementActivity
import com.example.cargive.feat.etc.UsageHistoryActivity
import com.example.cargive.model.network.google.search.GooglePlaceSearchModel
import com.example.cargive.model.network.google.GoogleRepository
import com.example.cargive.model.network.google.search.Results
import com.example.cargive.model.network.naver.NaverRepository
import com.google.android.gms.common.api.ApiException
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
import okhttp3.internal.wait
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt
import kotlin.system.exitProcess


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

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: MainNavheaderBinding
    private var naverPolyline: Polyline? = null

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
                    if(binding.selectFrame.visibility == View.VISIBLE) {
                        binding.placeInfoFrame.visibility = View.GONE
                        return
                    } else {
                        binding.placeInfoFrame.visibility = View.GONE
                        binding.searchResultFrame.visibility = View.VISIBLE
                        return
                    }
                } else {
                    if (binding.searchResultFrame.visibility == View.VISIBLE) {
                        setVisibility(0)
                        Log.d("검색 결과 제거", "검색 결과 제거")
                        binding.linearLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            topMargin = binding.linearLayout.marginTop - 40
                        }
                        binding.searchResultFrame.visibility = View.GONE
                        return
                    } else {
                        if(binding.choiceFrame.visibility == View.VISIBLE) {
                            setVisibility(0)
                            binding.choiceFrame.visibility = View.GONE
                            return
                        } else {
                            setVisibility(1)

                            if(binding.myCarFrame.visibility == View.VISIBLE) {
                                setVisibility(2)
                                return
                            } else {
                                backPressed()
                                return
                            }
                        }
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
            if(binding.placeInfoFrame.visibility == View.VISIBLE) {
                binding.placeInfoFrame.visibility = View.GONE
                binding.searchResultFrame.visibility = View.VISIBLE
            } else {
                if(binding.searchResultFrame.visibility == View.VISIBLE) {
                    setVisibility(3)
                    binding.linearLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        topMargin = binding.linearLayout.marginTop - 40
                    }
                } else {
                    if(binding.choiceFrame.visibility == View.VISIBLE) {
                        setVisibility(0)
                        binding.choiceFrame.visibility = View.GONE
                    } else {
                        if(binding.myCarFrame.visibility == View.VISIBLE) {
                            setVisibility(2)
                            binding.myCarFrame.visibility = View.GONE
                        }
                    }
                }

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
            binding.noticeFrame.visibility = View.GONE
        }
        binding.parkCar.setOnClickListener {
            binding.currentLocaiton.visibility = View.GONE
            binding.selectFrame.visibility = View.GONE
            binding.noticeFrame.visibility = View.GONE
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
                    setResultFrameSize(1)
                    binding.constraintLayout.visibility = View.GONE
                    binding.searchResult.visibility = View.GONE
                    if(binding.cautionFrame.visibility == View.VISIBLE) {
                        binding.cautionNotice.visibility = View.VISIBLE
                    }
                }
                View.GONE -> {
                    setResultFrameSize(0)
                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.searchResult.visibility = View.VISIBLE
//                    binding.cautionNotice.visibility = View.VISIBLE
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

        val arr1: MutableList<String> = mutableListOf("거리 순", "인기 순")
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr1)
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
                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }


    private fun searchPlaces(keyword: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing && longitude != 0.0 && latitude != 0.0) {
                val resultDeferred = coroutine.async {
                    googleRepository.getPlaceInfoByQuery(keyword = keyword, latitude = latitude, longitude = longitude)
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

        var bitmap : Bitmap? = null

        if (result.results.isNotEmpty()) {
            val sorted = result.results.sortedBy {
                val placeLocation = Location("place")
                placeLocation.latitude = it.geometry.location.lat
                placeLocation.longitude = it.geometry.location.lng
                it.distance = location?.distanceTo(placeLocation)!!.roundToInt()
                location?.distanceTo(placeLocation)
            }.toMutableList()
            val appropriateSorted = sorted.filter {
                it.distance!! <= 2000
            }
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS,
                Place.Field.PRICE_LEVEL, Place.Field.WEBSITE_URI
            )
            val coroutine = CoroutineScope(Dispatchers.IO)
            coroutine.launch {
                val resultDeferred = coroutine.async {
                    appropriateSorted.forEach {
                        val request = FetchPlaceRequest.newInstance(it.place_id, placeFields)
                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response: FetchPlaceResponse ->
                                val place = response.place
                                Log.d("place", "장소? : ${place.name}")
                                Log.d("place", "주소? : ${place.address}")
                                Log.d("place", "phone?: ${place.phoneNumber}")
                                if(place.phoneNumber.isNullOrBlank()) {
                                    it.call = ""
                                } else {
                                    it.call = place.phoneNumber
                                }
                                it.address = place.address
//                    Log.d("name", place.name)
                                // Get the photo metadata.
                                val metada = place.photoMetadatas
                                if (metada == null || metada.isEmpty()) {
                                    Log.d("장소 결과", "No photo metadata.")
                                    it.bitmaps = null
                                    return@addOnSuccessListener
                                }
                                val photoMetadata = metada.last()
                                // Get the attribution text.
                                val attributions = photoMetadata?.attributions

                                // Create a FetchPhotoRequest.
                                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(binding.placeImg.maxWidth) // Optional.
                                    .setMaxHeight(binding.placeImg.maxWidth) // Optional.
                                    .build()
                                placesClient.fetchPhoto(photoRequest)
                                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                                        bitmap = fetchPhotoResponse.bitmap
                                        Log.d("getbitmap", "bitmap: $bitmap")
                                        it.bitmaps = bitmap
                                    }.addOnFailureListener { exception: Exception ->
                                        if (exception is ApiException) {
                                            Log.d("getbitmap", "Place not found: " + exception.message)
                                            val statusCode = exception.statusCode
                                        }
                                    }

                            }
                            .addOnFailureListener {
                                Log.e("place", "Place not found: " + it.message)
                            }
                    }
                }
                val result = resultDeferred.await()
                delay(1000)
                withContext(Dispatchers.Main) {
                    showPlaceList(appropriateSorted)
                }
            }
        } else {
            binding.searchResult.visibility = View.GONE
            binding.cautionFrame.visibility = View.VISIBLE
        }
    }

    fun showPlaceList(list: List<Results>) {
//        placeListFragment =
//            SearchParkingLotFragment(keyword, result.results, placesClient, pLatitude, pLongitude, this)
//        placeListFragment?.show(supportFragmentManager, placeListFragment?.tag)

        binding.selectFrame.visibility = View.GONE
        binding.noticeFrame.visibility = View.GONE
        binding.searchResult.visibility = View.VISIBLE
        binding.menuBtn.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val adapter = ParkingLotListAdapter(location!!, placesClient, this)
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(this)
        adapter.submitList(list)
        Handler(Looper.getMainLooper()).postDelayed({binding.drawerLayout.closeDrawers()}, 500)
        removePlaceMarker()
        adapter.addMarker()
    }

    fun showPlaceNav(result: Results, distance: Int, bitmap: Bitmap?,
                     phoneNumber: String, address: String, placeId: String) {
        binding.placeImg.setImageBitmap(null)
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

        binding.navFrame.setOnClickListener {
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
            setResultFrameSize(0)
            binding.constraintLayout.visibility = View.VISIBLE
            binding.searchPlaceName.visibility = View.VISIBLE
            binding.sortSpinner.visibility = View.VISIBLE
            binding.linearLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = binding.linearLayout.marginTop + 40
            }
            binding.currentLocaiton.visibility = View.GONE

            searchPlaces("주차장")
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
            val intent = Intent(this, UsageHistoryActivity::class.java)
            startActivity(intent)
        }
        nav.announcement.setOnClickListener {
            val intent = Intent(this, AnnoucementActivity::class.java)
            startActivity(intent)
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
        binding.drawerLayout.closeDrawers()
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
                                .title("현재 위치")
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
                        .title("현재 위치")
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
                    R.drawable.parking_locations
                )
            )
            var bitmapString = ""
            val stream = ByteArrayOutputStream()
            bitmap?.let {
                it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                bitmapString = Base64.encodeToString(bytes, 0)
            }
            val marker = LatLng(pLat, pLng)
            val markerOptions = MarkerOptions()
                .position(marker)
                .title(name)
                .icon(markerIcon)
                .snippet("call:$call,address:$address,distance:$distance,bitmap:$bitmapString,lat:$pLat,lng:$pLng")
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
            .title("현재 위치")
            .anchor(0.5f, 1.0f)
        cMarker = googleMap?.addMarker(markerOptions)
        pMarker = null
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
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        binding.searchResultFrame.visibility = View.GONE
        binding.placeName.text = p0.title
        if(p0.title == "현재 위치") {
            return true
        }
        val split = p0.snippet?.split(",")
        Log.d("split", split.toString())
        var lat = ""
        var lng = ""
        split?.forEach {
            val values = it.split(":")
            Log.d("split", "valeus: $values")
            when(values[0]) {
                "call" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.internalCall.text = values[1]
                    } else {
                        binding.internalCall.text = ""
                    }
                }
                "address" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.detailAddress.text = values[1]
                    } else {
                        binding.detailAddress.text = ""
                    }
                }
                "distance" -> {
                    if(!values[1].isNullOrBlank()) {
                        binding.placeDistance.text = values[1]+"m"
                    }
                }
                "bitmap" -> {
//                    Log.d("bitmapsnippet", "bitmap: ${values[1]}")
                    if(!values[1].isNullOrBlank()) {
                        val imageBytes = Base64.decode(values[1], 0)
                        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        binding.placeImg.setImageBitmap(image)
                    } else {
                        binding.placeImg.setImageDrawable(null)
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
        binding.placeInfoFrame.visibility = View.VISIBLE
        Log.d("lat,lng", "latitude: $lat, longitude: $lng")
        if(lng.isNotBlank() && lat.isNotBlank()) {
            if(binding.selectFrame.visibility == View.VISIBLE) {
                binding.placeInfoFrame.visibility = View.GONE

            } else {
                pMarker?.setIcon(getMarkerIconFromDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.parking_locations
                    )
                ))
                pMarker = p0
                p0.setIcon(getMarkerIconFromDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.parking_location
                    )
                ))
                pLocation = Location(p0.title)
                pLocation?.latitude = lat.toDouble()
                pLocation?.longitude = lng.toDouble()
                binding.placeInfoFrame.visibility = View.VISIBLE
                Log.d("marker", p0.title.toString())
                binding.navigatePlace.setOnClickListener {
                    Log.d("lat,lng","latitude: ${pLocation!!.latitude}, longitude: ${pLocation!!.longitude}")
                    routePlace(binding.placeName.text.toString(), pLocation!!.latitude, pLocation!!.longitude)
                }
                binding.navFrame.setOnClickListener {
                    Log.d("lat,lng","latitude: ${pLocation!!.latitude}, longitude: ${pLocation!!.longitude}")
                    routePlace(binding.placeName.text.toString(), pLocation!!.latitude, pLocation!!.longitude)
                }
                binding.placeLike.setOnClickListener {
                    if(binding.placeLike.tag == "empty") {
                        binding.placeLike.setImageResource(R.drawable.star_fill)
                        binding.placeLike.tag = "fill"
                    } else {
                        binding.placeLike.setImageResource(R.drawable.star_empty)
                        binding.placeLike.tag = "empty"
                    }
                }
            }
        } else {
            binding.navigatePlace.setOnClickListener {

            }
            binding.navFrame.setOnClickListener {

            }
            binding.placeLike.setOnClickListener {

            }
        }
        return true
    }

    private fun setVisibility(mode: Int) {
        when(mode) {
            0 -> { returnMainView() }
            1 -> {
                returnMainView()
                removePlaceMarker()
            }
            2 -> {
                returnMainView()
                setToolbarConstraint()
                binding.myCarFrame.visibility = View.GONE
                binding.choiceFrame.visibility = View.GONE
            }
            3 -> {
                setToolbarConstraint()
                returnMainView()
                binding.searchResultFrame.visibility = View.GONE
            }
        }
    }

    private fun returnMainView() {
        binding.menuBtn.visibility = View.GONE
        binding.selectFrame.visibility = View.VISIBLE
        binding.noticeFrame.visibility = View.VISIBLE
        binding.toolbar.visibility = View.VISIBLE
        binding.currentLocaiton.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun backPressed() {
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
            exitProcess(0)
        }
    }

    private fun setResultFrameSize(mode: Int) {
        when(mode) {
            0 -> {
                Log.d("resize", "search result big")
                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                params.bottomMargin = 30
                params.topMargin = 240
                params.marginEnd = 60
                params.marginStart = 60
                binding.searchResultFrame.layoutParams = params

                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.uiFrame)
                constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.applyTo(binding.uiFrame)
            }
            1 -> {
                Log.d("resize", "search result small")
                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                params.bottomMargin = 30
                params.marginEnd = 60
                params.marginStart = 60
                binding.searchResultFrame.layoutParams = params

                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.uiFrame)
                constraintSet.connect(binding.searchResultFrame.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.applyTo(binding.uiFrame)
            }
        }
    }

    private fun setToolbarConstraint() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.uiFrame)
        constraintSet.connect(binding.linearLayout.id, ConstraintSet.TOP, binding.noticeFrame.id, ConstraintSet.BOTTOM)
        constraintSet.applyTo(binding.uiFrame)
    }

}