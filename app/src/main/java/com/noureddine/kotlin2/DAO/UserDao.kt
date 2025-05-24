package com.noureddine.kotlin2.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noureddine.kotlin2.model.User_db
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.utel.AppConstants.TABEL_USER

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT id FROM ${TABEL_USER} WHERE uid = :uid LIMIT 1")
    suspend fun getUserIdByUid(uid: String): Long?

//    @Query("SELECT * FROM ${TABEL_USER}")
//    fun getAllUsers(): LiveData<List<User>>

    @Query("""SELECT users.id, users.uid, users.name, users.img, users.notificationId, users.email, MAX(message.timestamp) AS messageTimestamp, message.text AS lastMessageText
FROM users 
JOIN participant ON users.id = participant.user_id
JOIN conversation ON conversation.id = participant.conversation_id
JOIN message ON conversation.id = message.conversation_id
GROUP BY users.id, users.uid, users.name, users.img, users.notificationId, users.email
ORDER BY messageTimestamp DESC
""")
    fun getAllUsers(): LiveData<List<User_db>>

    @Query("DELETE FROM ${TABEL_USER}")
    suspend fun clearUsers()

}