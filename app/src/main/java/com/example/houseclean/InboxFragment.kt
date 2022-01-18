package com.example.houseclean

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.houseclean.adapter.TransactionsAdapter
import com.example.houseclean.databinding.FragmentInboxBinding
import com.example.houseclean.model.Transaction
import com.example.houseclean.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InboxFragment : Fragment(R.layout.fragment_inbox) {
    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private var dbUser: User? = null
    private lateinit var dialog: AlertDialog
    private var transactions: MutableList<Transaction> = arrayListOf()
    private lateinit var selectedTrans: Transaction
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)

        transactions = checkTransactions()
        binding.noTransactionsTxt.isVisible = transactions.isNullOrEmpty()
        adapter = TransactionsAdapter(dbUser?.UID, transactions)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inboxLst.adapter = adapter
        binding.inboxLst.layoutManager = LinearLayoutManager(activity)

        val builder = AlertDialog.Builder(activity)
        val view = View.inflate(activity, R.layout.cancel_trans_dialog, null)
        dialog = builder.setView(view).create()
        val btn = view.findViewById<FloatingActionButton>(R.id.cleanHouseBtn)
        btn.setOnClickListener {
            cancelTrans()
            dialog.dismiss()
        }

        adapter.onItemLongClick = {
            selectedTrans = transactions[it]
            if (selectedTrans.completed != true && selectedTrans.status != "canceled") {
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
    }

    private fun cancelTrans() {
        selectedTrans.status = "canceled"
        database.getReference("Transactions").child(selectedTrans.transactionID.toString())
            .setValue(selectedTrans).addOnSuccessListener {
                Toast.makeText(activity, "Canceled!" , Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkTransactions() : MutableList<Transaction> {
        var tmpTrans = arrayListOf<Transaction>()
        database.getReference("Transactions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if (it.child("clientID").getValue(String::class.java).equals(user?.uid.toString()))
                        tmpTrans.add(it.getValue(Transaction::class.java)!!)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return tmpTrans
    }
}