package com.example.cargive.model.network.google.route

data class StepX(
    val distance: Distance,
    val duration: Duration,
    val end_location: EndLocation,
    val polyline: Polyline,
    val start_location: StartLocation,
    val travel_mode: String
)