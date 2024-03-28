    package com.bouchenna.rv_weather.ui.home


    import android.Manifest
    import android.annotation.SuppressLint
    import android.provider.Settings
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.net.ConnectivityManager
    import android.net.NetworkCapabilities
    import android.net.Uri
    import android.net.wifi.WifiManager
    import android.os.Build
    import android.os.Bundle

    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.core.app.ActivityCompat
    import androidx.core.app.NotificationCompat
    import androidx.core.app.NotificationManagerCompat
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.bouchenna.rv_weather.models.Localisation
    import com.bouchenna.rv_weather.MainActivity
    import com.bouchenna.rv_weather.R
    import com.bouchenna.rv_weather.models.WeatherResponse
    import com.bouchenna.rv_weather.databinding.FragmentHomeBinding
    import com.bouchenna.rv_weather.service.WeatherApiService
    import com.bumptech.glide.Glide
    import com.google.android.gms.common.api.Status
    import com.google.android.gms.location.LocationServices
    import com.google.android.gms.maps.CameraUpdateFactory
    import com.google.android.gms.maps.GoogleMap
    import com.google.android.gms.maps.OnMapReadyCallback
    import com.google.android.gms.maps.SupportMapFragment
    import com.google.android.gms.maps.model.LatLng
    import com.google.android.gms.maps.model.MarkerOptions
    import com.google.android.libraries.places.api.Places
    import com.google.android.libraries.places.api.model.Place
    import com.google.android.libraries.places.widget.AutocompleteSupportFragment
    import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.firestore.GeoPoint
    import com.google.firebase.ktx.Firebase
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import kotlin.math.roundToInt


    class HomeFragment (): Fragment() , OnMapReadyCallback, GoogleMap.OnMapClickListener {
        private val PERMISSION_REQUEST_CODE = 1001
        lateinit var wifiManager: WifiManager
        private lateinit var mainActivity: MainActivity
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
        private var marker: Boolean = false
        private var weatherResponse: WeatherResponse? = null

        private val binding get() = _binding!!
        override fun onAttach(context: Context) {
            super.onAttach(context)
            if (context is MainActivity) {
                mainActivity = context
            } else {
                throw IllegalStateException("Parent activity must be MainActivity")
            }
        }

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
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            Log.d("latTest", latitude.toString())
                            val lating = LatLng(latitude,longitude);
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lating, 10f)
                            googleMap?.moveCamera(cameraUpdate)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Location", "Impossible d'obtenir la localisation : ${exception.message}")
                    }
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }



            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync { map ->
                googleMap = map

                googleMap?.uiSettings?.isCompassEnabled = true

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

                    Toast.makeText(requireContext(), "Error occurred: $status", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPlaceSelected(p0: Place) {
                    if (p0.latLng != null) {

                        val lating = p0.latLng
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lating, 15f)
                        googleMap?.moveCamera(cameraUpdate)

                        get(lating)


                    }


                }
                // }

            })

            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


            checkNetworkConnectivity()

        }


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
                                Thread.sleep(1000)
                                val uri = Uri.parse(
                                    "https://openweathermap.org/img/w/" + weatherResponse.weather?.get(
                                        0
                                    )?.icon + ".png"
                                )

                                // Log.d("img", uri.toString())
                                context?.let {

                                    val imageView = binding.meteo
                                    if (imageView != null) {
                                        //Log.d("images", "pas null"+uri.toString())
                                        Glide.with(it)
                                            .load(uri)
                                            .into(imageView)
                                    }


                                }

                                binding.meteo.setImageURI(uri)
                                Log.d(TAG, "ville: ${response.name}")
                                Log.d(
                                    TAG,
                                    "coord: ${response.coord?.lon} et ${response.coord?.lat}"
                                )
                                binding.textView.visibility = View.VISIBLE
                                binding.btnAdd.setOnClickListener {

                                    val cityName = weatherResponse.name

                                    val latitude = weatherResponse.coord?.lat
                                    val longitude = weatherResponse.coord?.lon

                                    val countryName = weatherResponse.sys?.country


                                    if (cityName != null && latitude != null && longitude != null && countryName != null) {

                                        val geoPoint = GeoPoint(latitude, longitude)

                                        val localisation =
                                            Localisation("", cityName, geoPoint, countryName)


                                        mainActivity.addData(localisation)
                                    } else {

                                        Log.e(
                                            TAG,
                                            "Certaines données sont manquantes dans weatherResponse."
                                        )

                                        Toast.makeText(
                                            requireContext(),
                                            "Erreur: Certaines données sont manquantes.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
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




        fun createNotificationChannel(context: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "channel_name"
                val descriptionText = "description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    Log.d("channel", "pas null !")
                    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                    }

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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setTicker("Notification text scrolling...")


            val intent = Intent(requireContext(), HomeFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(pendingIntent)


            val notification = builder.build()

            val notificationManager = NotificationManagerCompat.from(requireContext())

            notificationManager.notify(0, notification)
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
            val alertDialogBuilder = AlertDialog.Builder(mainActivity!!)
            alertDialogBuilder.apply {
                setMessage("Choisissez une option:")
                setPositiveButton("Données mobiles") { dialog, which ->
                    val intent = Intent().apply {
                        action = Settings.ACTION_DATA_ROAMING_SETTINGS
                    }
                    startActivity(intent)
                    dialog.dismiss()
                }
                setNeutralButton("Wi-Fi") { dialog, which ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Pour Android 10 (Q) et versions ultérieures
                        val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                        startActivityForResult(panelIntent, PERMISSION_REQUEST_CODE)
                    } else {

                        val wifiManager = mainActivity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.CHANGE_WIFI_STATE), PERMISSION_REQUEST_CODE)
                        } else {

                            wifiManager.isWifiEnabled = true
                            Toast.makeText(mainActivity, "Wifi activé", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                setNegativeButton("Annuler") { dialog, which ->
                    dialog.dismiss()
                }
                setCancelable(false)
                create().show()
            }

            // Toast.makeText(requireContext(), "Pas de connexion Internet", Toast.LENGTH_LONG).show()
        }

        override fun onMapReady(googleMap: GoogleMap?) {

            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )



            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    println("passe par la !")
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("latTest", latitude.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Location", "Impossible d'obtenir la localisation : ${exception.message}")
                }
        }


    }


