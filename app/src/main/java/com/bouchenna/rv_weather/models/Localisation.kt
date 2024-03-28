package com.bouchenna.rv_weather.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Localisation(

    var documentId: String? = "",
    var nom: String? = "",
    var coord: GeoPoint ? = GeoPoint(0.0, 0.0),
    var country: String? = "",
    var state: String? = "",
    var userId: String? = ""
)
