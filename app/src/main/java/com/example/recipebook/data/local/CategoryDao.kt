package com.example.recipebook.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY id ASC")
    fun getCategories(): Flow<List<CategoryEntity>>
}