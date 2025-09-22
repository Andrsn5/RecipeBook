package com.example.recipebook.data.local.recipeLocal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipebook.data.remote.recipeRemote.IngredientDto

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String?,
    val summary: String?,
    val ingredients: List<IngredientDto>,
    val isFavorite: Boolean = false
)