package com.example.recipebook.data.remote.recipeRemote

import com.example.recipebook.data.remote.RecipeResponse
import com.example.recipebook.data.remote.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("number") number: Int = 20
    ): RecipeResponse

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 20
    ): SearchResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") id: String
    ): RecipeDto
}