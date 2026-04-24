package com.example.recipebook.data.remote.recipeRemote

import android.util.Log
import com.example.recipebook.data.remote.RecipeResponse
import com.example.recipebook.data.remote.SearchResponse
import kotlinx.coroutines.delay

class FakeRecipeApi : RecipeApi {

    // Генерируем 100 фейковых рецептов для пагинации
    private val allRecipes = (1..100).map { i ->
        RecipeDto(
            id = i,
            title = "Recipe #$i — ${listOf("Pasta","Salad","Soup","Burger","Pizza","Sushi","Tacos","Steak","Risotto","Curry")[i % 10]}",
            imageUrl = "https://picsum.photos/seed/$i/312/231",
            summary = "Delicious recipe number $i. Easy to make and very tasty!",
            extendedIngredients = listOf(
                IngredientDto("Salt", null),
                IngredientDto("Pepper", null),
                IngredientDto(listOf("Tomato","Chicken","Rice","Pasta","Beef")[i % 5], null)
            )
        )
    }

    private val categories = listOf(
        "Main Course", "Dessert", "Appetizer", "Salad", "Breakfast"
    )

    override suspend fun getRandomRecipes(number: Int, offset: Int): RecipeResponse {
        Log.d("FakeRecipeApi", "getRandomRecipes: number=$number, offset=$offset")
        delay(500) // имитация сети
        val page = allRecipes.drop(offset).take(number)
        Log.d("FakeRecipeApi", "Returning ${page.size} recipes")
        return RecipeResponse(recipes = page)
    }

    override suspend fun searchRecipes(query: String, number: Int): SearchResponse {
        Log.d("FakeRecipeApi", "searchRecipes: query=$query, number=$number")
        delay(300)
        val filtered = allRecipes.filter {
            it.title?.contains(query, ignoreCase = true) == true
        }.take(number)
        Log.d("FakeRecipeApi", "Found ${filtered.size} recipes")
        return SearchResponse(results = filtered, totalResults = filtered.size)
    }

    override suspend fun getRecipesByCategory(type: String, number: Int, offset: Int): SearchResponse {
        Log.d("FakeRecipeApi", "getRecipesByCategory: type=$type, number=$number, offset=$offset")
        delay(500)
        // Каждая категория получает свой диапазон рецептов
        val categoryIndex = categories.indexOf(type).coerceAtLeast(0)
        val categoryRecipes = allRecipes
            .filter { it.id % 5 == categoryIndex }
        val page = categoryRecipes.drop(offset).take(number)
        Log.d("FakeRecipeApi", "Returning ${page.size} recipes for category $type")
        return SearchResponse(results = page, totalResults = categoryRecipes.size)
    }
}