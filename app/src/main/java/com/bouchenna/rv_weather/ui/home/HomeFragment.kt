package com.bouchenna.rv_weather.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bouchenna.rv_weather.R
import com.bouchenna.rv_weather.databinding.FragmentHomeBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

class HomeFragment : Fragment() {

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
        if (!Places.isInitialized()) {
            Places.initialize(
                requireContext(),
                "AIzaSyAUhesh_MfnKVmEET8G6IKmDVaYUocE_yI"
            )
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.fragment)
                    as? AutocompleteSupportFragment

        autocompleteFragment?.setPlaceFields(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        )

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}