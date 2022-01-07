package com.example.houseclean

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.houseclean.databinding.FragmentHousesBinding
import com.example.houseclean.databinding.FragmentPerfilBinding

class HousesFragment : Fragment(R.layout.fragment_houses) {
    private var _binding: FragmentHousesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private val addHouse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) Toast.makeText(activity, "Added!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHousesBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addHouseBtn.setOnClickListener{
            val intent = Intent(activity, AddHouseActivity::class.java)
            intent.putExtra("houseID", "1")
            addHouse.launch(intent)
        }
    }
}