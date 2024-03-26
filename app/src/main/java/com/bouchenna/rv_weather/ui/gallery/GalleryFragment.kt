    package com.bouchenna.rv_weather.ui.gallery

import YourXAxisValueFormatter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bouchenna.rv_weather.AirPollutionResponse
import com.bouchenna.rv_weather.WeatherForecastResponse
import com.bouchenna.rv_weather.WeatherResponse
import com.bouchenna.rv_weather.databinding.FragmentGalleryBinding
import com.bouchenna.rv_weather.service.WeatherApiService

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.LineGraphSeries
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    lateinit var lineGraphView: GraphView
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private var humidity:Int?=null
    private var lineChart: LineChart? = null
    private var temp_max = 0.0
    private var temp_min= 0.0
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nom = arguments?.getString("nom")
        val longitude = arguments?.getDouble("long")
        val latitude = arguments?.getDouble("lat")


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(WeatherApiService::class.java)




        galleryViewModel.text.observe(viewLifecycleOwner) {

        }
        lineGraphView = binding.idGraphView




        val pieChart = binding.pieChart
        api.getWeather(latitude!!, longitude!!, "e3b787e83e7b983adeca31847414a20e")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        val test = 12;
                        humidity = weatherResponse?.main?.humidity
                        binding.idHumidity.text ="Humidity: " +  humidity.toString() + " %"
                        binding.TempMin.text ="Temp Min : \n"+ weatherResponse?.main?.temp_min?.minus(273.15)?.roundToInt().toString() +" °C"
                        binding.TempMax.text = "Temp Max : \n"+weatherResponse?.main?.temp_max?.minus(273.15)?.roundToInt().toString() +" °C"
                    }

                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {

                }
            })
        api.getWeatherForecast(latitude!!, longitude!!, "e3b787e83e7b983adeca31847414a20e")
            .enqueue(object : Callback<WeatherForecastResponse> {
                override fun onResponse(
                    call: Call<WeatherForecastResponse>,
                    response: Response<WeatherForecastResponse>
                ) {
                    if (response.isSuccessful) {
                        val WeatherForecastResponse = response.body()
                        val groupedWeatherForecasts = WeatherForecastResponse?.list?.groupBy { it.dt_txt.split(" ")[0] }


                        var count = 0
                        val dataPoints = mutableListOf<DataPoint>()

                        var str =""
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val dayFormat = SimpleDateFormat("EEEE")
                        binding.idTVHead.text = nom +" "+WeatherForecastResponse?.list?.get(0)?.main?.temp?.minus(273.15)?.roundToInt() +" °C"
                        val headerRow = TableRow(context)


                        val dayHeader = TextView(context).apply {
                            text = "Day"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        headerRow.addView(dayHeader)

                        val humidityHeader = TextView(context).apply {
                            text = "H"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        headerRow.addView(humidityHeader)

                        val tempHeader = TextView(context).apply {
                            text = "Temp "+"\n Moyenne (°C)"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        val tempMin = TextView(context).apply {
                            text = "Temp"+"\n  Min (°C)"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        val tempMax = TextView(context).apply {
                            text = "Temp"+"\n  Max (°C)"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        headerRow.addView(tempHeader)
                        headerRow.addView(tempMin)
                        headerRow.addView(tempMax)

                        binding.tableLayout.addView(headerRow)
                        groupedWeatherForecasts?.forEach { (date, forecasts) ->
                            var tempSum = 0.0
                            var tempMin = Double.MAX_VALUE // Initialisation de la température minimale à une valeur maximale
                            var tempMax = Double.MIN_VALUE // Initialisation de la température maximale à une valeur minimale

                            forecasts.forEach { forecast ->
                                tempSum += forecast.main.temp
                                val temp = forecast.main.temp
                                if (temp < tempMin) {
                                    tempMin = temp // Met à jour la température minimale si une valeur inférieure est trouvée
                                }
                                if (temp > tempMax) {
                                    tempMax = temp // Met à jour la température maximale si une valeur supérieure est trouvée
                                }
                            }

                            val formattedDate = dateFormat.parse(date)
                            val dayOfWeek = dayFormat.format(formattedDate)
                            val averageTemp = tempSum / forecasts.size
                            val humidity = forecasts.map { it.main.humidity }.average().toInt()

                            val tableRow = TableRow(context)

                            val dayTextView = TextView(context)
                            dayTextView.text = dayOfWeek
                            dayTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(dayTextView)

                            val humidityTextView = TextView(context)
                            humidityTextView.text = "$humidity %"
                            humidityTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(humidityTextView)

                            val tempTextView = TextView(context)
                            tempTextView.text = "${averageTemp.minus(273.15).roundToInt()}"
                            tempTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(tempTextView)

                            val tempMinTextView = TextView(context)
                            tempMinTextView.text = "${tempMin.minus(273.15).roundToInt()}" // Affiche la température minimale
                            tempMinTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(tempMinTextView)
                            dataPoints.add(DataPoint(count.toDouble(), averageTemp.minus(273.15)))
                            val tempMaxTextView = TextView(context)
                            tempMaxTextView.text = "${tempMax.minus(273.15).roundToInt()}" // Affiche la température maximale
                            tempMaxTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(tempMaxTextView)
                            count++;
                            binding.tableLayout.addView(tableRow)
                        }


                        binding.forecastTextView.text = str
                        val dataPointsArray = dataPoints.toTypedArray()


                        val series = LineGraphSeries(dataPointsArray)
                        lineGraphView.addSeries(series)
                        lineGraphView.animate()
                        lineGraphView.gridLabelRenderer.horizontalAxisTitle = "Date"
                        lineGraphView.gridLabelRenderer.verticalAxisTitle = "Température (°C)"
                        lineGraphView.viewport.isScrollable = true
                        lineGraphView.viewport.isScrollable = true


                        lineGraphView.viewport.isScalable = true

                        lineGraphView.viewport.setScalableY(true)
                        lineGraphView.viewport.setScrollableY(true)





                    }

                }

                override fun onFailure(call: Call<WeatherForecastResponse>, t: Throwable) {

                }
            })

        api.getAirPollution(latitude!!, longitude!!, "metric","fr","e3b787e83e7b983adeca31847414a20e")
            .enqueue(object : Callback<AirPollutionResponse> {
                override fun onResponse(
                    call: Call<AirPollutionResponse>,
                    response: Response<AirPollutionResponse>
                ) {
                    if (response.isSuccessful) {
                        val airPollutionResponse = response.body()
                        val pollutionDataList = airPollutionResponse?.list
                        if (!pollutionDataList.isNullOrEmpty()) {
                            binding.aqiProgressBar.progress =  airPollutionResponse.list.get(0).main?.aqi!!
                            binding.aqiValueTextView.text = airPollutionResponse.list.get(0).main?.aqi.toString()
                            val pollutionData = pollutionDataList[0] // On prend la première donnée de pollution de la liste
                            val aqi = pollutionData.main?.aqi
                            val co = pollutionData.components?.co
                            val no = pollutionData.components?.no
                            val no2 = pollutionData.components?.no2
                            val o3 = pollutionData.components?.o3
                            val so2 = pollutionData.components?.so2
                            val pm25 = pollutionData.components?.pm2_5
                            val pm10 = pollutionData.components?.pm10
                            val nh3 = pollutionData.components?.nh3
                            val nh3Values = mutableListOf<Float>()
                            val so2Values = mutableListOf<Float>()


                            for (pollutionData in pollutionDataList) {
                                val nh3 = pollutionData.components?.nh3?.toFloat() ?: 0f // Conversion de Double en Float
                                val so2 = pollutionData.components?.so2?.toFloat() ?: 0f // Conversion de Double en Float

                                nh3Values.add(nh3)
                                so2Values.add(so2)
                            }

                            val maxTempList = nh3Values + so2Values
                            val minTempList = nh3Values + so2Values

                            setTemperatureData(maxTempList, minTempList)


                            // Création des entrées pour le Pie Chart
                            val entries = mutableListOf<PieEntry>()
                            entries.add(PieEntry(co!!.toFloat(), "CO")) // Monoxyde de carbone
                            entries.add(PieEntry(no2!!.toFloat(), "NO2")) // Dioxyde d'azote
                            entries.add(PieEntry(o3!!.toFloat(), "O3")) // Ozone
                           /* entries.add(PieEntry(so2!!.toFloat(), "SO2")) // Dioxyde de soufre
                            entries.add(PieEntry(pm25!!.toFloat(), "PM2.5")) // Particules fines (PM2.5)
                            entries.add(PieEntry(pm10!!.toFloat(), "PM10")) // Particules en suspension (PM10)
                            entries.add(PieEntry(nh3!!.toFloat(), "NH3")) // Ammoniac
*/
                            // Création du dataset pour le Pie Chart
                            val dataSet = PieDataSet(entries, "Air Pollution Components")
                            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                            // Création des données à afficher dans le Pie Chart
                            val data = PieData(dataSet)

                            // Paramétrage du Pie Chart
                            pieChart.data = data
                            pieChart.invalidate()
                        }
                    }

                }


                override fun onFailure(call: Call<AirPollutionResponse>, t: Throwable) {
                    // Gérer les cas d'échec de la requête
                }
            })
        lineChart =binding.lineChart

        setupLineChart()

    return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupLineChart() {
        // Configuration du LineChart
        lineChart?.apply {
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            axisRight.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(false)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            val formatter = YourXAxisValueFormatter()
            xAxis.valueFormatter = formatter

            axisLeft.setDrawGridLines(true)


            legend.isEnabled = true
        }
    }

    private fun setData(maxTempList: List<Float>, minTempList: List<Float>) {
        val entriesMax = ArrayList<Entry>()
        val entriesMin = ArrayList<Entry>()

        // Remplissage des entrées pour les valeurs maximales
        for (i in maxTempList.indices) {
            entriesMax.add(Entry(i.toFloat(), maxTempList[i]))
        }



        for (i in minTempList.indices) {
            entriesMin.add(Entry(i.toFloat(), minTempList[i]))
        }

        // Création des DataSet pour les valeurs maximales et minimales
        val dataSetMax = LineDataSet(entriesMax, "Max Temp")
        val dataSetMin = LineDataSet(entriesMin, "Min Temp")

        // Configuration des couleurs et du style des DataSet
        dataSetMax.apply {
            color = ColorTemplate.COLORFUL_COLORS[0]
            setCircleColor(ColorTemplate.COLORFUL_COLORS[0])
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        dataSetMin.apply {
            color = ColorTemplate.COLORFUL_COLORS[1]
            setCircleColor(ColorTemplate.COLORFUL_COLORS[1])
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }


        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(dataSetMax)
        dataSets.add(dataSetMin)

        // Création de LineData à partir des DataSet
        val data = LineData(dataSets)

        // Configuration des données sur le LineChart
        lineChart?.data = data
        lineChart?.invalidate()
    }

    // Fonction pour définir les données de température maximale et minimale
    fun setTemperatureData(maxTempList: List<Float>, minTempList: List<Float>) {
        setData(maxTempList, minTempList)
    }
}