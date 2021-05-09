package com.example.buzzer


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.row_users.view.*

class UserAdapter(options: FirestoreRecyclerOptions<UserModel>) :
    FirestoreRecyclerAdapter<UserModel, UserAdapter.UserAdapterVH>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterVH {
        return UserAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.row_users,parent,false))
    }

    override fun onBindViewHolder(holder: UserAdapterVH, position: Int, model: UserModel) {
        holder.firstName.text = model.firstName
        holder.bg.text = model.bloodgroup
        holder.city.text = model.city
        holder.number.text = model.number

    }

    class UserAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstName = itemView.name
        var bg = itemView.bg
        var city = itemView.city
        var number = itemView.cntno
    }
}