package com.example.recipebook.di

import android.content.Context
import androidx.room.Room
import com.example.recipebook.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context,AppDatabase::class.java,"recipe_database").build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase) = database.recipeDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()
}



