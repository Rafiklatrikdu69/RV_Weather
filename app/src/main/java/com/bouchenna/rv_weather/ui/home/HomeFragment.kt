    package com.bouchenna.rv_weather.ui.home


    import android.Manifest
    import android.annotation.SuppressLint
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.net.ConnectivityManager
    import android.net.NetworkCapabilities
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
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
    import com.bumptech.glide.Glide
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
    import com.google.android.material.snackbar.Snackbar
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import retrofit2.http.GET
    import retrofit2.http.Query
    import kotlin.math.roundToInt
    import android.widget.Button
    import com.google.android.gms.maps.model.MarkerOptions
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.ktx.Firebase

    class HomeFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMapClickListener {
        interface WeatherApiService {
            @GET("weather")
            fun getWeather(
                @Query("lat") lat: Double,
                @Query("lon") long: Double,
                @Query("appid") apiKey: String
            ): Call<WeatherResponse>
        }

        val CHANNEL_ID = "pickerChannel"
        private var googleMap: GoogleMap? = null
        private val URL =
            "https://api.openweathermap.org/data/2.5/weather?q=lyon&appid=e3b787e83e7b983adeca31847414a20e"
        private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private var notificationChannelCreated = false
        private val TAG: String = "CHECK_RESPONSE"
        private var _binding: FragmentHomeBinding? = null
        private lateinit var test: Button
        private lateinit var user: FirebaseAuth
        private var marker:Boolean= false
        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            user = Firebase.auth
            val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            val root: View = binding.root

            val textView: TextView = binding.textHome
            homeViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }


            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync { map ->
                googleMap = map
                //    googleMap?.uiSettings?.isZoomControlsEnabled = true

                // Activer la boussole
                googleMap?.uiSettings?.isCompassEnabled = true

                // Activer la barre d'outils de carte
                googleMap?.uiSettings?.isMapToolbarEnabled = true
                googleMap?.setOnMapClickListener(this)
            }
            binding.textView.visibility = View.GONE
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

            autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onError(status: Status) {
                    Log.e(TAG, "Error occurred: $status")
                    // Ajoutez ici le code pour gérer l'erreur, par exemple afficher un message d'erreur
                    Toast.makeText(requireContext(), "Error occurred: $status", Toast.LENGTH_SHORT).show()
                }

                override fun onPlaceSelected(p0: Place) {
                    if (p0.latLng != null) {
                        val lating = p0.latLng
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lating, 15f)
                        googleMap?.moveCamera(cameraUpdate)
                      //  binding.textHome.text = lating!!.toString()
                        get(lating)

                    }
                }

            })

            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Vérifier la connectivité réseau lorsque la vue est créée
            checkNetworkConnectivity()

        }

        //test.setOnClickListener { addBD() }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


        fun get(lating: LatLng) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(WeatherApiService::class.java)

            api.getWeather(lating.latitude, lating.longitude, "e3b787e83e7b983adeca31847414a20e")
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val weatherResponse = response.body()
                            weatherResponse?.let { response ->
                                binding.nomVille.text =
                                    weatherResponse.name + " : " + weatherResponse.main?.temp?.minus(
                                        273.15
                                    )?.roundToInt() + " °C"
                                val uri = Uri.parse(
                                    "http://openweathermap.org/img/w/" + weatherResponse.weather?.get(
                                        0
                                    )?.icon + ".png"
                                )
                                Log.d("img", uri.toString())
                                context?.let {
                                    val uri = Uri.parse(
                                        "https://openweathermap.org/img/w/" + weatherResponse.weather?.get(
                                            0
                                        )?.icon + ".png"
                                    )
                                    Log.d("img", uri.toString())
                                    val imageView = binding.meteo
                                    context?.let {
                                        if (imageView != null) {
                                            Log.d("images", "pas null")
                                            Glide.with(it)
                                                .load(uri)
                                                .into(imageView)
                                        }
                                    }

                                }

                                binding.meteo.setImageURI(uri)
                                Log.d(TAG, "ville: ${response.name}")
                                Log.d(
                                    TAG,
                                    "coord: ${response.coord?.lon} et ${response.coord?.lat}"
                                )
                                binding.textView.visibility = View.VISIBLE

                                //showWeatherPopup(weatherResponse);
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
                context?.let {
                    createNotificationChannel(it)

                    Log.d("notif", "pas null !")
                }
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
                    Log.d("channel", "pas null !")
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
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(pendingIntent)

            // Construire la notification
            val notification = builder.build()

            // Afficher la notification
            val notificationManager = NotificationManagerCompat.from(requireContext())

            notificationManager.notify(0, notification)
        }

        override fun onMapReady(googleMap: GoogleMap?) {


        }


        override fun onMapClick(point: LatLng) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 5f)
            googleMap?.moveCamera(cameraUpdate)
            val ville = LatLng(point.latitude, point.longitude)
            if (!marker) {
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(ville)
                        .title("Marker in Sydney")
                )
                this.marker = true
            } else {
                googleMap?.clear()
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(ville)
                        .title("Marker in Sydney")
                )
            }
            get(point)
        }



        @SuppressLint("MissingPermission")
        private fun checkNetworkConnectivity() {
            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities == null ||
                    !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ) {
                    showNoInternetMessage()
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                if (networkInfo == null || !networkInfo.isConnected) {
                    showNoInternetMessage()

                }
            }
        }

        private fun showNoInternetMessage() {
            Toast.makeText(requireContext(), "Pas de connexion Internet", Toast.LENGTH_LONG).show()
        }
    }



