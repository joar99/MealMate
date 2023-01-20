package com.example.edamamnutrition

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.edamamnutrition.UserDatabase.User
import com.example.edamamnutrition.UserDatabase.UserDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class SettingsActivity : AppCompatActivity() {

    private lateinit var userDB: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_xml)
        val userDB = UserDatabase.getDatabase(this)
        val spinnerDiet: Spinner = findViewById(R.id.spinner_diet_type)
        val spinnerMeal: Spinner = findViewById(R.id.spinner_meal_type)

        //HERE WE USE THE CREATE AN ARRAY FROM RESOURCE FILES WE HAVE, THESE ARE ARRAYS OF STRINGS LOCATED IN RES/VALUES/STRINGS.XML
        //Page 2 : Paragraph 6
        val dietAdapter = ArrayAdapter.createFromResource(this, R.array.diettype, android.R.layout.simple_spinner_item)
        val mealAdapter = ArrayAdapter.createFromResource(this, R.array.mealtype, android.R.layout.simple_spinner_item)

        //SETTING THE DEFAULT RESOURCE DISPLAYED TO THE USER
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        //SETTING THE ADAPTER FOR THE SPINNERS
        spinnerDiet.adapter = dietAdapter
        spinnerMeal.adapter = mealAdapter

        val saveBtn: Button = findViewById(R.id.settings_btn_save)
        val calorieText: EditText = findViewById(R.id.settings_et_calories)
        val historyText: EditText = findViewById(R.id.settings_ed_history)
        val backBtn: ImageButton = findViewById(R.id.settings_back_btn)
        backBtn.setOnClickListener {
            finish()
        }

        //SETTING A GLOBAL VARIABLE FOR THE USER
        var currentUser: User? = null
        CoroutineScope(Dispatchers.IO).launch {
            //AT THIS POINT WE KNOW WITH CONFIDENCE THAT THE USER HAS BEEN CREATED, SO WE SIMPLY FETCH IT
            //IT IS AN ASYNC FUNCTION SO WE FETCH IT INSIDE COROUTINES
            currentUser = userDB.userDao().getUser(1)

            withContext(Main) {
                //GETTING THE VALUES OF THE CURRENT USER TO SET AS DEFAULT VALUES ON THE PAGE
                //Page 2 : Paragraph 6
                calorieText.setText(currentUser?.calories.toString())
                historyText.setText(currentUser?.maxSearch.toString())
                var dietIndex = dietAdapter.getPosition(currentUser?.diet)
                spinnerDiet.setSelection(dietIndex)
                var mealIndex = mealAdapter.getPosition(currentUser?.priority)
                spinnerMeal.setSelection(mealIndex)
            }
        }

        //FUNCTION TO SAVE THE UPDATED USER VALUES
        saveBtn.setOnClickListener {

            //GETTING ALL THE VALUES FROM THE INPUT FIELDS
            val calsToParse = calorieText.text.toString()
            val parsedCals = calsToParse.toInt()
            val historyToParse = historyText.text.toString()
            val parsedHistory = historyToParse.toInt()
            val spinnerDietIndex = spinnerDiet.selectedItem.toString()
            val spinnerMealIndex = spinnerMeal.selectedItem.toString()

            //HERE WE SIMPLY SET THE USER, INSIDE THE DAO WE HAVE CONFLICT HANDLING MANAGEMENT
            //SO WE CAN SET THE USER WITH THE SAME ID AS THE PREVIOUS USER
            val updatedUser = User(1, parsedCals, parsedHistory, spinnerDietIndex, spinnerMealIndex)

            CoroutineScope(Dispatchers.IO).launch {
                userDB.userDao().addUser(updatedUser)
            }

            Toast.makeText(this, "Settings Successfully Updated", Toast.LENGTH_LONG).show()

        }

    }

    companion object {
        //GLOBAL FUNCTION WRAPPED INSIDE COMPANION OBJECT
        //THIS IS THE FUNCTION THAT IS CALLED IN THE MAIN ACTIVITY IF A USER DOES NOT EXIST IN THE DATABASE
        fun prePopulate(): User {
            val user = User(1, 2500, 10, "balanced", "Dinner")
            return user
        }
    }

}