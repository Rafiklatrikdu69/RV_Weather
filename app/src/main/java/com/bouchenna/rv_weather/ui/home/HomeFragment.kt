package com.bouchenna.rv_weather.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bouchenna.rv_weather.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var test: Button
    private lateinit var user: FirebaseAuth


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
        test = binding.button

        //test.setOnClickListener { addBD() }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//     fun addBD(){
//
//        val loc =hashMapOf(
//            "contry" to "france",
//            "lat" to "14.15",
//            "long" to "15.15",
//            "nom" to "lyon",
//            "state" to "jsp",
//            "userId" to user.uid
//        )
//         FirebaseFirestore.getInstance().collection("Localisation").document().set(loc)
//            .addOnSuccessListener {
//                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: }")
//            }
//    }
}