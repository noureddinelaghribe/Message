package com.noureddine.kotlin2.Repository

import androidx.lifecycle.LiveData
import com.noureddine.kotlin2.DAO.ConversationDao
import com.noureddine.kotlin2.DAO.MessageDao
import com.noureddine.kotlin2.DAO.ParticipantDao
import com.noureddine.kotlin2.DAO.UserDao
import com.noureddine.kotlin2.model.User_db
import com.noureddine.kotlin2.model.Conversation_db
import com.noureddine.kotlin2.model.Message_db
import com.noureddine.kotlin2.model.Participant_db
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.Message
import com.noureddine.kotlin2.model.User

class MyRepository(private val userDao: UserDao,
                   private val conversationDao: ConversationDao,
                   private val participantDao: ParticipantDao,
                   private val messageDao: MessageDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserIdByUid(uid: String) : Long?{
        return userDao.getUserIdByUid(uid)
    }

    fun getAllUsers(): LiveData<List<User_db>> = userDao.getAllUsers()

    suspend fun clearUsers() = userDao.clearUsers()

    suspend fun insertConversation(conversation: Conversation_db) : Long{
        return conversationDao.insertConversation(conversation)
    }

    suspend fun getConversationIdByUid(uid: String) : Long?{
        return conversationDao.getConversationIdByUid(uid)
    }

//    suspend fun getConversationIdByUserId(uid: String) : Long?{
//        return conversationDao.getConversationIdByUserId(uid)
//    }

    suspend fun clearConversations() = conversationDao.clearConversations()

    suspend fun insertParticipant(participant: Participant_db) {
        return participantDao.insertParticipant(participant)
    }

    suspend fun clearParticipants() = participantDao.clearParticipants()

    suspend fun insertMessage(message: Message_db) {
        return messageDao.insertMessage(message)
    }


    suspend fun getAllMessagesByUserId(userId: Long): List<Message> = messageDao.getAllMessagesByUserId(userId)


    suspend fun clearMessages() = messageDao.clearMessages()

}