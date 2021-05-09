package com.example.buzzer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_citizenlogin.*
import kotlinx.android.synthetic.main.activity_user_list.*


class citizenlogin : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db.collection("notifications")
   // var notificationAdapter:citizen_notification_adapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizenlogin)
        var emaili = FirebaseAuth.getInstance().currentUser?.email.toString()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(this.toString(), "Fetching FCM registration token failed", task.exception)

                } else {
                    // Get new FCM registration token
                    var token = task.result
                    db.collection("citizen").document("$emaili")
                        .update("token", token)

                }
            })
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
        startService(Intent(applicationContext, Location::class.java))

        db.collection("notifications").whereEqualTo("email", emaili).get()
            .addOnSuccessListener {
                val adapter = CitizenAdapter(this, it.documents)
                re_citizen.adapter = adapter
                re_citizen.layoutManager = LinearLayoutManager(this)
            }.addOnFailureListener{
                Toast.makeText(this, "Failed to Load Data", Toast.LENGTH_SHORT).show()
            }

//        setUpRecyclerView()
    }

  /*  private fun setUpRecyclerView() {
        val query : Query = db.collection("notifications")
        val firestoreRecyclerOptions : FirestoreRecyclerOptions<notification_model> = FirestoreRecyclerOptions.Builder<notification_model>()
            .setQuery(query, notification_model::class.java)
            .build()

     notificationAdapter = citizen_notification_adapter(firestoreRecyclerOptions);
        re_citizen.layoutManager = LinearLayoutManager(this)
        re_citizen.adapter = notificationAdapter
    }*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_citizen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
//        notificationAdapter!!.startListening()

    }

    override fun onDestroy() {
        super.onDestroy()
//        notificationAdapter!!.stopListening()
    }
}