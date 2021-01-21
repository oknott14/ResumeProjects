package com.example.healthimprovementapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout

class WorkoutListAdapter(private val context : Context) : BaseAdapter() {

    private val workouts : ArrayList<Workout> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val workout = workouts[position]
        val viewHolder : ViewHolder

        if (convertView == null) {

            viewHolder = ViewHolder()
            val mLayoutInflater = LayoutInflater.from(context)
            val newView = mLayoutInflater.inflate(R.layout.workout_list, parent, false)

            newView.tag = viewHolder
            viewHolder.mItemLayout = newView.findViewById(R.id.workoutList)
            viewHolder.mTitleView = newView.findViewById(R.id.workoutName)
            viewHolder.mExercisesView = newView.findViewById(R.id.workoutExercises)

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //fill in default data for workouts
        viewHolder.mTitleView!!.text = workout.workoutName
        viewHolder.mExercisesView!!.text = exerciseString(workout)

        return viewHolder.mItemLayout!!
    }

    //Returns a formatted string of exercises and their values to display in the list's view
    private fun exerciseString(workout : Workout) : String {
        val roughString = workout.workoutExercises.toString() //returns [ s1, s2, s3, ..., sn]
        return roughString.substring(1, roughString.length-1)
    }

    //Adds a workout to the list and updates the ListView
    fun add(workout : Workout) {
        Log.i(TAG, "Adding Workout to adapter")
        workouts.add(workout)
        notifyDataSetChanged()
    }

    //Adds all workouts to the list and updates the ListView
    fun addAll(inWorkouts : List<Workout>) {
        workouts.addAll(inWorkouts)
        notifyDataSetChanged()
    }

    //Returns the item at the index position in the list
    override fun getItem(position: Int): Workout {
        return workouts[position]
    }

    //Returns the item's position as a long
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //returns the number of workouts in the current list
    override fun getCount(): Int {
        return workouts.size
    }

    internal class ViewHolder {
        var mItemLayout : RelativeLayout? = null
        var mTitleView : TextView? = null
        var mExercisesView : TextView? = null
    }

    companion object {
        val TAG = "Mine-WorkoutListAdapter"
    }
}