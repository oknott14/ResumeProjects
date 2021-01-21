package com.example.healthimprovementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/*Allows a user to register for an account using Firebase's email and password authentication.
 */
class RegistrationActivity : AppCompatActivity() {

    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var confirmPasswordTV : EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var validator = Validators()

    private var mAuth: FirebaseAuth? = null
    private var mDatabase : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference("users")

        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        confirmPasswordTV = findViewById(R.id.confirmPassword)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)

        regBtn!!.setOnClickListener { registerNewUser() }

    }

    /*Registers a user for a new account if neither field is empty and the user's input does not match
    an already existing user account.
     */
    private fun registerNewUser() {

        val email: String = emailTV!!.text.toString()
        val password: String = passwordTV!!.text.toString()
        val confirmPassword : String = confirmPasswordTV!!.text.toString()
        //Color value for invalid selections
        val validColor = resources.getColor(R.color.valid)
        val invalidColor = resources.getColor(R.color.invalid)

        if (!validator.validEmail(email)) {
            emailTV!!.setTextColor(invalidColor)
            Toast.makeText(applicationContext, "Please enter a valid email!", Toast.LENGTH_LONG).show()
            return
        } else {
            emailTV!!.setTextColor(validColor)
        }

        if (!validator.validPassword(password)) {
            passwordTV!!.setTextColor(invalidColor)
            confirmPasswordTV!!.setTextColor(invalidColor)
            Toast.makeText(applicationContext, "Please enter a valid password!", Toast.LENGTH_LONG).show()
            return
        } else {
            passwordTV!!.setTextColor(validColor)
            confirmPasswordTV!!.setTextColor(validColor)
        }

        if (password != confirmPassword) {
            passwordTV!!.setTextColor(invalidColor)
            confirmPasswordTV!!.setTextColor(invalidColor)
            Toast.makeText(applicationContext, "Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        } else {
            passwordTV!!.setTextColor(validColor)
            confirmPasswordTV!!.setTextColor(validColor)
        }

        progressBar!!.visibility = View.VISIBLE

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Welcome!", Toast.LENGTH_LONG).show()
                    progressBar!!.visibility = View.GONE

                    val uid = mAuth!!.currentUser!!.uid
                    val user = User(uid, ArrayList())

                    //Sets up user path and default workouts in database
                    addUserToDatabase(uid)

                    val intent = Intent(this@RegistrationActivity, Welcome::class.java)
                    intent.putExtra(USER_ID, uid)
                    startActivity(intent)

                } else {
                    //Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                    progressBar!!.visibility = View.GONE
                }
            }.addOnFailureListener { e ->
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()}
    }

    /*Adds a new node to users under the mAuth uid (will be different each time already) and then
    adds the default workouts from their node in the database to the user's uid node. The workout ids
    are constant among all users and the default-workouts node*/
    private fun addUserToDatabase(uid : String) {
        val userRef = mDatabase!!.child(uid) //DB.child("users").child(uid)
        val defaultWorkouts = FirebaseDatabase.getInstance().getReference(DEFAULT_WORKOUTS)

        //Read in default workouts
        defaultWorkouts.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) { Log.i(TAG, "No data received") }

                var workoutType : String? = null
                var dataId : String? = null
                var workout : Workout? = null
                for (typeData in snapshot.children) {
                    workoutType = typeData.key as String

                    for (idData in typeData.children) {
                        dataId = idData.key as String
                        workout = Workout(idData.value as Map<String, Object>)

                        userRef.child(workoutType).child(dataId).setValue(workout)
                    }
                }
            }

        })
    }

    companion object {
        const val TAG = "Mine-RegistrationActivity"
        const val USER_ID = "USER_ID"
        const val DEFAULT_WORKOUTS = "default-workouts"
    }
}
