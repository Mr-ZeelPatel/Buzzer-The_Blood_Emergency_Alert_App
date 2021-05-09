package com.example.buzzer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var id : EditText
    lateinit var passwd : EditText
    lateinit var login : Button
    lateinit var hospitalSignup : Button
    lateinit var citizenSignup : Button
    lateinit var oriID : EditText
    lateinit var oriPasswd : EditText


    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if(currentUser != null){
            Toast.makeText(this,"logedin",Toast.LENGTH_LONG).show()
            currentUser.reload()

            currentUser.let {
                var id = currentUser.email.toString()
                var splt = id.split("@")
                var del =splt[1].toString()
                if (del == "gmail.com")
                {
                    startActivity(Intent(this, citizenlogin::class.java))
                   

                }
                else
                {
                    startActivity(Intent(this, hospitallogin::class.java))
                }

            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        id = findViewById(R.id.id)
        passwd = findViewById(R.id.passwd)
        login = findViewById(R.id.login)
        hospitalSignup = findViewById(R.id.hsignup)
        citizenSignup = findViewById(R.id.csignup)

        hospitalSignup.setOnClickListener {
            val intent = Intent(this@MainActivity,hospital_signup::class.java)
            startActivity(intent)
        }

        citizenSignup.setOnClickListener {
            val intent = Intent(this@MainActivity,citizensignup::class.java)
            startActivity(intent)
        }

            login.setOnClickListener {
                var progressDialog = ProgressDialog(this@MainActivity)
                progressDialog.show()
                progressDialog.setContentView(R.layout.progress_dialog)
                progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                var entID = id.text.toString()
                var entpwd = passwd.text.toString()

                if(entID.isEmpty() || entpwd.isEmpty())
                {
                    Toast.makeText(this,"All fields are compulsory",Toast.LENGTH_LONG).show()
                }
                else {

                    auth.signInWithEmailAndPassword(entID, entpwd)
                        .addOnCompleteListener(
                        ) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                entID = entID.toString()
                                var list = entID.split("@")
                                var result = list[1]
                                val user = auth.getCurrentUser()
                                progressDialog.dismiss()
                                if (result == "hospital.com") {
                                    val intent = Intent(this, hospitallogin::class.java)
                                    startActivity(intent)

                                } else {
                                    val intent = Intent(this, citizenlogin::class.java)
                                    startActivity(intent)
                                }


                            } else {
                                progressDialog.dismiss()
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    this@MainActivity, "Email or Password is wrong",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }


                        }

                }

            }

        }
    }









