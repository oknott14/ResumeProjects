package com.example.healthimprovementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout
import com.example.healthimprovementapp.com.example.healthimprovementapp.WorkoutExercisesActivity
import com.google.firebase.database.*
import java.lang.Exception

/*Displays a list of all workouts created in the database so that the user can add workouts that other
users have created to use them to
 */
class DiscoverWorkoutsActivity : AppCompatActivity(), AddWorkoutDialogFragment.AddWorkoutListener {

    private lateinit var mDiscoverList : ListView
    private lateinit var mDiscoverListAdapter: WorkoutListAdapter
    private lateinit var mDatabase : DatabaseReference
    private lateinit var mUserDatabase : DatabaseReference
    private lateinit var workouts : MutableMap<Workout, String>
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_workouts)

        mDatabase = FirebaseDatabase.getInstance().getReference("users")
        uid = intent.getStringExtra(USER_ID)
        mUserDatabase = mDatabase.child(uid)

        mDiscoverList = findViewById(R.id.discoverWorkoutList)

    }

    override fun onStart() {
        super.onStart()

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) { Log.i(TAG, "No Data Received") }

                workouts = mutableMapOf()
                var workout : Workout? = null
                var workoutType : String? = null

                for(userData in snapshot.children) {
                    val dataUid = userData.key as String

                    if (dataUid == uid) {
                        continue
                    } else {

                        for (typeData in userData.children) {
                            workoutType = typeData.key

                            for (workoutData in typeData.children) {

                                try {
                                    workout = Workout(workoutData.value as Map<String, Object>)
                                } catch (e : Exception) {
                                    Log.i(TAG, e.toString())
                                } finally {
                                    workouts[workout!!] = workoutType!!
                                }
                            }
                        }
                    }
                }

                val newAdapter = WorkoutListAdapter(this@DiscoverWorkoutsActivity)
                newAdapter.addAll(workouts.keys.toList())
                mDiscoverListAdapter = newAdapter
                mDiscoverList.adapter = mDiscoverListAdapter
            }

        })

        mDiscoverList.setOnItemClickListener { adapterView, view, i, l ->
            val dialog = AddWorkoutDialogFragment.newInstance(i,NORMAL)
            dialog.show(supportFragmentManager, "AddWorkoutDialog")
            true
        }

        mDiscoverList.setOnItemLongClickListener { adapterView, view, i, l ->
            val dialog = AddWorkoutDialogFragment.newInstance(i,LONG)
            dialog.show(supportFragmentManager, "AddWorkoutDialog")
            true
        }
    }

    /*Adds a workout to the users list in the database and starts the WorkoutActivity under the type
    of the selected workout
     */
    override fun onAddToWorkoutList(position: Int) {
        val workout = mDiscoverListAdapter.getItem(position)
        val workoutType = workouts[workout]
        mUserDatabase.child(workoutType!!).child(workout.workoutId!!).setValue(workout)
        val intent = Intent(this, WorkoutActivity::class.java)
        intent.putExtra(USER_ID, uid)
        intent.putExtra(WORKOUT_TYPE, workoutType!!)
        startActivity(intent)

    }

    /*Adds the selected workout to the users database list and launches WorkoutExercisesActivity so
    the user can complete the workout right away.
     */
    override fun onAddandCompleteWorkout(position: Int) {
        val workout = mDiscoverListAdapter.getItem(position)
        val workoutType = workouts[workout]
        mUserDatabase.child(workoutType!!).child(workout.workoutId!!).setValue(workout)
        val intent = Intent(this, WorkoutExercisesActivity::class.java)
        intent.putExtra(WORKOUT, workout)
        intent.putExtra(USER_ID, uid)
        intent.putExtra(WORKOUT_TYPE, workoutType!!)
        startActivity(intent)
    }

    companion object {
        const val TAG = "Mine-DiscoverWorkouts:"
        const val USER_ID = "USER_ID"
        const val WORKOUT = "WORKOUT"
        const val WORKOUT_TYPE = "WORKOUT_TYPE"
        const val NORMAL = "NORMAL"
        const val LONG = "LONG"
    }
}