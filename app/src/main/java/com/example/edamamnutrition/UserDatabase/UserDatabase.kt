package com.example.edamamnutrition.UserDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    //PROVIDES ACCESS TO THE DAO INSIDE THE DATABASE
    abstract fun userDao(): UserDao

    //WE ONLY WANT ONE INSTANCE OF THE DATABASE IN THE APPLICATION
    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null;

        //FUNCTION TO RETRIEVE AN INSTANCE OF THE DATABASE
        //CHECKS FIRST IF AN INSTANCE EXISTS, IF NOT RETURNS NEW INSTANCE
        fun getDatabase(context: Context): UserDatabase {
            val tempInstance = INSTANCE;
            if(tempInstance != null) {

                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                //RETURNS NEW INSTANCE
                INSTANCE = instance
                return instance
            }
        }
    }

}