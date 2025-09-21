package com.example.recipebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.local.categoryLocal.CategoryEntity
import com.example.recipebook.data.local.recipeLocal.RecipeDao
import com.example.recipebook.data.local.recipeLocal.RecipeEntity
import com.example.recipebook.data.util.Converters

@Database( entities = [RecipeEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun categoryDao(): CategoryDao
}