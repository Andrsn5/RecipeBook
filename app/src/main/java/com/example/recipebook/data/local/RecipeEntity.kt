package com.example.recipebook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val category: String,
    val description: String,
    val ingredients: String,
    val isFavorite: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis()
)