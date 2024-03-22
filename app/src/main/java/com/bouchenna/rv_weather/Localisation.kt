package com.bouchenna.rv_weather

import com.google.firebase.firestore.GeoPoint

data class Localisation(

    var nom: String,
    var coord: GeoPoint,
    var contry: String,
    var state: String,
    var userId: String
)
