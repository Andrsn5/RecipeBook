package com.example.recipebook.di

import android.content.Context
import com.example.recipebook.data.local.categoryLocal.CategoryDao
import com.example.recipebook.data.local.recipeLocal.RecipeDao
import com.example.recipebook.data.remote.recipeRemote.RecipeApi
import com.example.recipebook.data.repository.CategoryRepositoryImpl
import com.example.recipebook.data.repository.RecipeRepositoryImpl
import com.example.recipebook.domain.repository.CategoryRepository
import com.example.recipebook.domain.repository.RecipeRepository
import com.example.recipebook.presentation.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context).apply {
            register() // Регистрируем здесь
        }
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        api: RecipeApi,
        dao: RecipeDao,
        networkMonitor: NetworkMonitor
    ): RecipeRepository {
        return RecipeRepositoryImpl(api, dao, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        dao: CategoryDao,
    ): CategoryRepository {
        return CategoryRepositoryImpl(dao)
    }
}