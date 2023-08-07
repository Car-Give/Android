package com.example.cargive.feat.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.cargive.BuildConfig
import com.example.cargive.R
import com.example.cargive.databinding.ActivityMainBinding
import com.example.cargive.databinding.MainNavheaderBinding
import com.example.cargive.model.network.google.search.GoogleRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET
    )
    private val repository = GoogleRepository()
    private val REQUEST_PERMISSION_CODE = 1
    private var googleMap: GoogleMap? = null
    private var cMarker: Marker? = null

    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val defaultLocation = LatLng(37.3411, 126.7326)
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
    private var lastKnownLocation: Location? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: MainNavheaderBinding
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var locationManager: LocationManager
    private var backPressed: Long = 0
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            } else {
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
                    moveTaskToBack(true)
                    finishAndRemoveTask()
                    android.os.Process.killProcess(android.os.Process.myPid())
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
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.menuBtn.setOnClickListener {
            binding.navigationView.bringToFront()
            binding.drawerLayout.invalidate()
            binding.drawerLayout.openDrawer(GravityCompat.START)
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

        binding.searchName.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.searchName.text.toString().isNotBlank()) {
                    searchPlaces(binding.searchName.text.toString())
                }
            }
            true
        }
        binding.searchIcon.setOnClickListener {
            if (binding.searchName.text.toString().isNotBlank()) {
                searchPlaces(binding.searchName.text.toString())
            }
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

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val placeResult = placesClient.findCurrentPlace(request)
        placeResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val likelyPlaces = task.result

                // Set the count, handling cases where less than 5 entries are returned.
                val count = if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                    likelyPlaces.placeLikelihoods.size
                } else {
                    M_MAX_ENTRIES
                }
                var i = 0
                likelyPlaceNames = arrayOfNulls(count)
                likelyPlaceAddresses = arrayOfNulls(count)
                likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                likelyPlaceLatLngs = arrayOfNulls(count)
                for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                    // Build a list of likely places to show the user.
                    likelyPlaceNames[i] = placeLikelihood.place.name
                    likelyPlaceAddresses[i] = placeLikelihood.place.address
                    likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                    likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                    i++
                    if (i > count - 1) {
                        break
                    }
                }

                // Show a dialog offering the user the list of likely places, and add a
                // marker at the selected place.
                openPlacesDialog()
            } else {
                Log.e(TAG, "Exception: %s", task.exception)
            }
        }
    }

    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { dialog, which -> // The "which" argument contains the position of the selected item.
            val markerLatLng = likelyPlaceLatLngs[which]
            var markerSnippet = likelyPlaceAddresses[which]
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
            }

            if (markerLatLng == null) {
                return@OnClickListener
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            googleMap?.addMarker(MarkerOptions()
                .title(likelyPlaceNames[which])
                .position(markerLatLng)
                .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle("choose a place")
            .setItems(likelyPlaceNames, listener)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                }
            } else {
                Log.d(TAG, "Current location is null. Using defaults.")
                Log.e(TAG, "Exception: %s", task.exception)
                googleMap?.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        try {
            googleMap?.isMyLocationEnabled = true
//            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    private fun searchPlaces(keyword: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (!this@MainActivity.isFinishing && longitude != 0.0 && latitude != 0.0) {
                val resultDeferred = coroutine.async {
                    repository.getPlaceInfoByQuery(keyword = keyword, latitude = latitude, longitude = longitude)
                }
                val result = resultDeferred.await()
                Log.d("search result", result.toString())
                result?.let {
                    withContext(Dispatchers.Main) {
                        val bottomSheetFragment =
                            SearchParkingLotFragment(keyword, result.results, placesClient, latitude, longitude)
                        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                    }
                }
            }
        }
    }

    private fun setNavListener() {
        nav.profileLink.setOnClickListener {
            Toast.makeText(this@MainActivity.applicationContext, "프로필 눌림!", Toast.LENGTH_SHORT)
                .show()
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
                            setLocationUpdates()
//                            Handler(Looper.getMainLooper()).postDelayed({added?.remove()}, 3000)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 위치 정보 가져오기 실패 처리
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

    @SuppressLint("MissingPermission")
    fun setLocationUpdates() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            10.0f,
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
                Log.d("location listener", "latitude: $latitude  and longitude: $longitude")
                if (longitude != 0.0 && latitude != 0.0) {
                    if (cMarker != null) {
                        cMarker?.remove()
                    } else {
                        it.clear()
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        closeKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    //    edittext의 키보드 제거
    fun closeKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchName.windowToken, 0)
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

}