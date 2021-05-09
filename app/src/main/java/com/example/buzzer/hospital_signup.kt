package com.example.buzzer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class hospital_signup : AppCompatActivity() {

    private val TAG = "hospitalsignup"

    lateinit var hospitalName: EditText
    lateinit var hospitalID: EditText
    lateinit var password: EditText
    lateinit var cpassword: EditText
    lateinit var phone: EditText
    lateinit var address: EditText
    lateinit var city: EditText
    lateinit var state: EditText
    lateinit var button : Button
    private lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_signup)
        auth = Firebase.auth
        hospitalName = findViewById(R.id.hospitalname)
        hospitalID = findViewById(R.id.hid)
        password = findViewById(R.id.hpwd)
        cpassword = findViewById(R.id.cpwd)
        phone = findViewById(R.id.cntno)
        address = findViewById(R.id.haddress1)
        city = findViewById(R.id.hcity)
        state = findViewById(R.id.hstate)
        button = findViewById(R.id.btnsignup)


        button.setOnClickListener{

            hinitialise()
        }

    }


    private fun hinitialise() {

        var HospitalName = hospitalName.text.toString()
        var HospitalID = hospitalID.text.toString()
        var HospitalCntNo = phone.text.toString()
        var HospitalAdd = address.text.toString()
        var HospitalCity = city.text.toString()
        var HospitalState = state.text.toString()
        var HospitalPassword = password.text.toString()
        var HospitalCPassword = cpassword.text.toString()

        if (HospitalName.isEmpty() || HospitalID.isEmpty() || HospitalCntNo.isEmpty() || HospitalAdd.isEmpty() || HospitalCity.isEmpty() || HospitalState.isEmpty() || HospitalCPassword.isEmpty())
        {
            Toast.makeText(this,"All fields are compulsory",Toast.LENGTH_LONG).show()
        }

        else if(HospitalPassword != HospitalCPassword)
        {
            Toast.makeText(this,"Password and confirm password are not matched",Toast.LENGTH_LONG).show()
        }
        else {
            var progressDialog = ProgressDialog(this@hospital_signup)
            progressDialog.show()
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            auth.createUserWithEmailAndPassword(HospitalID, HospitalPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                       val user = auth.currentUser

                        val hospital: MutableMap<String, Any> =
                            HashMap()


                        hospital.put("name", HospitalName)
                        hospital.put("email" , HospitalID)
                        hospital.put("number", HospitalCntNo)
                        hospital.put("address", HospitalAdd)
                        hospital.put("city", HospitalCity)
                        hospital.put("state", HospitalState)
                        hospital.put("password", HospitalPassword)
                        hospital.put("latitude", 0)
                        hospital.put("longitude", 0)


                        db.collection("hospital").document(HospitalID)
                            .set(hospital)
                            .addOnSuccessListener { documentReference ->
                                Firebase.auth.signOut()
                                progressDialog.dismiss()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                    e -> Log.w(TAG, "Error adding document", e)
                                progressDialog.dismiss()}


                    } else {
                        progressDialog.dismiss()
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Signup failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }

        }
    }
}