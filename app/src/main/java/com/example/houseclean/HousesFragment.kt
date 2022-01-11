package com.example.houseclean

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.houseclean.adapter.HousesAdapter
import com.example.houseclean.databinding.FragmentHousesBinding
import com.example.houseclean.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HousesFragment : Fragment(R.layout.fragment_houses) {
    private var _binding: FragmentHousesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private val user = FirebaseAuth.getInstance().currentUser
    private var dbUser: User? = null
    private lateinit var adapter: HousesAdapter
    private lateinit var addHouse: ActivityResultLauncher<Intent>
    private val addTransaction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

        }
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

        updateDbUser()

        adapter = HousesAdapter(dbUser?.UID, dbUser?.houses)
        binding.houseLst.adapter = adapter
        binding.houseLst.layoutManager = LinearLayoutManager(activity)

        addHouse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) Toast.makeText(activity, "Added!", Toast.LENGTH_SHORT).show()
        }

        binding.addHouseBtn.setOnClickListener{
            val intent = Intent(activity, AddHouseActivity::class.java)
            if (dbUser?.houses?.isEmpty() == true) intent.putExtra("houseID", "0")
            else intent.putExtra("houseID", (dbUser?.houses?.size?.plus(1)).toString())
            intent.putExtra("user", dbUser)
            addHouse.launch(intent)
        }

        adapter.onItemClick = {
            openHouseDetails(it)
        }
        adapter.onItemLongClick = {
            addTransaction(it)
        }
    }

    private fun openHouseDetails(pos: Int) {

    }
    private fun addTransaction(pos: Int) {
        val intent = Intent(activity, AddTransactionActivity::class.java)
        intent.putExtra("house", dbUser?.houses?.get(pos))
        addTransaction.launch(intent)
    }

    fun updateDbUser() {
        mainActivity.database.getReference("Users").child(user?.uid.toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dbUser = snapshot.getValue(User::class.java)
                    if (dbUser?.houses.isNullOrEmpty()) binding.noHousesTxt.isVisible = true
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}