package com.example.recipebook.domain.model

import com.example.recipebook.presentation.model.IngredientUi

data class Recipe(
    val id: Int,
    val name: String,
    val summary: String,
    val imageUrl: String?,
    val ingredients: List<IngredientUi> = emptyList(),
    val favourite: Boolean
)
