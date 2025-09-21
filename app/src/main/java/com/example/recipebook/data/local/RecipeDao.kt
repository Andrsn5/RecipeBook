package com.example.recipebook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id ASC")
    fun getAll(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    fun getById(id: String): Flow<RecipeEntity?>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun search(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavorites(): Flow<List<RecipeEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE lastUpdate < :threshold")
    suspend fun deleteOld(threshold: Long)
}