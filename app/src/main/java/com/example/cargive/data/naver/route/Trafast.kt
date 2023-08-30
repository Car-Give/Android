package com.example.cargive.data.naver.route

data class Trafast(
    val guide: List<Guide>,
    val path: List<List<Double>>,
    val section: List<Section>,
    val summary: Summary
)