package com.bouchenna.rv_weather

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
import com.bouchenna.rv_weather.service.FireBase_db
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sonney.valentin.LocalisationAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalisationAdapter
    private lateinit var addLocalisation: Button
    private lateinit var firebaseDb: FireBase_db
    private lateinit var deletedLocalisation: Localisation
    private var deletedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance()
        firebaseDb = FireBase_db()
        recyclerView = binding.menuCustomInclude.recyclerViewMenu
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocalisationAdapter(this)
        recyclerView.adapter = adapter
        addLocalisation = binding.menuCustomInclude.addLocButton
        getData()

        addLocalisation.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_home)
        }
    }



    fun suppression(loc: Localisation): Boolean {
        lifecycleScope.launch {
            firebaseDb.deleteLocalisation(loc)
            getData()
        }
        return true
    }

    fun showMmeteo(loc: Localisation): Boolean {
        val bundle = Bundle().apply {
            putString("nom", loc.nom)
            loc.coord?.let { putDouble("long", it.longitude) }
            loc.coord?.let { putDouble("lat", it.latitude) }
        }
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_home_to_nav_gallery, bundle)
        return true
    }

    private fun getData() {
        lifecycleScope.launch {
            val locs = firebaseDb.getLocalisations(user.uid.toString())
            adapter.submitList(locs)
        }
    }

    fun addData(loc: Localisation): Boolean {
        lifecycleScope.launch {
            loc.userId = user.uid.toString()
            firebaseDb.addLocalisation(loc)
            getData()
        }
        return true
    }
}
