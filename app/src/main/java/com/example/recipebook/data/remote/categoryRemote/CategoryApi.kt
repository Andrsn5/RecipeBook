package com.example.recipebook.data.remote.categoryRemote

import com.example.recipebook.data.remote.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryApi {
    @GET("recipes/complexSearch")
    suspend fun getRecipesByCategory(
        @Query("apiKey") apiKey: String,
        @Query("type") category: String,
        @Query("number") number: Int = 20
    ): SearchResponse

    @GET("food/categories")
    suspend fun getCategories(
        @Query("apiKey") apiKey: String
    ): List<CategoryDto>
}