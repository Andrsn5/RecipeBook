package com.example.recipebook.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {
    @GET("recipes")
    suspend fun getAll(): List<RecipeDto>

    @GET("recipes/search")
    suspend fun search(@Query("q") query: String): List<RecipeDto>

    @GET("recipes/{id}")
    suspend fun getById(@Path("id") id: String): RecipeDto

}