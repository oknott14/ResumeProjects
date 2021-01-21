package com.example.healthimprovementapp.com.example.healthimprovementapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthimprovementapp.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WorkoutExercisesActivity : AppCompatActivity(), EditExerciseDialogFragment.EditListener,
    SubmitDialogFragment.SubmitListener {

    private lateinit var mWorkoutNameView : TextView
    private lateinit var mListView : ListView
    private lateinit var mListAdapter : ExerciseListAdapter
    private lateinit var mSubmitButton : View
    private lateinit var mWorkout : Workout
    private lateinit var mWorkoutType : String
    private lateinit var uid : String
    private lateinit var mDatabase : DatabaseReference
    private lateinit var mHistoryDatabase : DatabaseReference
    private lateinit var mUserDatabase : DatabaseReference

    private var changesMade = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_exercise)

        //Get workout from the intent
        mWorkout = intent.getParcelableExtra(WORKOUT)
        uid = intent.getStringExtra(USER_ID)
        mWorkoutType = intent.getStringExtra(WORKOUT_TYPE)
        if (mWorkout == null) {
            Log.i(TAG, "Intent Data not received properly... finishing")
            finish()
        }

        //Init database reference
        mDatabase = FirebaseDatabase.getInstance().reference
        mHistoryDatabase = mDatabase.child("user-history").child(uid).child(mWorkoutType)
        mUserDatabase = mDatabase.child("users").child(uid).child(mWorkoutType)

        //Set workout name on UI
        mWorkoutNameView = findViewById(R.id.workoutName)
        mWorkoutNameView.text = mWorkout.workoutName

        //Set mListView and add mSubmitButton as a footer to the list
        mListView = findViewById(R.id.exerciseList)
        mListView.setFooterDividersEnabled(true)
        mSubmitButton =
            (applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.submit_workout_footer,
                null,
                false
            )
        mListView.addFooterView(mSubmitButton)

        //Setup List Adapter
        mListAdapter = ExerciseListAdapter(this)
        mListView.adapter = mListAdapter
        addAllExercises(mWorkout)
    }

    override fun onStart() {
        super.onStart()

        //Submits the workout
        mSubmitButton.setOnClickListener {
            submitWorkout()
        }

        //Allows users to edit exercises in the workout
        mListView.setOnItemClickListener { adapterView, view, i, l ->
            val dialog = EditExerciseDialogFragment.newInstance(i,
                (mListAdapter.getItem(i) as Exercise).exerciseName)
            dialog.show(supportFragmentManager, "EditExerciseDialog")
        }

    }

    /*Inherited function from the EditExerciseListener interface that submits edits to an exercise and saves
    the changes in the workout list*/
    override fun onDialogPositiveClick(pos: Int, name: String, sets: Int, reps: Int, weight: Int) {
        mListAdapter.editExerciseAt(pos, name, sets, reps, weight)
        mWorkout.workoutExercises[pos] = Exercise(name,sets,reps,weight)
        changesMade = true
    }

    /*Inherited function from EditExerciseListener interface that shows a toast so that the user
    knows their changes were canceled*/
    override fun onDialogNegativeClick() {
        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
    }

    //Adds all exercises into the list view
    private fun addAllExercises(workout : Workout) {
        mListAdapter.addAll(workout.workoutExercises)
    }

    /*Submits workouts to the database if they are not edited or launches a dialog asking if the user
    wants to submit the workout as a copy
     */
    private fun submitWorkout() {
        if (changesMade) {
            val dialog = SubmitDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "SubmitDialog")
        } else {
            mHistoryDatabase.child(mWorkout.workoutId!!).setValue(mWorkout)
            val intent = Intent(this, Welcome::class.java)
            intent.putExtra(USER_ID, uid)
            startActivity(intent)
        }
    }

    /*Inherited function from the SubmitWorkoutListener that allows the user to save their customized
    workout as a copy in the database. The copy will be given a new Workout Id so that it can be kept
    separate in the database.
     */
    override fun onSubmitCopy() {
        val id = mUserDatabase.push().key
        val workout : Workout = Workout(id!!, mWorkout.workoutName!!, mWorkout.workoutExercises)
        mUserDatabase.child(id!!).setValue(workout)
        mHistoryDatabase.child(id!!).setValue(workout)
        val intent = Intent(this, Welcome::class.java)
        intent.putExtra(USER_ID, uid)
        startActivity(intent)
    }

    companion object {
        const val TAG = "Mine-WorkoutExercisesActivity:"
        const val WORKOUT = "WORKOUT"
        const val WORKOUT_TYPE = "WORKOUT_TYPE"
        const val USER_ID = "USER_ID"
    }
}