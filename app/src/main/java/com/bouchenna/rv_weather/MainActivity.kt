package com.bouchenna.rv_weather


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
import com.sonney.valentin.LocalisationAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.service.FireBase_db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var menuBurger: ImageView
    private lateinit var deconnexion: Button
    private lateinit var firebaseDb: FireBase_db

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalisationAdapter
    private var locs: ArrayList<Localisation> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuBurger = binding.menuBurgerImageView
        deconnexion = binding.menuCustomInclude.header.buttonDeconnexion
        firebaseDb = FireBase_db()
        user  = FirebaseAuth.getInstance()
        recyclerView = binding.menuCustomInclude.recyclerViewMenu
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocalisationAdapter(this)
        recyclerView.adapter = adapter

        getData()

        deconnexion.setOnClickListener{

            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        menuBurger.setOnClickListener{
            val drawerLayout: DrawerLayout = binding.drawerLayout
            drawerLayout.openDrawer(GravityCompat.START)
        }


        // addLocalisation exemple

//        val paris = Localisation(
//            nom = "Paris",
//            coord = GeoPoint(48.8566, 2.3522),
//            country = "France",
//            state = "Île-de-France",
//            userId = user.uid.toString()
//        )
//        lifecycleScope.launch{
//            firebaseDb.addLocalisation(paris)
//            getData()
//        }




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

    fun suppression(loc: Localisation): Boolean{

        lifecycleScope.launch{
            firebaseDb.deleteLocalisation(loc)
            getData()
        }
        return  true
    }

    fun getData (){
        lifecycleScope.launch {
            locs = firebaseDb.getLocalisations(user.uid.toString())
            adapter.submitList(locs)
            adapter.notifyDataSetChanged()
        }
    }

}