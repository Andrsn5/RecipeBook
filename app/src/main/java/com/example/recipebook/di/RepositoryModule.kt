package com.example.recipebook.di

import com.example.recipebook.data.repository.CategoryRepositoryImpl
import com.example.recipebook.data.repository.RecipeRepositoryImpl
import com.example.recipebook.domain.repository.CategoryRepository
import com.example.recipebook.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}