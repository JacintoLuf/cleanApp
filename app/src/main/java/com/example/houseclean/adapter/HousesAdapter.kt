package com.example.houseclean.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.houseclean.R
import com.example.houseclean.model.House
import com.google.firebase.storage.FirebaseStorage
class HousesAdapter(private val UID: String, private val houses: MutableList<House>? = arrayListOf()): RecyclerView.Adapter<HousesAdapter.HouseViewHolder>() {
    private val storage = FirebaseStorage.getInstance().reference
    //private var houses = allHouses?.filter { it.deleted == false }?.sortedBy { it.ID.toInt() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.house_item, parent, false)
        return HouseViewHolder(view)
        /*return HouseViewHolder(
            HouseItemUI().createView(
                AnkoContext.create(parent.context, parent)
            )
        )*/
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        if (houses != null) {
            return houses.size
        }
        return 0
    }

    inner class HouseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img = itemView.findViewById<ImageView>(R.id.houseLstImg)
        private val title = itemView.findViewById<TextView>(R.id.houseLstTitle)
        private val addr = itemView.findViewById<TextView>(R.id.houseLstAddress)

        fun bind(pos: Int) {
            with(houses?.get(pos)) {
                storage.child(UID.plus("/houses/".plus(this?.ID)).plus("/housePic")).downloadUrl.addOnSuccessListener {
                    if (it != null) {
                        Glide.with(itemView).load(it).into(img)
                    }
                }.addOnFailureListener{
                    img.setImageResource(R.drawable.ic_home)
                }
                title.setText("House ".plus((pos.toInt()+1).toString()))
                addr.setText(this?.address)
            }
        }
    }
}

