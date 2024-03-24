package com.bouchenna.rv_weather


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.Menu
import android.widget.AutoCompleteTextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
import com.google.android.gms.fitness.data.Field
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

import android.content.Intent
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.ImageView

import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.sonney.valentin.LocalisationAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var menuBurger: ImageView
    private lateinit var deconnexion: Button


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalisationAdapter
    private var locs: ArrayList<Localisation> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView
        recyclerView = binding.menuCustomInclude.recyclerViewMenu
        adapter = LocalisationAdapter(this)
        recyclerView.adapter = adapter
        adapter.submitList(locs)

        menuBurger = binding.menuBurgerImageView
        deconnexion = binding.menuCustomInclude.header.buttonDeconnexion

        deconnexion.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        menuBurger.setOnClickListener{
            val drawerLayout: DrawerLayout = binding.drawerLayout
            drawerLayout.openDrawer(GravityCompat.START)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}