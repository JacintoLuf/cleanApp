package com.example.houseclean.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.houseclean.R
import com.example.houseclean.model.Transaction
import com.google.firebase.storage.FirebaseStorage

class TransactionsAdapter(private val UID: String?, private val transactions: MutableList<Transaction>? = arrayListOf()): RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = if (transactions.isNullOrEmpty()) 0 else transactions.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val houseImg = itemView.findViewById<ImageView>(R.id.transactionHouseImg)
        private val addr = itemView.findViewById<TextView>(R.id.transactionLstAddress)
        private val status = itemView.findViewById<TextView>(R.id.transactionLstStatus)
        private val button = itemView.findViewById<TextView>(R.id.transactionBtn)
        private val clientN = itemView.findViewById<TextView>(R.id.clientName)
        private val waiting = itemView.findViewById<TextView>(R.id.waiting)

        fun bind(pos: Int) {
            with(transactions?.get(pos)) {
                storage.child(UID.plus("/houses/".plus(this?.house?.ID)).plus("/housePic")).downloadUrl.addOnSuccessListener {
                    if (it != null) {
                        com.bumptech.glide.Glide.with(itemView).load(it).into(houseImg)
                    }
                }.addOnFailureListener{
                    houseImg.setImageResource(R.drawable.ic_home)
                }
                status.setText(this?.status)
                if (this?.status == "canceled" || this?.status == "finished") {
                    button.isVisible = false
                } else if (this?.status == "waiting") {
                    clientN.isVisible = false
                    addr.isVisible = false
                    waiting.isVisible = true
                } else {
                    clientN.setText(this?.clientName)
                    addr.setText(this?.house?.address)
                }
                button.setText("cancel")
            }
        }
    }
}