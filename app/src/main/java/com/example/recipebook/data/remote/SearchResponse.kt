package com.example.recipebook.data.remote

import com.example.recipebook.data.remote.recipeRemote.RecipeDto
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results")
    val results: List<RecipeDto>,
    @SerializedName("totalResults")
    val totalResults: Int
)