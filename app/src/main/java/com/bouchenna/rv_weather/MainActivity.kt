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
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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
//    private lateinit var mobile_navigation: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalisationAdapter
    private lateinit var addLocalisation: Button
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var locs: ArrayList<Localisation> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        menuBurger = binding.menuBurgerImageView
        deconnexion = binding.menuCustomInclude.header.buttonDeconnexion
        firebaseDb = FireBase_db()
        user  = FirebaseAuth.getInstance()
        recyclerView = binding.menuCustomInclude.recyclerViewMenu
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocalisationAdapter(this)
        recyclerView.adapter = adapter
        addLocalisation = binding.menuCustomInclude.addLocButton
        getData()

        user.currentUser?.email?.let {
            binding.menuCustomInclude.header.textViewUserName.text = it
        }

        deconnexion.setOnClickListener{

            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        menuBurger.setOnClickListener{
            val drawerLayout: DrawerLayout = binding.drawerLayout
            drawerLayout.openDrawer(GravityCompat.START)
        }

        addLocalisation.setOnClickListener{
            navController.navigate(R.id.nav_home)
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

    fun suppression(loc: Localisation): Boolean{

        lifecycleScope.launch{
            firebaseDb.deleteLocalisation(loc)
            getData()
        }
        return  true
    }

    fun showMmeteo(loc: Localisation): Boolean{

        val bundle = Bundle().apply {
            putString("nom", loc.nom)
            putDouble("long", loc.coord.longitude)
            putDouble("lat", loc.coord.latitude)}
            navController.navigate(R.id.action_nav_home_to_nav_gallery, bundle)

        return  true
    }

    fun getData (){
        lifecycleScope.launch {
            locs = firebaseDb.getLocalisations(user.uid.toString())
            adapter.submitList(locs)
            adapter.notifyDataSetChanged()
        }
    }

    fun addData (loc: Localisation): Boolean{
        lifecycleScope.launch{
            loc.userId = user.uid.toString()
            firebaseDb.addLocalisation(loc)
            getData()
        }
        return true
    }

}