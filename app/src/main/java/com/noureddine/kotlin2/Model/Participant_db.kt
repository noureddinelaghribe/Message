package com.noureddine.kotlin2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noureddine.kotlin2.utel.AppConstants.TABEL_PARTICIPANT


@Entity(tableName = TABEL_PARTICIPANT)
class Participant_db {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var conversation_id: Long = 0
    var user_id: Long = 0

    constructor()

    constructor(conversation_id: Long, user_id: Long): this(){
        this.conversation_id = conversation_id
        this.user_id = user_id
    }

}