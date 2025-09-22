package com.example.recipebook.data.mapper

import com.example.recipebook.data.local.recipeLocal.RecipeEntity
import com.example.recipebook.data.remote.recipeRemote.RecipeDto
import com.example.recipebook.domain.model.Recipe

object RecipeMapper {

    fun dtoToEntity(dto: RecipeDto): RecipeEntity =
        RecipeEntity(
            id = dto.id,
            title = dto.title ?: "No Title",
            imageUrl = dto.imageUrl ?: "", // Обработка null
            summary = dto.summary ?: "No description", // Обработка null
            ingredients = dto.extendedIngredients ?: emptyList(),
            isFavorite = false
        )

    fun entityToDomain(entity: RecipeEntity): Recipe =
        Recipe(
            id = entity.id,
            name = entity.title ?: "No Title",
            summary = entity.summary ?: "No description",
            imageUrl = entity.imageUrl ?: "",
            ingredients = entity.ingredients,
            favourite = entity.isFavorite
        )

    fun entityListToDomain(list: List<RecipeEntity>): List<Recipe> =
        list.map { entityToDomain(it) }

    fun dtoListToEntity(list: List<RecipeDto>): List<RecipeEntity> =
        list.map { dtoToEntity(it) }
}