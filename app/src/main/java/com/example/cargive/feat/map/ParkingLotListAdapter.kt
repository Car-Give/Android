package com.example.cargive.feat.map

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.databinding.ParkingLotListBinding
import com.example.cargive.model.network.google.search.Results
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import kotlin.math.roundToInt

class ParkingLotListAdapter(private val cLocation: Location, private val client: PlacesClient, private val activity: Context):
    ListAdapter<Results, ParkingLotListAdapter.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Results>() {
            override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {

                return oldItem.place_id == newItem.place_id
            }

            override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotListAdapter.ViewHolder {
        val binding = ParkingLotListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ParkingLotListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(datas: Results){
            binding.placeName.text = datas.name
            var bitmap: Bitmap? = null
            var phoneNumber: String = ""
            var address: String = ""
            val pLocation = Location("place")
            pLocation.latitude = datas.geometry.location.lat
            pLocation.longitude = datas.geometry.location.lng
            Log.d("place_id", datas.place_id)
            val distance = cLocation.distanceTo(pLocation).roundToInt()
            binding.placeDistance.text = distance.toString()+"m"
            binding.placeInfoFrame.setOnClickListener {
                val main = activity as MainActivity
                main.showPlaceNav(datas, distance, bitmap, phoneNumber, address, datas.place_id)
            }


//            val placeFields = mutableListOf(Place.Field.ID, Place.Field.NAME)
//            val request = FetchPlaceRequest.newInstance(datas.place_id, placeFields)
//
//            val fields = listOf(Place.Field.PHOTO_METADATAS)
//            val placeRequest = FetchPlaceRequest.newInstance(datas.place_id, fields)
            val placeFields = listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.PHOTO_METADATAS,
                Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI
            )
            val request = FetchPlaceRequest.newInstance(datas.place_id, placeFields)

            client.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
//                    Log.d("name", "장소? : ${place.name}")
//                    Log.d("name", "주소? : ${place.address}")
//                    Log.d("plus", "phone?: ${place.phoneNumber}")
//                    Log.d("price", "price?: ${place.priceLevel}")
//                    Log.d("price", "uri?: ${place.websiteUri}")
                    if(place.phoneNumber.isNullOrBlank()) {
                        binding.callFrame.visibility = View.GONE
                    } else {
                        binding.internalCall.text = place.phoneNumber
                        phoneNumber = place.phoneNumber as String
                    }
                    binding.detailAddress.text = place.address
                    address = place.address as String
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
                            bitmap = fetchPhotoResponse.bitmap
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



    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ParkingLotListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}