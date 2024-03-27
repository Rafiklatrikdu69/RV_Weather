package com.bouchenna.rv_weather


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
import com.sonney.valentin.LocalisationAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.service.FireBase_db
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.launch
import android.Manifest
import androidx.activity.result.ActivityResultLauncher


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var menuBurger: ImageView
    private lateinit var deconnexion: Button
    private lateinit var firebaseDb: FireBase_db
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalisationAdapter
    private lateinit var addLocalisation: Button
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private var locs: ArrayList<Localisation> = ArrayList()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        menuBurger = binding.menuBurgerImageView
        drawerLayout = binding.drawerLayout
        deconnexion = binding.menuCustomInclude.header.buttonDeconnexion
        firebaseDb = FireBase_db()
        user = FirebaseAuth.getInstance()
        recyclerView = binding.menuCustomInclude.recyclerViewMenu
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocalisationAdapter(this)
        recyclerView.adapter = adapter
        addLocalisation = binding.menuCustomInclude.addLocButton

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                Log.d("test perm", "perm")
                if (isGranted) {
                    Log.d("Permission: ", "Granted")
                } else {
                    Log.d("Permission: ", "Denied")
                }
            }

        onClickRequestPermission(binding.root)


        initialisation()

        user.currentUser?.email?.let {
            binding.menuCustomInclude.header.textViewUserName.text = it
        }

        deconnexion.setOnClickListener {

            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        menuBurger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        addLocalisation.setOnClickListener {
            navController.navigate(R.id.nav_home)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast

            Log.d("token", " token : $token" )

        })

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
            putDouble("long", loc.coord.longitude)
            putDouble("lat", loc.coord.latitude)
        }
        navController.navigate(R.id.action_nav_home_to_nav_gallery, bundle)

        return true
    }

    fun getData() {
        lifecycleScope.launch {
            Log.d("test", "data1")
            locs = firebaseDb.getLocalisations(user.uid.toString())
            Log.d("test", "data2")
            adapter.submitList(locs)
            adapter.notifyDataSetChanged()
        }
    }

    fun addData(loc: Localisation): Boolean {
        lifecycleScope.launch {
            loc.userId = user.uid.toString()
            firebaseDb.addLocalisation(loc)
            getData()
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    fun initialisation() {

        lifecycleScope.launch {

            locs = firebaseDb.getLocalisations(user.uid.toString())

            adapter.submitList(locs)
            adapter.notifyDataSetChanged()

            if (!locs.isEmpty()) {
                var loc = locs.get(0)
                showMmeteo(loc)
            } else {

            }
        }
    }

    fun onClickRequestPermission(view: View) {
        //notification
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                showSnackbar(
                    view,
                    getString(R.string.permission_notification_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            else -> {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        //internet
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.INTERNET
            ) -> {
                showSnackbar(
                    view,
                    getString(R.string.permission_internet_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {

                    requestPermissionLauncher.launch(Manifest.permission.INTERNET)
                }
            }

            else -> {

                requestPermissionLauncher.launch(Manifest.permission.INTERNET)
            }
        }
    }


    fun showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(view)
            }.show()
        } else {
            snackbar.show()
        }
    }
}


