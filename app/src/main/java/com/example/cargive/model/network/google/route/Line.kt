package com.example.cargive.model.network.google.route

data class Line(
    val agencies: List<Agency>,
    val color: String,
    val name: String,
    val short_name: String,
    val text_color: String,
    val vehicle: Vehicle
)