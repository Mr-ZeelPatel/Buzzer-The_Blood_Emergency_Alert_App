package com.example.buzzer

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@Suppress("DEPRECATION")
class hospitallogin() : AppCompatActivity() {
    lateinit var buzz: Button
    lateinit var bg : EditText
    var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    var lat by Delegates.notNull<Double>()
    var lon by Delegates.notNull<Double>()
    var mylat by Delegates.notNull<Double>()
    var mylo by Delegates.notNull<Double>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospitallogin)


        buzz = findViewById(R.id.buzz)

        bg = findViewById(R.id.bgText)

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
                10
            )
        }
        val intent: Intent = Intent(applicationContext, HLocation::class.java)
        startService(intent)
         buzz.setOnClickListener {
            var bloodGroup = bg.text.toString()
            if(bloodGroup.isNotEmpty()) {
                if (bloodGroup.toLowerCase() == "ab+" || bloodGroup.toLowerCase() == "ab-" || bloodGroup.toLowerCase() == "a+" || bloodGroup.toLowerCase() == "a-" || bloodGroup.toLowerCase() == "b+" || bloodGroup.toLowerCase() == "b-" || bloodGroup.toLowerCase() == "o+" || bloodGroup.toLowerCase() == "o-") {
                    getLocationAndCompare(bloodGroup)
                } else {
                    Toast.makeText(this, "Enter valid blood group", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this,"Please enter blood group",Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hospital,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.database -> {
                val intent = Intent(this, UserList::class.java)
                startActivity(intent)
            }

            R.id.hlogout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocationAndCompare(bloodGroup: String) {
        var progressDialog = ProgressDialog(this@hospitallogin)
        progressDialog.show()
        progressDialog.setContentView(R.layout.alert_pogress)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //      val current = auth.currentUser?.email.toString()
        var blood_group = bloodGroup
        var emaili = FirebaseAuth.getInstance().currentUser?.email.toString()


        db.collection("hospital").whereEqualTo("email", "$emaili")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    mylat = document.get("latitude") as Double
                    mylo = document.get("longitude") as Double
                    var h_add = document.get("address") as String
                    var h_name = document.get("name") as String
                    val hnum = document.get("number") as String


                    db.collection("citizen")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if(blood_group.toLowerCase() == document.get("bloodgroup").toString().toLowerCase()) {
                                    lat = document.get("latitude") as Double
                                    lon = document.get("longitude") as Double
                                    var token = document.get("token") as String

                                    var dlon = 0.00
                                    if (lon > mylo) {
                                        dlon = lon - mylo
                                    } else {
                                        dlon = mylo - lon
                                    }

                                    var emailID = document.get("email").toString()
                                    var distance =
                                        sin(deg2rad(mylat as Double)) * sin(deg2rad(lat as Double)) + (cos(
                                            deg2rad(
                                                mylat
                                            )
                                        )
                                                * cos(x = deg2rad(lat as Double))
                                                * cos(x = deg2rad(dlon)))
                                    distance = acos(x = distance)
                                    distance = rad2deg(distance)
                                    // distance in miles
                                    distance = distance.times(60) * 1.1515
                                    //distance in km
                                    distance = distance * 1.609344
                                    //   var bg: String = document.get("bloodgroup") as String

                                     if (distance < 5) {
                                    //add notofication data to firebase
                                         var title = h_name
                                         var message = h_add
                                         var recipientToken = token
                                         if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                             PushNotification(
                                                 NotificationData(title, message),
                                                 recipientToken
                                             ).also {
                                                 sendAlert(it)
                                             }
                                         }

                                    addNotificationToFirebase(
                                        emailID,
                                        h_name,
                                        h_add,
                                        bloodGroup,
                                        hnum
                                    )


                                    }


                                    db.collection("citizen").document(emailID)
                                        .update("diff", distance)

                                }
                            }
                            progressDialog.dismiss()
                        }

                        .addOnFailureListener { exception ->
                            Log.d(this.toString(), "Error getting documents: ", exception)
                            progressDialog.dismiss()
                        }


                }
            }

    }

    private fun sendAlert(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch  {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(this.toString(), "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(this.toString(), response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(this.toString(), e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNotificationToFirebase(
        email: String,
        hname: String,
        hadd: String,
        bg: String,
        hnum: String
    ) {

        var current = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss.SSS")
        var formatted = current.format(formatter).toString()
        var finalDoc = "$formatted&$email"
        val notifications: MutableMap<String, Any> =
            HashMap()



        notifications.put("email", email)
        notifications.put("hospital_name" , hname)
        notifications.put("hospital_address", hadd)
        notifications.put("blood_group", bg)
        notifications.put("h_number", hnum)
        notifications.put("dateTime",formatted)




        db.collection("notifications").document(finalDoc)
            .set(notifications)
            .addOnSuccessListener { documentReference ->
              Toast.makeText(this,"notification Added",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { Toast.makeText(this, "Error adding document",Toast.LENGTH_SHORT).show() }
    }


    private fun rad2deg(distance: Double): Double {
        return (distance * 180.0 / Math.PI)
    }

    private fun deg2rad(mylat: Double): Double {
        return (mylat * Math.PI / 180.0)
    }


}






