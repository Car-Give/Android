package com.example.cargive.model.network.naver.route

data class Summary(
    val bbox: List<List<Double>>,
    val departureTime: String,
    val distance: Int,
    val duration: Int,
    val etaServiceType: Int,
    val fuelPrice: Int,
    val goal: Goal,
    val start: Start,
    val taxiFare: Int,
    val tollFare: Int
)