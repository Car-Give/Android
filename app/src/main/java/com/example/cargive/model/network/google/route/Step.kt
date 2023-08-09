package com.example.cargive.model.network.google.route

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: EndLocation,
    val html_instructions: String,
    val polyline: Polyline,
    val start_location: StartLocation,
    val steps: List<StepX>,
    val transit_details: TransitDetails,
    val travel_mode: String
)