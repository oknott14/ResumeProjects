package com.example.healthimprovementapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout
import com.example.healthimprovementapp.com.example.healthimprovementapp.WorkoutExercisesActivity
import com.google.firebase.database.*
import java.util.*

/*Displays a list of workouts based on the workout type passed to the activity by an intent. Allows
users to complete the workouts if they select one from the list. Workouts can be deleted if they are
long pressed, and the user selects Yes from the dialog shown.
 */
class WorkoutActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var selectWorkoutTextView : TextView
    private lateinit var buttonAddWorkout: Button
    private lateinit var listViewWorkouts: ListView
    private lateinit var workoutListAdapter : WorkoutListAdapter

    private lateinit var uid: String
    private lateinit var workoutType : String
    private lateinit var workouts : MutableList<Workout>

    private lateinit var mDatabase : DatabaseReference
    private lateinit var mWorkout: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        //Initializes UID and Workout Type here based on intent provided
        uid = intent.getStringExtra(USER_ID) as String
        workoutType = intent.getStringExtra(WORKOUT_TYPE) as String

        //Access the workout's node in the database
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid).child(workoutType)

        //Set up views for adding workouts
        editTextName = findViewById<View>(R.id.customWorkoutName) as EditText
        selectWorkoutTextView = findViewById(R.id.selectAWorkout)
        selectWorkoutTextView.text = "Select an existing ${formatWorkoutType(workoutType)} workout"
        buttonAddWorkout = findViewById<View>(R.id.addCustomWorkoutButton) as Button

        //setup list view and adapter
        listViewWorkouts = findViewById<View>(R.id.listViewWorkouts) as ListView
        workoutListAdapter = WorkoutListAdapter(this)
        listViewWorkouts.adapter = workoutListAdapter

        workouts = ArrayList()
    }

    override  fun onStart() {
        super.onStart()

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) { Log.i(TAG, "No data received") }
                workouts.clear()

                var dbWorkout : Workout? = null

                for (data in dataSnapshot.children) {
                    try {

                        val dataMap : Map<String, Object> = data.value as Map<String, Object>
                        dbWorkout = Workout(dataMap)

                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        workouts.add(dbWorkout!!)
                    }
                }

                val newAdapter : WorkoutListAdapter = WorkoutListAdapter(this@WorkoutActivity)
                newAdapter.addAll(workouts)
                workoutListAdapter = newAdapter
                listViewWorkouts.adapter = newAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, error.toString())
            }
        })

        buttonAddWorkout.setOnClickListener {
            addWorkout()
        }

        listViewWorkouts.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->


            //get the selected workout
            val workout = workoutListAdapter.getItem(i)

            //create an intent and package it up
            val intent = Intent(applicationContext, WorkoutExercisesActivity::class.java)
            intent.putExtra(WORKOUT, workout)
            intent.putExtra(USER_ID, uid)
            intent.putExtra(WORKOUT_TYPE, workoutType)
            startActivity(intent)
        }

        listViewWorkouts.onItemLongClickListener = AdapterView.OnItemLongClickListener {
            adapterView, view, i, l ->
            mWorkout = mDatabase.child(workoutListAdapter.getItem(i).workoutId as String)
            var mDialog = DeleteDialogFragment.newInstance(workoutListAdapter.getItem(i) as Object, WORKOUT)
            mDialog.show(supportFragmentManager, "DeleteDialog")
            true

        }
    }

    //Adds a workout to the list by starting AddWorkoutActivity for a result
    private fun addWorkout() {
        val name = editTextName.text.toString()

        if (!TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Adding a custom workout...", Toast.LENGTH_LONG).show()

            val exerciseListIntent = Intent(this, AddWorkoutActivity::class.java)
            exerciseListIntent.putExtra(WORKOUT_NAME, name)
            exerciseListIntent.putExtra(WORKOUT_TYPE, workoutType)
            exerciseListIntent.putExtra(USER_ID, uid)

            editTextName.setText("")
            startActivityForResult(exerciseListIntent, ADD_WORKOUT_REQUEST)

            //The workout is actually added in the onActivityResult function
        }
        else {
            Toast.makeText(this, "Please enter a workout name", Toast.LENGTH_LONG).show()
        }
    }


    /*Completes adding a user to the list as long as they did not cancel their addition in
    AddWorkoutActivity.
     */
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == ADD_WORKOUT_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                //Get workout from intent
                val workout = data?.getParcelableExtra<Workout>(WORKOUT_NAME)!!
                //workout.writeToDatabase(mDatabase!!)                                                              //Am now adding to database in AddWorkoutActivity.kt
                workoutListAdapter.add(workout)


                Log.i(TAG, "Workout received and added in onActivityResult")
            } else if (resultCode == RESULT_CANCELED){
                Log.i(TAG, "Add workout canceled")
            } else {
                Toast.makeText(this,
                    "Were sorry something went wrong. Please try to add your workout again!", Toast.LENGTH_LONG)
            }
        }
    }

    /*Removes a workout from the database and the list*/
    internal fun deleteWorkout(workout: Workout) {
        mWorkout.removeValue()
    }

    //Takes the passed workout type from the intent and formats it so that it can be displayed on the UI
    private fun formatWorkoutType(wType : String) : String {
        var outType = wType.toLowerCase()

        if (outType.contains(Regex("_"))) {
            outType = outType.replace("_", " ")
        }

        return outType
    }

    companion object {
        const val TAG = "Mine-WorkoutActivity"
        const val WORKOUT_NAME = "WORKOUT_NAME"
        const val ADD_WORKOUT_REQUEST = 0
        const val WORKOUT = "WORKOUT"
        const val USER_ID = "USER_ID"
        const val WORKOUT_TYPE = "WORKOUT_TYPE"
    }

}
