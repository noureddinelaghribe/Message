package com.noureddine.kotlin2.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.noureddine.kotlin2.DAO.MessageDao
import com.noureddine.kotlin2.Database.AppDatabase
import com.noureddine.kotlin2.model.User_db
import com.noureddine.kotlin2.model.Conversation_db
import com.noureddine.kotlin2.model.Message_db
import com.noureddine.kotlin2.model.Participant_db
import com.noureddine.kotlin2.Repository.MyRepository
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.Message
import com.noureddine.kotlin2.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(application: Application): AndroidViewModel(application) {

    private val repository: MyRepository
    val allUsers: LiveData<List<User_db>>
    //val allConversations: LiveData<List<Conversation_db>>
    //val allConversations: LiveData<List<Message_db>>
    //val allConversations: LiveData<List<Participant_db>>

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val conversationDao = AppDatabase.getDatabase(application).conversationDao()
        val participantDao = AppDatabase.getDatabase(application).participantDao()
        val messageDao = AppDatabase.getDatabase(application).messageDao()
        repository = MyRepository(userDao, conversationDao, participantDao, messageDao)
        allUsers = repository.getAllUsers()
        //allConversations = repository.getAllConversations()
    }

    fun insertUser(user: User) = viewModelScope.launch (Dispatchers.IO){ repository.insertUser(user) }

    suspend fun getUserIdByUid(uid: String): Long? {
        return withContext(Dispatchers.IO) {
            repository.getUserIdByUid(uid)
        }
    }

    fun clearUsers() = viewModelScope.launch (Dispatchers.IO){ repository.clearUsers()}

//    fun insertConversation(conversation: Conversation_db) : Long{
//        viewModelScope.launch (Dispatchers.IO){
//            return repository.insertConversation(conversation)
//        }
//    }

    // في ViewModel
    suspend fun insertConversation(conversation: Conversation_db): Long {
        return repository.insertConversation(conversation)
    }


    suspend fun getConversationIdByUid(uid: String): Long? {
        return withContext(Dispatchers.IO) {
            repository.getConversationIdByUid(uid)
        }
    }

//    suspend fun getConversationIdByUserId(uid: String): Long? {
//        return withContext(Dispatchers.IO) {
//            repository.getConversationIdByUserId(uid)
//        }
//    }

    fun clearConversations() = viewModelScope.launch (Dispatchers.IO){ repository.clearConversations()}

    fun insertParticipant(participant: Participant_db) = viewModelScope.launch (Dispatchers.IO){ repository.insertParticipant(participant) }

    fun clearParticipants() = viewModelScope.launch (Dispatchers.IO){ repository.clearParticipants()}

    fun insertMessage(message: Message_db) = viewModelScope.launch (Dispatchers.IO){ repository.insertMessage(message) }

    suspend fun getAllMessagesByUserId(userId: Long): List<Message> = repository.getAllMessagesByUserId(userId)

    fun clearMessages() = viewModelScope.launch (Dispatchers.IO){ repository.clearMessages()}

}