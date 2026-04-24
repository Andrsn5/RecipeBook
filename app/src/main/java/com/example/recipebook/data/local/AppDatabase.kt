package com.example.recipebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.local.categoryLocal.CategoryEntity
import com.example.recipebook.data.local.recipeLocal.RecipeDao
import com.example.recipebook.data.local.recipeLocal.RecipeEntity
import com.example.recipebook.data.util.Converters

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE recipes ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}

@Database( entities = [RecipeEntity::class, CategoryEntity::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun categoryDao(): CategoryDao

}