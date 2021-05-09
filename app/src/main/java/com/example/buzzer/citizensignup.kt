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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


@Suppress("DEPRECATION")
class citizensignup : AppCompatActivity() {
    private val TAG = "citizensignup"
    
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var cpassword: EditText
    lateinit var phone: EditText
    lateinit var address: EditText
    lateinit var city: EditText
    lateinit var state: EditText
    lateinit var bg : EditText
    lateinit var button : Button

    lateinit private var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizensignup)

        auth = Firebase.auth

        firstName = findViewById(R.id.hospitalname)
        lastName = findViewById(R.id.lname)
        email = findViewById(R.id.eid)
        password = findViewById(R.id.hpwd)
        cpassword = findViewById(R.id.cpwd)
        phone = findViewById(R.id.pno)
        address = findViewById(R.id.haddress1)
        city = findViewById(R.id.hcity)
        state = findViewById(R.id.hstate)
        bg = findViewById(R.id.blood_group)
        button = findViewById(R.id.button)


        button.setOnClickListener{

            var etfirstName = firstName.text.toString()
            var etlastName = lastName.text.toString()
            var etEmail = email.text.toString()
            var etPno = phone.text.toString()
            var etAdd = address.text.toString()
            var etCity = city.text.toString()
            var etState = state.text.toString()
            var etPassword = password.text.toString()
            var etCPassword = cpassword.text.toString()
            var etBloodGroup = bg.text.toString()


if(etfirstName.isEmpty() ||etlastName.isEmpty()|| etEmail.isEmpty()|| etPno.isEmpty()|| etAdd.isEmpty()|| etCity.isEmpty()|| etState.isEmpty()|| etBloodGroup.isEmpty())
{
    Toast.makeText(this,"All fields are compulsory",Toast.LENGTH_LONG).show()
}
            else if (etPassword != etCPassword){
                 Toast.makeText(this,"Password and Confirm password are not matched",Toast.LENGTH_LONG).show()
            }
            else {
                 var progressDialog = ProgressDialog(this@citizensignup)
                 progressDialog.show()
                 progressDialog.setContentView(R.layout.progress_dialog)
                  progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                 auth.createUserWithEmailAndPassword(etEmail, etPassword)
                   .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser


                var citizen: MutableMap<String, Any> = HashMap()
                citizen.put("firstName", etfirstName)
                citizen.put("lastName", etlastName)
                citizen.put("email", etEmail)
                citizen.put("address", etAdd)
                citizen.put("number", etPno)
                citizen.put("city", etCity)
                citizen.put("state", etState)
                citizen.put("password",etPassword)
                citizen.put("bloodgroup", etBloodGroup)
                citizen.put("latitude", 0)
                citizen.put("longitude", 0)
                citizen.put("diff",100)
                citizen.put("token","")


                db.collection("citizen").document(etEmail)
                    .set(citizen)
                    .addOnSuccessListener { documentReference ->
                        Firebase.auth.signOut()
                        progressDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e)
                    progressDialog.dismiss()}

            } else {
                        progressDialog.dismiss()
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()

            }


        }


}


                }

        }

    }
