package com.example.cargive.feat.map

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cargive.databinding.ParkingLotListBinding
import com.example.cargive.data.google.search.Results
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class NavParkingLotFragment (private val result: Results, private val client: PlacesClient, private val cLocation: Location):
    BottomSheetDialogFragment() {
    private lateinit var binding: ParkingLotListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ParkingLotListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigatePlace.visibility = View.VISIBLE
        binding.navigatePlace.setOnClickListener {

        }
        binding.placeName.text = result.name
        val pLocation = Location("place")
        pLocation.latitude = result.geometry.location.lat
        pLocation.longitude = result.geometry.location.lng
        Log.d("place_id", result.place_id)

        val distance = cLocation.distanceTo(pLocation)
        binding.placeDistance.text = distance.roundToInt().toString()+"m"
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.PRICE_LEVEL,
            Place.Field.WEBSITE_URI
        )
        val request = FetchPlaceRequest.newInstance(result.place_id, placeFields)

        client.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
//                    Log.d("name", "장소? : ${place.name}")
//                    Log.d("name", "주소? : ${place.address}")
//                    Log.d("plus", "phone?: ${place.phoneNumber}")
//                    Log.d("price", "price?: ${place.priceLevel}")
//                    Log.d("price", "uri?: ${place.websiteUri}")
                binding.internalCall.text = place.phoneNumber
                binding.detailAddress.text = place.address
//                    Log.d("name", place.name)
                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {
                    Log.d("장소 결과", "No photo metadata.")
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
                client.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        binding.placeImg.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("place", "Place not found: " + exception.message)
                            val statusCode = exception.statusCode
                            TODO("Handle error with given status code.")
                        }
                    }
            }
            .addOnFailureListener {
                Log.e("place", "Place not found: " + it.message)
            }
    }



    private fun initRecycler(sorted: List<Results>) {

    }
}