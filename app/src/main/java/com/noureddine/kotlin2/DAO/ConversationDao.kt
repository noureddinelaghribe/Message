package com.noureddine.kotlin2.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.Conversation_db
import com.noureddine.kotlin2.utel.AppConstants.TABEL_CONVERSATION
import com.noureddine.kotlin2.utel.AppConstants.TABEL_PARTICIPANT
import com.noureddine.kotlin2.utel.AppConstants.TABEL_USER

@Dao
interface ConversationDao {

    @Insert
    suspend fun insertConversation(conversation: Conversation_db): Long

    @Query("SELECT id FROM ${TABEL_CONVERSATION} WHERE uid = :uid LIMIT 1")
    suspend fun getConversationIdByUid(uid: String): Long?

//    @Query("SELECT conversation_id FROM ${TABEL_PARTICIPANT} WHERE user_id = :uid ORDER BY timestamp DESC LIMIT 1")
//    suspend fun getConversationIdByUserId(uid: String): Long?

//    @Dao
//    interface ParticipantDao {

//    }



    @Query("DELETE FROM ${TABEL_CONVERSATION}")
    suspend fun clearConversations()

}