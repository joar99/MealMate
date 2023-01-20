package com.example.edamamnutrition.FavoriteDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//ENTITY FOR FAVORITE (IDENTICAL TO USER SO CHECK /USERDATABASE/USER FOR FURTHER COMMENTS
//Page 2 : Paragraph 4
@Entity(tableName = "favorite_table")
data class Favorite(
    //THE LABEL OF THE RECIPE IS USED AS A PRIMARY KEY, THIS IS FAR FROM OPTIMAL
    //WE LOOKED FOR A LONG TIME INSIDE THE JSON OBJECT FOR A FIELD THAT WAS GUARANTEED TO BE UNIQUE AND HENCE COULD SERVE AS PRIMARY KEY, YET TO NO AVAIL
    //PREFERABLY THE BEST WAY TO GO ABOUT THIS WOULD PERHAPS BE TO COMBINE A FIELD FOUND IN THE JSON OBJECT CALLED "foodID"
    //THIS FIELD IS UNIQUE TO THE INGRIEDIENTS USED IN THAT SPECIFIC DISH, AND THOSE COMBINED WITH LABEL WOULD PERHAPS SERVE THE BEST CHANCE...
    //OF PROVIDING A UNIQUE PRIMARY KEY
    @PrimaryKey
    @ColumnInfo(name = "label")
    var label: String,
    @ColumnInfo(name = "image")
    var image: String,
    @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "calories")
    var calories: Float
)