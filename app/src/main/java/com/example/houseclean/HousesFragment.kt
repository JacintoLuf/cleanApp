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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.houseclean.adapter.HousesAdapter
import com.example.houseclean.databinding.FragmentHousesBinding
import com.google.firebase.auth.FirebaseAuth

class HousesFragment : Fragment(R.layout.fragment_houses) {
    private var _binding: FragmentHousesBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
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

        binding.houseLst.adapter = HousesAdapter(user?.uid.toString(), mainActivity.dbUser.houses)
        binding.houseLst.layoutManager = LinearLayoutManager(activity)

        binding.addHouseBtn.setOnClickListener{
            val intent = Intent(activity, AddHouseActivity::class.java)
            if (mainActivity.dbUser.houses?.isEmpty() == true) intent.putExtra("houseID", "0")
            else intent.putExtra("houseID", (mainActivity.dbUser.houses?.size?.plus(1)).toString())
            intent.putExtra("user", mainActivity.dbUser)
            addHouse.launch(intent)
        }
    }
}