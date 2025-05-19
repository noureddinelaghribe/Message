package com.noureddine.kotlin2.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.noureddine.kotlin2.Database.AppDatabase
import com.noureddine.kotlin2.Repository.MyRepository
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application): AndroidViewModel(application) {

    private val repository: MyRepository
    val allUsers: LiveData<List<User>>
    val allConversations: LiveData<List<Conversation>>

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val conversationDao = AppDatabase.getDatabase(application).conversationDao()
        repository = MyRepository(userDao,conversationDao)
        allUsers = repository.getAllUsers()
        allConversations = repository.getAllConversations()
    }

    fun insertUser(user: User) = viewModelScope.launch (Dispatchers.IO){ repository.insertUser(user) }

    fun insertConversation(conversation: Conversation) = viewModelScope.launch (Dispatchers.IO){ repository.insertConversation(conversation) }

}