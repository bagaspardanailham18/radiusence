package com.bagas.radiusence.data.model

data class PresenceUsers(
    val nim: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val distance: Double,
    val presenceStatus: String
)
