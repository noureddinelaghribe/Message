package com.noureddine.kotlin2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noureddine.kotlin2.utel.AppConstants.TABEL_MESSAGE

@Entity(tableName = TABEL_MESSAGE)
class Message_db {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var conversation_id: Long = 0
    var user_id: Long = 0
    var text: String = ""
    var timestamp: Long = 0L
    var read: Boolean = false
    var iSend: Boolean = false


    constructor()

    constructor(conversation_id: Long, user_id: Long, text: String, timestamp: Long, read: Boolean, iSend: Boolean): this(){
        this.conversation_id = conversation_id
        this.user_id = user_id
        this.text = text
        this.timestamp = timestamp
        this.read = read
        this.iSend = iSend
    }

}