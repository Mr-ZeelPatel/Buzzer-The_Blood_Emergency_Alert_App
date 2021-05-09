package com.example.buzzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.noti_card.view.*
import kotlinx.android.synthetic.main.noti_card.view.bg


class CitizenAdapter(val context: Context, val allData : List<DocumentSnapshot>): RecyclerView.Adapter<CitizenAdapter.CitizenViewHolder>() {
    class CitizenViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitizenViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.noti_card, parent, false)
        return CitizenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: CitizenViewHolder, position: Int) {
        val currentItemData = allData[position]

        holder.itemView.h_name.text = currentItemData.get("hospital_name").toString()
        holder.itemView.h_add.text = currentItemData.get("hospital_address").toString()
        holder.itemView.dt.text = currentItemData.get("dateTime").toString()
        holder.itemView.bg.text = currentItemData.get("blood_group").toString()
        holder.itemView.h_no.text = currentItemData.get("h_number").toString()
    }
}