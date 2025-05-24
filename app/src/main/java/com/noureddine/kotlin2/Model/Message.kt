package com.noureddine.kotlin2.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class Message : Parcelable {

    var messageId: String = ""

    var senderId: String = ""
    var text: String = ""
    var timestamp: Long = 0L
    var read: Boolean = false

    constructor()

    constructor(messageId: String, senderId: String, text: String, timestamp: Long, read: Boolean) : this() {
        this.messageId = messageId
        this.senderId = senderId
        this.text = text
        this.timestamp = timestamp
        this.read = read
    }

    constructor(senderId: String, text: String, timestamp: Long, read: Boolean) : this() {
        this.senderId = senderId
        this.text = text
        this.timestamp = timestamp
        this.read = read
    }


    constructor(parcel: Parcel) : this() {
        messageId = parcel.readString() ?: ""
        senderId = parcel.readString() ?: ""
        text = parcel.readString() ?: ""
        timestamp = parcel.readLong()
        read = parcel.readByte() != 0.toByte()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeString(senderId)
        parcel.writeString(text)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (read) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}


