package com.sonney.valentin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bouchenna.rv_weather.Localisation
import com.bouchenna.rv_weather.MainActivity

import com.bouchenna.rv_weather.databinding.ItemLayoutBinding
import com.bouchenna.rv_weather.service.FireBase_db


class LocalisationAdapter(private val listener: MainActivity) : ListAdapter<Localisation, LocalisationAdapter.LocalisationViewHolder>(DiffCardCallback()) {


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
            binding.titleTextView.text = loc.nom
            binding.itemLayout.setOnLongClickListener{listener.suppression(loc)}
        }

    }
}
