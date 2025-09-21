package com.example.recipebook.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.encodeToString(
            serializer = list as SerializationStrategy<Nothing>,
            value = TODO()
        )
    }

    @TypeConverter
    fun toList(jsonString: String): List<String> {
        return try {
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}