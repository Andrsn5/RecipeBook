package com.example.recipebook.data.local.recipeLocal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE category = '' ORDER BY rowid DESC")
    fun getAll(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<RecipeEntity?>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Int): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun search(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavorites(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY rowid DESC")
    fun getByCategory(category: String): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("""
        DELETE FROM recipes 
        WHERE category = '' 
        AND isFavorite = 0 
        AND rowid NOT IN (
            SELECT rowid FROM recipes 
            WHERE category = '' AND isFavorite = 0 
            ORDER BY rowid DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldNonFavorites(keepCount: Int)

    @Query("SELECT COUNT(*) FROM recipes WHERE category = ''")
    suspend fun getHomeRecipesCount(): Int

    @Query("""
        DELETE FROM recipes 
        WHERE category = :category 
        AND isFavorite = 0 
        AND rowid NOT IN (
            SELECT rowid FROM recipes 
            WHERE category = :category AND isFavorite = 0 
            ORDER BY rowid DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldNonFavoritesForCategory(category: String, keepCount: Int)
}