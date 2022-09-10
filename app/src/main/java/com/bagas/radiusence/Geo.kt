package com.bagas.radiusence

class Geo(private val lat: Double, private val long: Double) {

    companion object {
        const val earthRadiusKm: Double = 6372.8
    }

    fun haversine(destination: Geo): Double {
        val dLat = Math.toRadians(destination.lat - this.lat)
        val dLon = Math.toRadians(destination.long - this.long)
        val originLat = Math.toRadians(this.lat)
        val destinationLat = Math.toRadians(destination.lat)

        val a = Math.pow(Math.sin(dLat / 2), 2.toDouble()) + Math.pow(Math.sin(dLon / 2), 2.toDouble()) * Math.cos(originLat) * Math.cos(destinationLat)
        val c = 2 * Math.asin(Math.sqrt(a))
        return earthRadiusKm * c * 1000
    }

}