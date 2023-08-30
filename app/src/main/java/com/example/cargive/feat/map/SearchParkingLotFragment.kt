package com.example.cargive.feat.map

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import android.R
import android.content.Context
import com.example.cargive.databinding.FragementChooseParkinglotBinding
import com.example.cargive.data.google.search.Results
import com.google.android.libraries.places.api.net.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchParkingLotFragment(
    private val name: String, private val arr: List<Results>, private val client: PlacesClient,
    private val latitude: Double, private val longitude: Double, private val context: Context
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragementChooseParkinglotBinding
    private lateinit var location: Location
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragementChooseParkinglotBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchPlaceName.text = name
        initSpinner()

    }

    private fun sortPlace() {
        location = Location("Current Location")
        location.longitude = longitude
        location.latitude = latitude
//        val placeFields =
//            listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.TYPES)
//        val request = FindCurrentPlaceRequest.builder(placeFields).build()
//
//        client.findCurrentPlace(request).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val response = task.result
//                response?.let { likelyPlaces ->
//                    sortedPlaces = likelyPlaces.placeLikelihoods
//                        .sortedBy {
//                            val placeLocation = Location("place")
//                            placeLocation.latitude = it.place.latLng.latitude
//                            placeLocation.longitude = it.place.latLng.longitude
//                            Log.d("type", "type: ${it.place.types}")
//                            location.distanceTo(placeLocation)
//                        }
//                    Log.d("정렬 결과", "sorted: $sortedPlaces")
//                }
//            } else {
//                // Handle the error
//            }
//        }
        if(arr.isNotEmpty()) {
            val sorted = arr.sortedBy {
                val placeLocation = Location("place")
                placeLocation.latitude = it.geometry.location.lat
                placeLocation.longitude = it.geometry.location.lng
                location.distanceTo(placeLocation)
            }
            Log.d("정렬됨", "sorted: $sorted")
            initRecycler(sorted)
        } else {
            binding.searchResult.visibility = View.GONE
            binding.cautionFrame.visibility = View.VISIBLE
        }
    }
    private fun initSpinner() {
        val arr1: MutableList<String> = mutableListOf("거리 순", "추천 순", "인기 순")
        binding.sortSpinner
        val spinnerAdapter =
            ArrayAdapter<String>(requireContext(), R.layout.simple_list_item_1, arr1)
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
        sortPlace()
    }

    private fun initRecycler(sorted: List<Results>) {
        binding.searchResult.visibility = View.VISIBLE
        val adapter = ParkingLotListAdapter(context)
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(sorted)
    }
}