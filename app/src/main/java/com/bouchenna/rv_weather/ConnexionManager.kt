package com.bouchenna.rv_weather
import com.firebase.ui.auth.AuthUI

class ConnexionManager {
    companion object {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
    }
}