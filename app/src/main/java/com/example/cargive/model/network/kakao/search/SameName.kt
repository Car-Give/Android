package com.example.cargive.model.network.kakao.search

data class SameName(
    val keyword: String,
    val region: List<Any>,
    val selected_region: String
)