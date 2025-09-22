package com.example.recipebook.data.remote

import com.example.recipebook.data.remote.recipeRemote.RecipeDto
import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    @SerializedName("recipes")
    val recipes: List<RecipeDto>
)