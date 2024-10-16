    package com.bouchenna.rv_weather.ui.gallery

import YourXAxisValueFormatter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bouchenna.rv_weather.models.AirPollutionResponse
import com.bouchenna.rv_weather.service.WeatherForecastResponse
import com.bouchenna.rv_weather.models.WeatherResponse
import com.bouchenna.rv_weather.databinding.FragmentGalleryBinding
import com.bouchenna.rv_weather.service.WeatherApiService
import com.bumptech.glide.Glide

import com.github.mikephil.charting.charts.LineChart
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

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
//        lineGraphView = binding.idGraphView




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
                        binding.idHumidity.text ="Humidity: \n" +  humidity.toString() + " %"
                        binding.TempMin.text ="Temp Min : \n"+ weatherResponse?.main?.temp_min?.minus(273.15)?.roundToInt().toString() +" °C"
                        binding.TempMax.text = "Temp Max : \n"+weatherResponse?.main?.temp_max?.minus(273.15)?.roundToInt().toString() +" °C"
                        val uri = Uri.parse(
                            "https://openweathermap.org/img/w/" + weatherResponse?.weather?.get(
                                0
                            )?.icon + ".png"
                        )
                        val imageView = binding.icone
                        if (imageView != null) {
                            //Log.d("images", "pas null"+uri.toString())
                            context?.let {
                                Glide.with(it)
                                    .load(uri)
                                    .into(imageView)
                            }
                        }
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

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val dayFormat = SimpleDateFormat("EEEE")
                        binding.idTVHead.text = nom
                        binding.tempTextView.text = WeatherForecastResponse?.list?.get(0)?.main?.temp?.minus(273.15)?.roundToInt().toString() + " °C"
                        val headerRow = TableRow(context)
                        binding.description.text = WeatherForecastResponse?.list?.get(0)?.weather?.get(0)?.description + "\n"

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


                        val tempMin = TextView(context).apply {
                            text = "Temp"+"\n  Min (°C)"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        val tempMax = TextView(context).apply {
                            text = "Temp"+"\n  Max (°C)"
                            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        }

                        headerRow.addView(tempMin)
                        headerRow.addView(tempMax)

                        binding.tableLayout.addView(headerRow)
                        groupedWeatherForecasts?.forEach { (date, forecasts) ->
                            var tempSum = 0.0
                            var tempMin = Double.MAX_VALUE
                            var tempMax = Double.MIN_VALUE

                            forecasts.forEach { forecast ->
                                tempSum += forecast.main.temp
                                val temp = forecast.main.temp
                                if (temp < tempMin) {
                                    tempMin = temp
                                }
                                if (temp > tempMax) {
                                    tempMax = temp
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



                            val tempMinTextView = TextView(context)
                            tempMinTextView.text = "${tempMin.minus(273.15).roundToInt()}"
                            tempMinTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(tempMinTextView)
                            dataPoints.add(DataPoint(count.toDouble(), averageTemp.minus(273.15)))
                            val tempMaxTextView = TextView(context)
                            tempMaxTextView.text = "${tempMax.minus(273.15).roundToInt()}"
                            tempMaxTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                            tableRow.addView(tempMaxTextView)
                            count++;
                            binding.tableLayout.addView(tableRow)
                        }


//                        val dataPointsArray = dataPoints.toTypedArray()



//                        val simpleDateFormat = SimpleDateFormat("dd/MM")
//                        val series = LineGraphSeries(dataPointsArray)
//                        lineGraphView.addSeries(series)
//                        lineGraphView.gridLabelRenderer.horizontalAxisTitle = "Date"
//                        lineGraphView.gridLabelRenderer.verticalAxisTitle = "Température (°C)"
//                        lineGraphView.viewport.isScrollable = true
//                        lineGraphView.viewport.isScalable = true
//                        lineGraphView.viewport.setScalableY(true)
//                        lineGraphView.viewport.setScrollableY(true)
//
//                        val labelFormatter = DateAsXAxisLabelFormatter(context, simpleDateFormat)
//                        lineGraphView.gridLabelRenderer.labelFormatter = labelFormatter


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
                            val pollutionData = pollutionDataList[0]
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

                            val maxTempList = so2Values
                            val minTempList = nh3Values

                            setTemperatureData(maxTempList, minTempList)



                            val entries = mutableListOf<PieEntry>()
                            entries.add(PieEntry(co!!.toFloat(), "CO")) // Monoxyde de carbone
                            entries.add(PieEntry(no2!!.toFloat(), "NO2")) // Dioxyde d'azote
                            entries.add(PieEntry(o3!!.toFloat(), "O3")) // Ozone
                           /* entries.add(PieEntry(so2!!.toFloat(), "SO2")) // Dioxyde de soufre
                            entries.add(PieEntry(pm25!!.toFloat(), "PM2.5")) // Particules fines (PM2.5)
                            entries.add(PieEntry(pm10!!.toFloat(), "PM10")) // Particules en suspension (PM10)
                            entries.add(PieEntry(nh3!!.toFloat(), "NH3")) // Ammoniac
*/

                            val dataSet = PieDataSet(entries, "Air Pollution Components")
                            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                            val data = PieData(dataSet)
                            pieChart.apply {
                                description.isEnabled = true
                                description.text = "Diagramme de Pollution"
                                description.textColor = Color.BLACK
                                description.setPosition(300f,100f)
                            }
                            pieChart.data = data
                            pieChart.invalidate()
                        }
                    }

                }


                override fun onFailure(call: Call<AirPollutionResponse>, t: Throwable) {

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

        for (i in maxTempList.indices) {
            entriesMax.add(Entry(i.toFloat(), maxTempList[i]))
        }



        for (i in minTempList.indices) {
            entriesMin.add(Entry(i.toFloat(), minTempList[i]))
        }


        val dataSetMax = LineDataSet(entriesMax, "so2")
        val dataSetMin = LineDataSet(entriesMin, "nh3")


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

        val data = LineData(dataSets)

        lineChart?.data = data

        lineChart?.apply {
            val title = "Graphique du so2 et nh3"
            description.textColor = Color.BLACK
            description.textSize = 14f
            description.isEnabled = true
            description.text = title
            description.setPosition(500f, 50f)
        }

        lineChart?.invalidate()
    }


    fun setTemperatureData(maxTempList: List<Float>, minTempList: List<Float>) {
        setData(maxTempList, minTempList)
    }
}