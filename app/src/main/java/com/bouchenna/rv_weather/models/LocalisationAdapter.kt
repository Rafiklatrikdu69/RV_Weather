package com.bouchenna.rv_weather.models

import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.MainActivity

import com.bouchenna.rv_weather.databinding.ItemLayoutBinding
import com.bouchenna.rv_weather.service.WeatherApiService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlin.math.roundToInt


class LocalisationAdapter(private val listener: MainActivity) : ListAdapter<Localisation, LocalisationAdapter.LocalisationViewHolder>(
    DiffCardCallback()
) {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LocalisationViewHolder(
        ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: LocalisationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



    private class DiffCardCallback : DiffUtil.ItemCallback<Localisation>() {
        override fun areItemsTheSame(oldItem: Localisation, newItem: Localisation): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Localisation, newItem: Localisation): Boolean =
            oldItem == newItem
    }

    inner class LocalisationViewHolder(private val binding: ItemLayoutBinding):

        RecyclerView.ViewHolder(binding.root){
        fun bind(loc: Localisation){
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(WeatherApiService::class.java)

            api.getWeather(loc.coord?.latitude!!, loc.coord?.latitude!!, "e3b787e83e7b983adeca31847414a20e")
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val weatherResponse = response.body()


                            val uri = Uri.parse(
                                "https://openweathermap.org/img/w/" + weatherResponse?.weather?.get(
                                    0
                                )?.icon + ".png"
                            )

                            val description =
                                "<b>${weatherResponse?.weather?.get(0)?.description}</b>"
                            val formattedText =
                                loc.nom + " " + weatherResponse?.main?.temp?.minus(273.15)
                                    ?.roundToInt().toString() + " °C ( " + description + " )"

                            binding.titleTextView.text = Html.fromHtml(formattedText)
                        }

                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {

                    }
                })
            binding.itemLayout.setOnLongClickListener{listener.suppression(loc)}
            binding.itemLayout.setOnClickListener{listener.showMmeteo(loc)}
        }

    }
}