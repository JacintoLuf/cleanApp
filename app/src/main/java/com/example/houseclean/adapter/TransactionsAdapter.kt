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

class TransactionsAdapter(private val UID: String, private val transactions: MutableList<Transaction>? = arrayListOf()): RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.house_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        if (transactions != null) {
            return transactions.size
        }
        return 0
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val houseImg = itemView.findViewById<ImageView>(R.id.transactionHouseImg)
        private val name = itemView.findViewById<TextView>(R.id.costumerName)
        private val location = itemView.findViewById<TextView>(R.id.transactionLstLocation)
        private val addr = itemView.findViewById<TextView>(R.id.transactionLstAddress)
        private val start = itemView.findViewById<TextView>(R.id.transactionStart)
        private val end = itemView.findViewById<TextView>(R.id.transactionEnd)
        private val status = itemView.findViewById<TextView>(R.id.transactionLstStatus)
        private val button = itemView.findViewById<TextView>(R.id.transactionBtn)

        fun bind(pos: Int) {
            with(transactions?.get(pos)) {
                storage.child(UID.plus("/houses/".plus(this?.house?.ID)).plus("/housePic")).downloadUrl.addOnSuccessListener {
                    if (it != null) {
                        com.bumptech.glide.Glide.with(itemView).load(it).into(houseImg)
                    }
                }.addOnFailureListener{
                    houseImg.setImageResource(com.example.houseclean.R.drawable.ic_home)
                }
                name.setText(this?.clientName)
                location.setText(this?.house?.location)
                addr.setText(this?.house?.address)
                start.setText(this?.startDate.toString())
                end.setText(this?.limitDate.toString())
                status.setText(this?.status)
                if (this?.status != "canceled" || this?.status != "finished") button.setText("cancel")
                else button.isVisible = false
            }
        }
    }
}