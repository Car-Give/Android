package com.example.cargive.data.google.search

data class GooglePlaceSearchModel(
    val error_message: String,
    val html_attributions : List<String>,
    val results: List<Results>,
    val status: String
)
