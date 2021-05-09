package com.example.buzzer

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_list.*

class UserList() : AppCompatActivity() {

   private val db:FirebaseFirestore = FirebaseFirestore.getInstance()
   private val collectionReference:CollectionReference = db.collection("citizen")
   var userAdapter:UserAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        setUpRecyclerView()
    }

    fun setUpRecyclerView() {
        val query : Query = db.collection("citizen")
        val firestoreRecyclerOptions : FirestoreRecyclerOptions<UserModel> = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        userAdapter = UserAdapter(firestoreRecyclerOptions);
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = userAdapter

    }

    override fun onStart() {
        super.onStart()
        userAdapter!!.startListening()

    }

    override fun onDestroy() {
        super.onDestroy()
        userAdapter!!.stopListening()
    }
}