package com.example.healthimprovementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/*Allows users to select a type of workout they want to complete, view their history, or view all
workouts created by other users. Also provides the user an option to sign out of the app, so they can
log in again later.
 */
class Welcome : AppCompatActivity() {

    //Button Variables
    private lateinit var mWeightLossButton : Button
    private lateinit var mBulkUpButton : Button
    private lateinit var mEnduranceButton : Button
    private lateinit var mFlexibilityButton : Button
    private lateinit var mSignoutButton : Button
    private lateinit var mHistoryButton : Button
    private lateinit var mDiscoverButton : Button
    private lateinit var mAuth: FirebaseAuth

    //Variable to hold the UID string from login screen
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        mWeightLossButton = findViewById(R.id.weight_loss_button)
        mBulkUpButton = findViewById(R.id.bulk_up_button)
        mEnduranceButton = findViewById(R.id.endurance_button)
        mFlexibilityButton = findViewById(R.id.flexibility_button)
        mSignoutButton = findViewById(R.id.signout_button)
        mHistoryButton = findViewById(R.id.history_button)
        mDiscoverButton = findViewById(R.id.discoverWorkoutsButton)
        mAuth = FirebaseAuth.getInstance()

        //Acquire the UID from the intent LoginActivity passed in
        uid = intent.getStringExtra(USER_ID)

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(applicationContext, WorkoutActivity::class.java)
        intent.putExtra(USER_ID, uid)

        mWeightLossButton.setOnClickListener{
            Log.i(TAG, "Weight Loss Selected")
            intent.putExtra(WORKOUT_TYPE, WEIGHT_LOSS)
            startActivity(intent)
        }

        mBulkUpButton.setOnClickListener{
            Log.i(TAG, "Bulk Up Selected")
            intent.putExtra(WORKOUT_TYPE, BULK_UP)
            startActivity(intent)
        }

        mEnduranceButton.setOnClickListener{
            Log.i(TAG, "Endurance Selected")
            intent.putExtra(WORKOUT_TYPE, ENDURANCE)
            startActivity(intent)
        }

        mFlexibilityButton.setOnClickListener{
            Log.i(TAG, "Flexibility Selected")
            intent.putExtra(WORKOUT_TYPE, FLEXIBILITY)
            startActivity(intent)
        }


        mHistoryButton.setOnClickListener {
            Log.i(TAG, "History Selected")
            val intent = Intent(this, UserHistoryActivity::class.java)
            intent.putExtra(USER_ID, uid)
            startActivity(intent)
        }

        mDiscoverButton.setOnClickListener {
            Log.i(TAG, "Discover Workout Selected")
            val intent = Intent(this, DiscoverWorkoutsActivity::class.java)
            intent.putExtra(USER_ID, uid)
            startActivity(intent)
        }

        mSignoutButton.setOnClickListener {
            Log.i(TAG, "Signing Out User")
            Toast.makeText(this, "Signing Out...", Toast.LENGTH_LONG).show()

            mAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "Mine-Welcome"
        const val USER_ID = "USER_ID"
        const val WORKOUT_TYPE = "WORKOUT_TYPE"
        const val BULK_UP = "BULK_UP"
        const  val WEIGHT_LOSS = "WEIGHT_LOSS"
        const  val ENDURANCE = "ENDURANCE"
        const val FLEXIBILITY = "FLEXIBILITY"
    }
}