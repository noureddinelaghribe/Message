package com.noureddine.kotlin2.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.Participant_db
import com.noureddine.kotlin2.utel.AppConstants.TABEL_PARTICIPANT
import com.noureddine.kotlin2.utel.AppConstants.TABEL_USER

@Dao
interface ParticipantDao {

    @Insert
    suspend fun insertParticipant(participant: Participant_db)

    @Query("DELETE FROM ${TABEL_PARTICIPANT}")
    suspend fun clearParticipants()

}