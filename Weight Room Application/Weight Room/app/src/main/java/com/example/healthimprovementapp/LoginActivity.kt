package com.example.healthimprovementapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

//Allows the user to login to their account using the Firebase email and password authentication
class LoginActivity : AppCompatActivity() {

    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)

        loginBtn!!.setOnClickListener { loginUserAccount() }
    }

    /*Tries to log the user into their account. Succeeds if neither field is empty, both fields
    pass their validation, and .
     */
    private fun loginUserAccount() {

        progressBar!!.visibility = View.VISIBLE
        val email: String = userEmail?.text.toString()
        val password: String = userPassword?.text.toString()

        //Check if they are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar!!.visibility = View.GONE
            if (task.isSuccessful) {

                Toast.makeText(applicationContext, "Welcome Back!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginActivity, Welcome::class.java)
                intent.putExtra(USER_ID, mAuth!!.currentUser!!.uid)
                startActivity(intent)

            } else {
                Toast.makeText(applicationContext, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
            }
        }

    }

    companion object {
        const val USER_ID = "USER_ID"
    }
}