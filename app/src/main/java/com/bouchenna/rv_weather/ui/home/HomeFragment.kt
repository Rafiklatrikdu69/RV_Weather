package com.bouchenna.rv_weather.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bouchenna.rv_weather.R
import com.bouchenna.rv_weather.WeatherResponse
import com.bouchenna.rv_weather.databinding.FragmentHomeBinding
import com.bouchenna.rv_weather.service.Api_Weather
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class HomeFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMapClickListener{
    interface WeatherApiService {
        @GET("weather")
        fun getWeather(@Query("q") city: String, @Query("appid") apiKey: String): Call<WeatherResponse>
    }
    val CHANNEL_ID = "pickerChannel"
    private var googleMap: GoogleMap? = null
    private val URL ="https://api.openweathermap.org/data/2.5/weather?q=lyon&appid=e3b787e83e7b983adeca31847414a20e"
    private  val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private var notificationChannelCreated = false
    private val TAG : String ="CHECK_RESPONSE"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            googleMap?.uiSettings?.isZoomControlsEnabled = true

            // Activer la boussole
            googleMap?.uiSettings?.isCompassEnabled = true

            // Activer la barre d'outils de carte
            googleMap?.uiSettings?.isMapToolbarEnabled = true
            googleMap?.setOnMapClickListener(this)
        }

        if (!Places.isInitialized()) {
            Places.initialize(
                requireContext(),
                "AIzaSyAUhesh_MfnKVmEET8G6IKmDVaYUocE_yI"
            )
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as? AutocompleteSupportFragment

        autocompleteFragment?.setPlaceFields(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener{
    override fun onError(p0: Status) {
        TODO("Not yet implemented")
    }

    override fun onPlaceSelected(p0: Place) {
     if(p0.latLng!=null){
         val lating = p0.latLng
         val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lating, 15f)
         googleMap?.moveCamera(cameraUpdate)
         binding.textHome.text = lating!!.toString()
         get()

     }
    }

})

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun get() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApiService::class.java)

        api.getWeather("lyon", "e3b787e83e7b983adeca31847414a20e")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        weatherResponse?.let { response ->
                            Log.d(TAG, "ville: ${response.name}")
                            Log.d(TAG, "coord: ${response.coord?.lon} et ${response.coord?.lat}")
                            showWeatherPopup(weatherResponse);
                        }
                    } else {
                        Log.e(TAG, "Failed to get weather data: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e(TAG, "Error fetching weather data", t)
                }
            })
    }
    private fun showWeatherPopup(weatherResponse: WeatherResponse) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val message = "Country: ${weatherResponse.sys?.country}\n" +
                "Temperature: ${weatherResponse.main?.temp?.minus(273.15)} °C\n" +
                "Description: ${weatherResponse.weather?.firstOrNull()?.description}"

        dialogBuilder.setTitle("Weather Information")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton(null, null)

        val dialog = dialogBuilder.create()
        dialog.show()
        if (!notificationChannelCreated) {
            context?.let { createNotificationChannel(it)

            Log.d("notif","pas null !")}
            notificationChannelCreated = true
        }

        // Afficher la notification
        runNotify()


    }


    fun createNotificationChannel(context: Context) {
        // Create the notification channel if needed for O OS and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Check if the channel already exists
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                Log.d("channel","pas null !")
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system.
                notificationManager.createNotificationChannel(channel)
            }
        }
    }


    private fun runNotify() {
        // Construire la notification
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("This is title")
            .setContentText("This is content")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorité élevée
            .setAutoCancel(true) // Fermer automatiquement la notification lorsque l'utilisateur clique dessus
            .setTicker("Notification text scrolling...") // Texte de défilement

        // Créer une intention pour ouvrir l'activité lorsque l'utilisateur clique sur la notification
        val intent = Intent(requireContext(), HomeFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)

        // Construire la notification
        val notification = builder.build()

        // Afficher la notification
        val notificationManager = NotificationManagerCompat.from(requireContext())

        notificationManager.notify(0, notification)
    }

    override fun onMapReady(p0: GoogleMap?) {

    }
    override fun onMapClick(point: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 10f)
        googleMap?.moveCamera(cameraUpdate)
    }

}