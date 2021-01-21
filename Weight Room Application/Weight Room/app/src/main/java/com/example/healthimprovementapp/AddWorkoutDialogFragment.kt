package com.example.healthimprovementapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import java.lang.Exception
import java.lang.IllegalStateException

/*Class creates a dialog to make ask what the user wants to do with the clicked workout. The behavior
of this class depends on the clickType parameter passed when the class is created. The user can either
add the workout to their database list or add it to their list and complete the workout immediatley.
 */
class AddWorkoutDialogFragment(val position : Int, val clickType : String) : DialogFragment() {

    internal lateinit var listener : AddWorkoutListener

    //Interface for receiving data from the dialog. All calling classes must extend this interface
    interface AddWorkoutListener {
        fun onAddToWorkoutList(position : Int)
        fun onAddandCompleteWorkout(position : Int)
    }

    /*Attaches the listener to the calling activity, throws an exception if the calling activity
    does not implement the AddWorkoutListener database
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AddWorkoutListener
        } catch (e : Exception) {
            Log.i(TAG, e.toString())
        }
    }

    /*Creates and returns the dialog so it can be shown in the calling activity */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
                .setIcon(R.drawable.logo)

            if (clickType == NORMAL) {
                builder.setMessage("Would you like to add this workout to your list?")
            } else if (clickType == LONG) {
                builder.setMessage("Would you like to add this workout to your list and complete it?")
            }

            builder.setPositiveButton("Yes") { _,_ ->
                if (clickType == NORMAL) {
                    listener.onAddToWorkoutList(position)
                } else if (clickType == LONG) {
                    listener.onAddandCompleteWorkout(position)
                }
            }

                .setNegativeButton("No"){_,_ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    companion object {

        //Creates and returns a new instance of this dialog
        fun newInstance(position : Int, clickType: String) : AddWorkoutDialogFragment {
            return AddWorkoutDialogFragment(position, clickType)
        }

        const val TAG = "Mine-AddWorkoutDialog:"
        const val NORMAL = "NORMAL"
        const val LONG = "LONG"
    }

}