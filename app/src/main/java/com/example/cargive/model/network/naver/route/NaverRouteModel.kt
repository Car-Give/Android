package com.example.cargive.model.network.naver.route

data class NaverRouteModel(
    val code: Int,
    val currentDateTime: String,
    val message: String,
    val route: Route
)