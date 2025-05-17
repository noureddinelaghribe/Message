package com.noureddine.kotlin2.Model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
//class Notification: Parcelable{
//    var conversation: Conversation
//    var user: User
//
//    constructor(conversation: Conversation, user: User){
//        this.conversation = conversation
//        this.user = user
//    }
//
//    override fun describeContents(): Int {
//    }
//
//    override fun writeToParcel(p0: Parcel, p1: Int) {
//    }
//
//}

@androidx.annotation.Keep
class Notification(
    var conversation: Conversation,
    var user: User
) : Parcelable {

    // 1. Reading back in the same order you write
    private constructor(parcel: Parcel) : this(
        parcel.readParcelable(Conversation::class.java.classLoader)!!,
        parcel.readParcelable(User::class.java.classLoader)!!
    )

    // 2. No special objects → always 0
    override fun describeContents(): Int {
        return 0
    }

    // 3. Write each field in order
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(conversation, flags)  // write nested Parcelable :contentReference[oaicite:0]{index=0}
        dest.writeParcelable(user, flags)
    }

    // 4. CREATOR to generate instances from a Parcel
    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)            // invokes the private ctor :contentReference[oaicite:1]{index=1}
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}

