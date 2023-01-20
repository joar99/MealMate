package com.example.edamamnutrition.UserDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    //Page 2 : Paragraph 5
    //DEFINING CRUD OPERATIONS TOWARDS THE DATABASE
    //ALL OPERATIONS ARE ASYNC AND WILL BE CALLED ON DIFFERENT THREADS AS THEY ARE RESOURCE HEAVY

    //HERE WE HANDLE THE CONFLICT WHEN UPDATING THE USER VALUES BY CALLING ONCONFLICTSTRATEGY
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("Select * FROM user_table WHERE id LIKE :id LIMIT 1")
    suspend fun getUser(id: Int): User

    @Query("UPDATE user_table SET calories = :calories WHERE id = :id")
    suspend fun updateCals(calories: Float, id: Int)

    @Query("UPDATE user_table SET maxSearch = :maxSearch WHERE id = :id")
    suspend fun updateSearch(maxSearch: Int, id: Int)

    @Query("UPDATE user_table SET diet = :diet WHERE id = :id")
    suspend fun updateDiet(diet: String, id: Int)

    @Query("UPDATE user_table SET priority = :priority WHERE id = :id")
    suspend fun updatePriority(priority: String, id: Int)


}