package com.example.healthimprovementapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

/*Creates a dialog that allows users to edit the fields associated with the exercise at the passed
position, and with the name passed as well. If any edit text fields are left un-filled the dialog will
not edit any of the fields. The same will happen if the user chooses the Cancel option from the dialog's
negative button
 */
class EditExerciseDialogFragment(val position : Int, val name : String) : DialogFragment() {

    internal lateinit var listener : EditListener

    //Interface for receiving data from the dialog
    interface EditListener {
        fun onDialogPositiveClick(pos : Int, name : String, sets : Int, reps : Int, weight : Int)
        fun onDialogNegativeClick()
    }

    /*Attaches the dialog listener to the calling activity, throwing an exception if the activity
    does not extend the EditListener interface
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        //verifies the host activity implements EditListener
        try {
            //instantiate listener
            listener = context as EditListener
        } catch (e : ClassCastException) {
            Log.i(TAG, "The calling class does not implement the EditListener interface")
        }
    }

    /*Creates and returns the dialog so that it can be shown in the calling activity*/
    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.edit_exercise_fragment, null)
            val setsView = view.findViewById<EditText>(R.id.numSets)
            val repsView = view.findViewById<EditText>(R.id.numReps)
            val weightView = view.findViewById<EditText>(R.id.weight)

            builder.setView(view)
                .setNegativeButton(R.string.cancel) { _,_ ->
                    listener.onDialogNegativeClick()
                }
                .setTitle(name)

                .setPositiveButton("Submit Edits") { p0, p1 ->
                    val sets = setsView.text.toString()
                    val reps = repsView.text.toString()
                    val weight = weightView.text.toString()

                    if (sets != "" && reps != "" && weight != "") {
                        listener.onDialogPositiveClick(position, name, sets.toInt(), reps.toInt(), weight.toInt())
                    } else {
                        Toast.makeText(context, "Please fill in empty fields!", Toast.LENGTH_SHORT).show()
                    }
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {

        //Created a new instance of the EditExerciseDialog
        fun newInstance(position : Int, name : String) : EditExerciseDialogFragment {
            return EditExerciseDialogFragment(position, name)
        }

        const val TAG = "Mine-EditExerciseDialog:"
    }
}