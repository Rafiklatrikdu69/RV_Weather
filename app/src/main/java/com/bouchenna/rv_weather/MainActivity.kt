package com.bouchenna.rv_weather

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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
        setSupportActionBar(binding.appBarMain.toolbar)

        deconnexion = findViewById(R.id.buttonDeconnexion)
        user = Firebase.auth
        val drawerLayout: DrawerLayout = binding.drawerLayout
        menuBurger = binding.appBarMain.toolbar.findViewById(R.id.menuBurgerImageView)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        recyclerView = findViewById(R.id.recyclerViewMenu)

        adapter = LocalisationAdapter(this)

        //test
        val coordLyon = GeoPoint(45.75 , 4.5833)
        var test: Localisation = Localisation("lyon", coordLyon, "france", "france", "ZZhlxaf3YNObI4l7L8TtXAc7EV92" )
        locs.add(test)
        locs.add(test)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.submitList(locs)

        recyclerView.adapter = adapter




        deconnexion.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        menuBurger.setOnClickListener{
            val drawerLayout: DrawerLayout = binding.drawerLayout
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)


        // Recycler View lat 45.75 long 4.5833


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