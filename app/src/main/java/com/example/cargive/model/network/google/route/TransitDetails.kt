package com.example.cargive.model.network.google.route

data class TransitDetails(
    val arrival_stop: ArrivalStop,
    val arrival_time: ArrivalTime,
    val departure_stop: DepartureStop,
    val departure_time: DepartureTime,
    val headsign: String,
    val headway: Int,
    val line: Line,
    val num_stops: Int
)