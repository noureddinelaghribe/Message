package com.noureddine.kotlin2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.noureddine.kotlin2.utel.AppConstants.TABEL_CONVERSATION
import com.noureddine.kotlin2.utel.Converters
import android.os.Parcel
import android.os.Parcelable

//@Entity(tableName = TABEL_CONVERSATION)
//@TypeConverters(Converters::class)
//@androidx.annotation.Keep
class Conversation : Parcelable {
    //@PrimaryKey(autoGenerate = true)
    //var id: Int = 0

    var conversationId: String = ""
    var participants: List<String> = emptyList()
    var lastMessage: String = ""
    var lastMessageTimestamp: Long = 0L
    var notify: Boolean = true
    var messages: Map<String, Message> = emptyMap()

    // Default constructor required by Room
    constructor()

    // Parcelable constructor
    constructor(parcel: Parcel) : this() {
        conversationId = parcel.readString() ?: ""
        participants = parcel.createStringArrayList()?.toList() ?: emptyList()
        lastMessage = parcel.readString() ?: ""
        lastMessageTimestamp = parcel.readLong()
        notify = parcel.readByte() != 0.toByte()
        @Suppress("UNCHECKED_CAST")
        messages = parcel.readHashMap(Message::class.java.classLoader) as Map<String, Message>
    }

    // Constructor for normal creation (without ID)
    constructor(
        conversationId: String,
        participants: List<String>,
        lastMessage: String,
        lastMessageTimestamp: Long,
        notify: Boolean,
        messages: Map<String, Message>
    ) : this() {
        this.conversationId = conversationId
        this.participants = participants
        this.lastMessage = lastMessage
        this.lastMessageTimestamp = lastMessageTimestamp
        this.notify = notify
        this.messages = messages
    }

    constructor(
        conversationId: String,
        participants: List<String>,
        lastMessage: String,
        lastMessageTimestamp: Long
    ) : this(
        conversationId = conversationId,
        participants = participants,
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
        notify = true, // قيمة افتراضية
        messages = emptyMap() // قيمة افتراضية
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(conversationId)
        parcel.writeStringList(participants)
        parcel.writeString(lastMessage)
        parcel.writeLong(lastMessageTimestamp)
        parcel.writeByte(if (notify) 1 else 0)
        parcel.writeMap(messages)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Conversation> {
        override fun createFromParcel(parcel: Parcel): Conversation {
            return Conversation(parcel)
        }

        override fun newArray(size: Int): Array<Conversation?> {
            return arrayOfNulls(size)
        }
    }
}