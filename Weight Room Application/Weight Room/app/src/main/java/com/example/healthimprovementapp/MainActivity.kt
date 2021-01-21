package com.example.healthimprovementapp

/*"Weight Room" workout app by Owen Knott, Philip Corn, Omar Shawqi for UMD CMSC436 Fall2020

This app is meant to provide users a means of creating and recording custom workouts to keep
their bodies healthy.
Future functionality could include:
1. A means of "challenging" friends by sending a workout to them
2. A system of timers for cardiovascular workouts
3. Specific instructions and videos on how to safely perform basic workouts

Special thanks to the CMSC436 lab code, especially for the user authentication, adapters,
and realtime database starter code. This was modified heavily with help from Firebase.org's
docs to create the app as it currently stands.

*/

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/*Gives the user an option to register for an account, if they have not already, or to login to their
existing account
 */

class MainActivity : AppCompatActivity() {

    private var registerBtn: Button? = null
    private var loginBtn: Button? = null
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        //TESTING
        if (user != null) {
            //User is signed in
            Toast.makeText(applicationContext, "Welcome Back!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Welcome::class.java)
            intent.putExtra(USER_ID, mAuth!!.currentUser!!.uid)
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)

        initializeViews()

        registerBtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        loginBtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    //Initializes the two button views in the UI
    private fun initializeViews() {
        registerBtn = findViewById(R.id.register)
        loginBtn = findViewById(R.id.login)
    }

    companion object {
        const val USER_ID = "USER_ID"
    }

}
