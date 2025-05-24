package com.noureddine.kotlin2.model

import android.os.Parcel
import android.os.Parcelable

class User_db() : Parcelable {

    var id: Long = 0
    var uid: String = ""
    var name: String = ""
    var img: Long = 0
    var notificationId: Int = 0
    var email: String = ""
    var messageTimestamp: Long = 0
    var lastMessageText: String = ""

    constructor(
        id: Long,
        uid: String,
        name: String,
        img: Long,
        notificationId: Int,
        email: String,
        messageTimestamp: Long,
        lastMessageText: String
    ) : this() {
        this.id = id
        this.uid = uid
        this.name = name
        this.img = img
        this.notificationId = notificationId
        this.email = email
        this.messageTimestamp = messageTimestamp
        this.lastMessageText = lastMessageText
    }

    // 1) مُنشِئ خاص لقراءة البيانات من Parcel
    private constructor(parcel: Parcel) : this() {
        id               = parcel.readLong()
        uid              = parcel.readString().orEmpty()
        name             = parcel.readString().orEmpty()
        img              = parcel.readLong()
        notificationId   = parcel.readInt()
        email            = parcel.readString().orEmpty()
        messageTimestamp = parcel.readLong()
        lastMessageText  = parcel.readString().orEmpty()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeLong(img)
        parcel.writeInt(notificationId)
        parcel.writeString(email)
        parcel.writeLong(messageTimestamp)
        parcel.writeString(lastMessageText)
    }

    override fun describeContents(): Int = 0

    // 3) CREATOR لإنشاء النسخ من Parcel
    companion object CREATOR : Parcelable.Creator<User_db> {
        override fun createFromParcel(parcel: Parcel): User_db = User_db(parcel)
        override fun newArray(size: Int): Array<User_db?> = arrayOfNulls(size)
    }
}
