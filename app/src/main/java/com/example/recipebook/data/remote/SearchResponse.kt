package com.example.recipebook.data.remote

import com.example.recipebook.data.remote.recipeRemote.RecipeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("results")
    val results: List<RecipeDto>,
    @SerialName("totalResults")
    val totalResults: Int
)