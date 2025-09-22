package com.example.recipebook.data.util

import androidx.room.TypeConverter
import com.example.recipebook.data.remote.recipeRemote.IngredientDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    private val type = object : TypeToken<List<IngredientDto>>() {}.type

    @TypeConverter
    fun fromIngredientList(list: List<IngredientDto>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIngredientList(jsonString: String): List<IngredientDto> {
        return try {
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}