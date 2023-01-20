package com.example.edamamnutrition.FavoriteDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//DAO FOR FAVORITE, CODE NEARLY IDENTICAL TO USERDAO SO SEE USERDATABASE/USERDAO FOR FURTHER COMMENTS
//Page 2 : Paragraph 5

@Dao
interface FavoriteDao {

    //HANDLING ONCONFLICTSTRATEGY MAKING SURE ITEM IS NOT SELECTED AS FAVORITE MORE THAN ONCE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite_table LIMIT :limit")
    suspend fun getFavorites(limit: Int) : MutableList<Favorite>

    @Query("SELECT * FROM favorite_table WHERE label LIKE :label LIMIT 1")
    suspend fun getFavorite(label: String): Favorite

    @Query("DELETE FROM favorite_table WHERE label LIKE :label")
    suspend fun deleteFavorite(label: String)

}