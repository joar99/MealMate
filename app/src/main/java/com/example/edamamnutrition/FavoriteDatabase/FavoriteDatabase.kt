package com.example.edamamnutrition.FavoriteDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.edamamnutrition.UserDatabase.User
import com.example.edamamnutrition.UserDatabase.UserDatabase

//CODE HERE IS NEARLY IDENTICAL TO ONE SEEN IN USERDATABASE, SEE USERDATABASE/USERDATABASE FOR FURTHER COMMENTS
@Database(entities = [Favorite::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null
        //REPORT 79
        fun getDatabase(context: Context): FavoriteDatabase {
            val tempInstance = FavoriteDatabase.INSTANCE;
            if(tempInstance != null) {

                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite_database"
                ).build()
                //RETURNS NEW INSTANCE
                FavoriteDatabase.INSTANCE = instance
                return instance
            }

        }
    }

}