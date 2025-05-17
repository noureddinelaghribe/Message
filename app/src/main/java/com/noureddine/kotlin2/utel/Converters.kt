package com.noureddine.kotlin2.utel

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noureddine.kotlin2.model.Message

class Converters {
    // For Map<String, Message>
    @TypeConverter
    fun fromMessagesMap(messages: Map<String, Message>): String {
        return Gson().toJson(messages)
    }

    @TypeConverter
    fun toMessagesMap(json: String): Map<String, Message> {
        val type = object : TypeToken<Map<String, Message>>() {}.type
        return Gson().fromJson(json, type) ?: emptyMap()
    }

    // For List<String>
    @TypeConverter
    fun fromParticipantsList(participants: List<String>): String {
        return Gson().toJson(participants)
    }

    @TypeConverter
    fun toParticipantsList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromString(value: String?): Map<String, String> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String {
        if (map == null) return "{}"
        return Gson().toJson(map)
    }
}