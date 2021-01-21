package com.example.healthimprovementapp.com.example.healthimprovementapp

import android.os.Parcel
import android.os.Parcelable

/*Data class containing exercise fields and constructors to create an exercise from a parcel or
String -> Object Map
 */
data class Exercise (val exerciseName: String = "", val numSets: Int, val numReps: Int, val weight: Int) : Parcelable {

    //creates an exercise from a parcel
    constructor(parcel : Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    //creates an exercise from a map
    constructor(data : Map<String, Object>) : this (
        data.getValue("exerciseName") as String,
        data.getValue("numSets") as Int,
        data.getValue("numReps") as Int,
        data.getValue("weight") as Int)

    //Writes an exercise to a parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exerciseName)
        parcel.writeInt(numSets)
        parcel.writeInt(numReps)
        parcel.writeInt(weight)
    }

    //Inherited method, does nothing
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }

    //Returns a string of the exercise name, weight, sets, and reps
    override fun toString(): String {
        return "$exerciseName (W:$weight, S:$numSets, R:$numReps)"
    }
}