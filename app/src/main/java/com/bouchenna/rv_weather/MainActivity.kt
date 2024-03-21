package com.bouchenna.rv_weather

<<<<<<< Updated upstream
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
=======
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
>>>>>>> Stashed changes
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bouchenna.rv_weather.databinding.ActivityMainBinding
<<<<<<< Updated upstream
=======
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
>>>>>>> Stashed changes

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
<<<<<<< Updated upstream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

=======
    private lateinit var user: FirebaseAuth
    private lateinit var deconnexion: Button





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
>>>>>>> Stashed changes
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val headerView = binding.navView.getHeaderView(0)

        // Rechercher le bouton de déconnexion dans la vue de l'en-tête de navigation
        deconnexion = headerView.findViewById(R.id.buttonDeconnexion)
        user = Firebase.auth
        deconnexion.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
<<<<<<< Updated upstream
=======




>>>>>>> Stashed changes
}