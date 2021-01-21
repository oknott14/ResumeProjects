package com.example.healthimprovementapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout


/**
 * A simple [Fragment] subclass.
 * Use the [DeleteDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteDialogFragment(val item : Object, val type : String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setMessage("Do you really want to Delete?")

            // User cannot dismiss dialog by hitting back button
            .setCancelable(true)

            // Set up No Button
            .setNegativeButton(
                "No"
            ) { _, _ ->
            }

            // Set up Yes Button
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
               if (type == WORKOUT) {
                   (activity as WorkoutActivity).deleteWorkout(item as Workout)
               } else if (type == EXERCISE) {
                   (activity as AddWorkoutActivity).deleteExercise(item as Int)
               }
            }.create()
    }

    companion object {

        fun newInstance(item : Object, type : String): DeleteDialogFragment {
            return DeleteDialogFragment(item, type)
        }

        val EXERCISE = "EXERCISE"
        val WORKOUT = "WORKOUT"
    }
}
