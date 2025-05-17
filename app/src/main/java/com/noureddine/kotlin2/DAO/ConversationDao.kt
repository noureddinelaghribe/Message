package com.noureddine.kotlin2.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.utel.AppConstants.TABEL_CONVERSATION

@Dao
interface ConversationDao {

    @Insert
    suspend fun insertConversation(conversation: Conversation)

    @Query("SELECT * FROM ${TABEL_CONVERSATION}")
    fun getAllConversations(): LiveData<List<Conversation>>

}