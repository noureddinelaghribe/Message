package com.noureddine.kotlin2.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.Message
import com.noureddine.kotlin2.model.Message_db
import com.noureddine.kotlin2.model.User_db
import com.noureddine.kotlin2.utel.AppConstants.TABEL_CONVERSATION
import com.noureddine.kotlin2.utel.AppConstants.TABEL_MESSAGE

@Dao
interface MessageDao {

    @Insert
    suspend fun insertMessage(message: Message_db)

//    @Query("SELECT id FROM ${TABEL_MESSAGE} WHERE uid = :uid LIMIT 1")
//    suspend fun getMessageIdByUid(uid: String): Long?

    @Query("""SELECT message.id as messageId, message.user_id as senderId, message.text, message.timestamp, message.read
FROM conversation
JOIN participant ON participant.conversation_id = conversation.id
JOIN message ON message.conversation_id = conversation.id
WHERE participant.user_id = :userId
ORDER BY message.timestamp DESC""")
    suspend fun getAllMessagesByUserId(userId: Long): List<Message>

    @Query("DELETE FROM ${TABEL_MESSAGE}")
    suspend fun clearMessages()

}