package com.example.healthimprovementapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.healthimprovementapp.com.example.healthimprovementapp.Exercise


/*List adapter for exercises. Shows the exercise name and its values in a custom list view. Includes
supporting methods for editing the list
 */
class ExerciseListAdapter(private val context : Context) : BaseAdapter() {

    private val exercises : ArrayList<Exercise> = ArrayList()

    //returns the view for the exercise at the given position
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val exercise = exercises[position]
        val viewHolder : ViewHolder

        if (convertView == null) {

            viewHolder = ViewHolder()
            val mLayoutInflater = LayoutInflater.from(context)
            val newView = mLayoutInflater.inflate(R.layout.exercise_list, parent, false)

            newView.tag = viewHolder
            viewHolder.mItemLayout = newView.findViewById(R.id.exerciseList)
            viewHolder.mTitleView = newView.findViewById(R.id.exerciseName)
            viewHolder.mSetsView = newView.findViewById(R.id.setsView)
            viewHolder.mRepsView = newView.findViewById(R.id.repsView)
            viewHolder.mWeightView = newView.findViewById(R.id.weightView)

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //fill in default data in the EditTexts as hints for reps, weight, sets.
        viewHolder.mTitleView!!.text = exercise.exerciseName
        viewHolder.mWeightView!!.text = exercise.weight.toString()
        viewHolder.mSetsView!!.text = exercise.numSets.toString()
        viewHolder.mRepsView!!.text = exercise.numReps.toString()

        return viewHolder.mItemLayout!!
    }

    //returns the exercise at the index position in the list
    override fun getItem(position: Int): Any {
        return exercises[position]
    }

    //returns the items position as a long
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //returns the total number of exercises in the current list
    override fun getCount(): Int {
        return exercises.size
    }

    //adds an exercise to the list and alters the list view
    fun add(exercise : Exercise) {
        Log.i(TAG, "Adding exercise to adapter")
        exercises.add(exercise)
        notifyDataSetChanged()
    }

    //adds all exercises to the list and alters the list view
    fun addAll(inExercises : List<Exercise>) {
        exercises.addAll(inExercises)
        notifyDataSetChanged()
    }

    //Edits the exercise at index pos with the parameters passed into the method
    fun editExerciseAt(pos : Int, name : String, sets : Int, reps : Int, weight : Int) {
        exercises[pos] = Exercise(name, sets, reps, weight)
        notifyDataSetChanged()
    }

    //removes the element at position from the list
    fun removeAt(position: Int) {
        exercises.removeAt(position)
        notifyDataSetChanged()
    }

    internal class ViewHolder {
        var mItemLayout : RelativeLayout? = null
        var mTitleView : TextView? = null
        var mSetsView : TextView? = null
        var mRepsView : TextView? = null
        var mWeightView : TextView? = null
    }

    companion object {
        const val TAG = "Mine-ExerciseListAdapter:"
    }
}