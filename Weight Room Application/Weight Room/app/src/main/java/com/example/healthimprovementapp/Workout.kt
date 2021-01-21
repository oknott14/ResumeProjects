package com.example.healthimprovementapp.com.example.healthimprovementapp

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

/*Object containing the values of a workout with constructors for creating a workout from a parcel or
a map, as would be passed from the database.
 */
class Workout(val workoutId: String? = "", val workoutName: String? = "", var workoutExercises: ArrayList<Exercise>) : Parcelable {

    //Creates a workout from a parcel
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        ArrayList<Exercise>().apply{
            parcel.readList(this as List<Exercise>,Exercise::class.java.classLoader)
        }
    )

    //Creates a workout frm a String -> Object map
    constructor(data : Map<String,Object>) : this(
        data.getValue("workoutId") as String,
        data.getValue("workoutName") as String,
        ArrayList<Exercise>()
    ) {
        for (exerciseData in data.getValue("workoutExercises") as ArrayList<Object>) {
            Log.i("Mine", exerciseData.toString())
            val eMap = exerciseData as Map<String, Object>
            val exercise = Exercise(eMap.getValue("exerciseName") as String, (eMap.getValue("numReps") as Long).toInt(),
                (eMap.getValue("numSets") as Long).toInt(), (eMap.getValue("weight") as Long).toInt())
            this.workoutExercises.add(exercise)
        }
    }

    //Writes a workout to a parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(workoutId)
        parcel.writeString(workoutName)
        parcel.writeList(workoutExercises as List<Exercise>)
    }

    //Inherited method, does nothing
    override fun describeContents(): Int {
        return 0
    }

    //Returns a string containing the ID, Name, and number of exercises in the workout
    override fun toString(): String {
        return "Name: $workoutName, ID: $workoutId, with ${workoutExercises.size.toString()} exercises"
    }

    //Overridden equals method for the workout class
    override fun equals(other: Any?): Boolean {
        val inWorkout = other as Workout
        var returnValue : Boolean = (inWorkout.workoutId == this.workoutId &&
                inWorkout.workoutName == this.workoutName)
        if (returnValue) {
            inWorkout.workoutExercises.forEach { exercise ->
                if (!this.workoutExercises.contains(exercise)) {
                    returnValue = false
                }
            }
        }
        return returnValue
    }
    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }
}