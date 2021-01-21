package com.example.healthimprovementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout
import com.example.healthimprovementapp.com.example.healthimprovementapp.WorkoutExercisesActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.user_history.*
import java.lang.Exception

/*Displays a list of all the workouts the user has submitted. Clicking on a workout in the list will
allow send the user to the WorkoutExercises activity to complete it again.
 */
class  UserHistoryActivity : AppCompatActivity() {

    private lateinit var uid : String
    private var mHistoryList : ListView? = null
    private var mListAdapter : WorkoutListAdapter? = null
    private var mReturnButton : Button? = null
    private lateinit var workouts : MutableMap<Workout, String>
    private lateinit var mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_history)

        uid = intent.getStringExtra(USER_ID)
        if (uid == null) {
            Log.i(TAG, "User not received from intent")
            finish()
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("user-history").child(uid)

        mHistoryList = findViewById(R.id.historyList)
        mListAdapter = WorkoutListAdapter(this)
        mHistoryList!!.adapter = mListAdapter

        mReturnButton = findViewById(R.id.returnButton)

    }

    override fun onStart() {
        super.onStart()

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {Log.i(TAG, "No data received")}
                workouts = mutableMapOf()
                var workout : Workout? = null

                for (workoutTypeData in snapshot.children) {
                    val workoutType = workoutTypeData.key

                    for (workoutData in workoutTypeData.children) {
                        try {
                            workout = Workout(workoutData.value as Map<String, Object>)
                        } catch (e :Exception) {
                            Log.i(TAG, e.toString())
                        } finally {
                            workouts!![workout!!] = workoutType!!
                        }
                    }
                }

                val newAdapter = WorkoutListAdapter(this@UserHistoryActivity)
                newAdapter.addAll(workouts.keys.toList())
                mListAdapter = newAdapter
                historyList.adapter = newAdapter
            }

        })

        mReturnButton!!.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            intent.putExtra(USER_ID, uid)
            startActivity(intent)
        }

        //Starts the WorkoutExercisesActivity so that the user can complete the selected workout again
        mHistoryList!!.setOnItemClickListener { adapterView, view, i, l ->
            val workout = mListAdapter!!.getItem(i)
            val intent = Intent(this, WorkoutExercisesActivity::class.java)
            intent.putExtra(WORKOUT_TYPE, workouts!![workout])
            intent.putExtra(WORKOUT, workout)
            intent.putExtra(USER_ID, uid)
            startActivity(intent)
        }
    }
    companion object {
        const val TAG = "Mine-UserHistory"
        const val USER_ID = "USER_ID"
        const val WORKOUT = "WORKOUT"
        const val WORKOUT_TYPE = "WORKOUT_TYPE"
    }
}