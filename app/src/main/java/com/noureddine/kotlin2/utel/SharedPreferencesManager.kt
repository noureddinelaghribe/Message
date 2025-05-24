package com.noureddine.kotlin2.utel

import android.content.Context
import android.content.SharedPreferences
import com.noureddine.kotlin2.model.User

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_UID = "uid"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_IMAGE = "image"
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_UID, user.uid)
        editor.putString(KEY_NAME, user.name)
        editor.putString(KEY_EMAIL, user.email)
        editor.putLong(KEY_IMAGE, user.img)
        editor.apply()
    }

    fun getUser(): User {
        return User(
            uid = sharedPreferences.getString(KEY_UID, "") ?: "",
            name = sharedPreferences.getString(KEY_NAME, "") ?: "",
            email = sharedPreferences.getString(KEY_EMAIL, "") ?: "",
            img = sharedPreferences.getLong(KEY_IMAGE, 0)
        )
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}