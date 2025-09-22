package com.example.recipebook.domain.model

import com.example.recipebook.data.remote.recipeRemote.IngredientDto

data class Recipe(val id: Int,
                  val name: String,
                  val description: String,
                  val imageUrl: String?,
                  val ingredients: List<IngredientDto> = emptyList(),
                  val favourite: Boolean)
