package com.noureddine.kotlin2.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.utel.AppConstants.TABEL_USER

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM ${TABEL_USER}")
    fun getAllUsers(): LiveData<List<User>>

}