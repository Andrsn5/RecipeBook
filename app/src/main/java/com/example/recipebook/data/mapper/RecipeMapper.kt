package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.recipeLocal.RecipeEntity
import com.example.recipebook.data.remote.recipeRemote.IngredientDto
import com.example.recipebook.data.remote.recipeRemote.RecipeDto
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.presentation.model.IngredientUi

object RecipeMapper {

    fun dtoToEntity(dto: RecipeDto): RecipeEntity =
        RecipeEntity(
            id = dto.id,
            title = dto.title ?: "No Title",
            imageUrl = dto.imageUrl ?: "",
            summary = dto.summary ?: "No description",
            ingredients = dto.extendedIngredients,
            isFavorite = false
        )

    fun entityToDomain(entity: RecipeEntity): Recipe =
        Recipe(
            id = entity.id,
            name = entity.title,
            summary = entity.summary ?: "No description",
            imageUrl = entity.imageUrl ?: "",
            ingredients = entity.ingredients.map { ingredientDtoToUi(it) },
            favourite = entity.isFavorite
        )

    fun ingredientDtoToUi(dto: IngredientDto): IngredientUi =
        IngredientUi(
            name = dto.name ?: "Unknown",
            imageUrl = dto.imageUrl?.let { 
                if (it.startsWith("http")) it else null
            }
        )

    fun entityListToDomain(list: List<RecipeEntity>): List<Recipe> =
        list.map { entityToDomain(it) }
}