package com.noureddine.kotlin2.Repository

import androidx.lifecycle.LiveData
import com.noureddine.kotlin2.DAO.ConversationDao
import com.noureddine.kotlin2.DAO.UserDao
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.User

class MyRepository(private val userDao: UserDao, private val conversationDao: ConversationDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    fun getAllUsers(): LiveData<List<User>> = userDao.getAllUsers()


    suspend fun insertConversation(conversation: Conversation) {
        return conversationDao.insertConversation(conversation)
    }

    fun getAllConversations(): LiveData<List<Conversation>> = conversationDao.getAllConversations()



}