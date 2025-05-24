package com.noureddine.kotlin2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noureddine.kotlin2.utel.AppConstants.TABEL_CONVERSATION

@Entity(tableName = TABEL_CONVERSATION)
class Conversation_db {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var uid: String = ""

    constructor()

    constructor(uid: String) : this() {
        this.uid = uid
    }

}