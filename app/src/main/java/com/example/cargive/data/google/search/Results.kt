package com.example.cargive.data.google.search

import android.graphics.Bitmap


data class Results(
    val business_status: String,
    val geometry: GeoMetry,
    val icon: String,
    val icon_background_color: String,
    val icon_mask_base_uri: String,
    val name: String,
    val opening_hours: Opened,
    val photos: List<PlacePhotos>,
    val place_id: String,
    val plus_code: PlacePlusCode,
    val price_level: Int,
    val rating: Double,
    val types: List<String>,
    val user_ratings_total: Double,
    val vicinity: String,
    val website: String?,
    val url: String?,
    var call: String? = null,
    var address: String? = null,
    var distance : Int? = null,
    var bitmaps: Bitmap? = null
)
