package com.example.recipebook.data.remote.recipeRemote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    @SerialName("image") val imageUrl: String,
    val description: String, // ← не description!
    val extendedIngredients: List<IngredientDto> //
)

@Serializable
data class IngredientDto(
    val name: String,
    @SerialName("image") val imageUrl: String
)
