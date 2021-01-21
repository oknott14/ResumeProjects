package com.example.healthimprovementapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import java.lang.Exception

/*Creates and returns a dialog prompting the user to decide how they want to submit their altered
workout
 */
class SubmitDialogFragment : DialogFragment() {

    private lateinit var listener : SubmitListener

    //Interface for receiving data from the dialog
    interface SubmitListener {
        fun onSubmitCopy()
    }

    /*Attaches the listener to the calling activity, throwing an error if the activity does not extend
    the SubmitListener interface
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as SubmitListener
        } catch(e : Exception) {
            Log.i(TAG, e.toString())
        }
    }

    /*Creates and returns a dialog to the calling activity to be shown there*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setIcon(R.drawable.logo)
                .setMessage("Changes were made to this workout. Would you like to" +
            " save them?")
                .setPositiveButton("Yes") { _,_ ->
                    listener.onSubmitCopy()
                }

                .setNegativeButton("No") {_,_ ->}
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {

        //returns a new instance of the SubmitDialogFragment
        fun newInstance() : SubmitDialogFragment {
            return SubmitDialogFragment()
        }
        const val TAG = "Mine-SubmitDialogFragment:"
    }
}