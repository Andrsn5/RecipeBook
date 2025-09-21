package com.example.recipebook.data.remote.recipeRemote

data class RecipeDto(
    val id: String,
    val title: String,
    val imageUrl: String,
    val category: String,
    val description: String,
    val ingredients: List<String>,
    val ingredientsImage: List<String>,
    val isFavorite: Boolean = false,
    val lastUpdate: Long = 0
)