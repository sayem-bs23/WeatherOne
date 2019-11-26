package com.bs.weatherone


data class PlacePrediction(
    var placeId: String = "",
    var address: String? = null,
    var primaryAddress: String = "",
    var secondaryAddress: String = "",
    var lat: Double? = null,
    var lon: Double? = null
)