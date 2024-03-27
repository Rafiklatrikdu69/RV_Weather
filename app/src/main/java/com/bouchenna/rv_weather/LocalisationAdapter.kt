package com.sonney.valentin

import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.Localisation
import com.bouchenna.rv_weather.MainActivity
import com.bouchenna.rv_weather.WeatherResponse

import com.bouchenna.rv_weather.databinding.ItemLayoutBinding
import com.bouchenna.rv_weather.service.FireBase_db
import com.bouchenna.rv_weather.service.WeatherApiService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.sql.DataSource
import kotlin.contracts.contract
import com.bumptech.glide.request.target.Target

import kotlin.math.roundToInt


class LocalisationAdapter(private val listener: MainActivity) : ListAdapter<Localisation, LocalisationAdapter.LocalisationViewHolder>(DiffCardCallback()) {
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


                            val uri = Uri.parse("https://openweathermap.org/img/w/" + weatherResponse?.weather?.get(0)?.icon + ".png")
                            val imageView = binding.icone
                            Glide.with(listener)
                                .load(uri)
                                .override(300, 200)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Log.d("fail","failed")
                                        binding.icone.visibility = View.GONE
                                        val description = "<b>${weatherResponse?.weather?.get(0)?.description}</b>"
                                        val formattedText = loc.nom + " " + weatherResponse?.main?.temp?.minus(273.15)?.roundToInt().toString() + " °C ( " + description+" )"

                                        binding.titleTextView.text = Html.fromHtml(formattedText)

                                        return false
                                    }



                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: com.bumptech.glide.load.DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Log.d("succes","succes")
                                        binding.titleTextView.text = loc.nom +" "+weatherResponse?.main?.temp?.minus(273.15)?.roundToInt().toString() +" °C"

                                        binding.icone.visibility = View.VISIBLE
                                        return false
                                    }
                                })
                                .into(imageView)


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