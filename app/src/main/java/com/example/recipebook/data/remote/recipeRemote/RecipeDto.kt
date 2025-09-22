package com.example.recipebook.data.remote.recipeRemote


import com.google.gson.annotations.SerializedName


data class RecipeDto(
    val id: Int,
    val title: String? = null,
    @SerializedName("image")
    val imageUrl: String? = null,
    val summary: String? = null,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<IngredientDto> = emptyList()
)


data class IngredientDto(
    val name: String? = null,
    @SerializedName("image")
    val imageUrl: String? = null
)