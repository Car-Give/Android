package com.example.cargive.model.network.google.route

data class PlaceRouteResponseModel(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)