package com.example.buzzer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Location : Service() {


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    lateinit var emailID : String
        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            onTaskRemoved(intent)
            fusedLocationProviderClient = FusedLocationProviderClient(this)
            emailID = Firebase.auth.currentUser?.email.toString()
            updateLocationTracking()
            return START_STICKY
        }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    @SuppressLint("MissingPermission")
private fun updateLocationTracking() {

            val request = LocationRequest().apply {
                interval = 5000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }


val locationCallback = object : LocationCallback() {
    override fun onLocationResult(result: LocationResult?) {
        super.onLocationResult(result)
            result?.locations?.let { locations ->
                for(location in locations) {
                    var latitude = location.latitude
                    var longitude = location.longitude
                    db.collection("citizen").document(emailID)
                        .update("latitude", latitude)

                    db.collection("citizen").document(emailID)
                        .update("longitude", longitude)
                }

        }
    }
}
}
