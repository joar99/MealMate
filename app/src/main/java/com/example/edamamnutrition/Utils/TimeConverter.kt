package com.example.edamamnutrition.Utils

import java.util.*

class TimeConverter {

    //A GLOBAL FUNCTION TO AVOID CLUTTER IN THE MAINACTIVITY
    //IT SIMPLE CHECKS THE TIME BY CALLING CALENDAR.GETINSTANCE
    //IT RETURNS A STRING THAT IS THEN USED TO QUERY THE DATABASE AT FIRST LOAD
    companion object {
    fun getTime(): String {

        var calendar = Calendar.getInstance()
        var currentTime = calendar.get(Calendar.HOUR_OF_DAY)

        if (currentTime in 0..11) {
            return "Breakfast"
        } else if (currentTime in 12..15) {
            return "Lunch"
        } else if (currentTime in 16..20) {
            return "Dinner"
        } else if (currentTime in 21..23) {
            return "Supper"
        } else {
            return "Supper"
        }

    }
    }

}