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
import androidx.core.view.isVisible
import com.example.houseclean.databinding.FragmentInboxBinding
import com.example.houseclean.databinding.FragmentPerfilBinding
import com.example.houseclean.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class InboxFragment : Fragment(R.layout.fragment_inbox) {
    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private val user = FirebaseAuth.getInstance().currentUser
    private var dbUser: User? = null
    private val addTransaction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) Toast.makeText(activity, "Added!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_map, container, false)
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDbUser()

        /*binding.addInboxBtn.setOnClickListener{
            val intent = Intent(activity, AddTransactionActivity::class.java)
            if (mainActivity.dbUser.houses?.isEmpty() == true) intent.putExtra("transactionID", "0")
            else intent.putExtra("transactionID", (mainActivity.dbUser.houses?.size?.plus(1)).toString())
            intent.putExtra("user", mainActivity.dbUser)
            addTransaction.launch(intent)
        }*/
    }

    fun updateDbUser() {
        mainActivity.database.getReference("Users").child(user?.uid.toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dbUser = snapshot.getValue(User::class.java)
                    if (dbUser?.transactions.isNullOrEmpty()) binding.noTransactionsTxt.isVisible = true
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}