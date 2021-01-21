package com.example.healthimprovementapp

import android.os.Parcel
import android.os.Parcelable
import com.example.healthimprovementapp.com.example.healthimprovementapp.Workout
/* A data class representing a typical user of the app
 */
class User(val userId : String? = "", val workouts : ArrayList<Workout> = ArrayList()) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        ArrayList<Workout>().apply {
            parcel.readList(this as List<Workout>, Workout::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeList(workouts as List<Workout>)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}