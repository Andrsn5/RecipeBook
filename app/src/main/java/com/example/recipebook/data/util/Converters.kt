package com.example.recipebook.data.util

import androidx.room.TypeConverter
import com.example.recipebook.data.remote.recipeRemote.IngredientDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromIngredientList(list: List<IngredientDto>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toIngredientList(jsonString: String): List<IngredientDto> {
        return try {
            Json.decodeFromString<List<IngredientDto>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}