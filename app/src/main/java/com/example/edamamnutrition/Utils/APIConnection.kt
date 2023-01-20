package com.example.edamamnutrition.Utils

import com.example.edamamnutrition.JSONModels.JSONFood
import com.example.edamamnutrition.JSONModels.Recipe
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

class APIConnection {
    companion object {

        //FUNCTION TO FETCH JSON FROM API
        //Page 1 : Paragraph 3
        fun fetchJSON(url: String): JSONFood? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            //Page 1 : Paragraph 4
            val call = client.newCall(request)
            val response: Response = call.execute()
            val responseBody = response.body?.string()

            //USING THE GSON LIBRARY TO PARSE THE JSON RETURN VALUE INTO A MATCHING JAVA CLASS
            val gson = GsonBuilder().create()
            val foods = gson.fromJson(responseBody, JSONFood::class.java)

            //RETURNING THE COMPLETE JSON OBJECT WHICH WE NOW MAY MANIPULATE AS WE SEE FIT
            return foods
        }

        //FUNCTION TO HANDLE THE VALUES OF THE JSON OBJECT
        //Page 1 : Paragraph 3
        fun parseJSON(jsonFood: JSONFood, index: Int): Recipe? {
            //SETTING A GLOBAL RECIPE VARIABLE SO IT MAY BE RETURNED
            var recipe: Recipe? = null

            //THIS FUNCTION MAY HAVE CAUSE SUDDEN ERRORS OF VARIOUS TYPES DIFFICULT TO DIAGNOSE SO THEREFORE IS WRAPPED WITHIN A TRY CATCH
            //SOME OF THE ERRORS DISCOVERED THROUGHOUT TESTING HAVE BEEN ACCOUNTED FOR
            //ONE ERROR FOR EXAMPLE IS THE RESULT NOT EXISTING, HENCE THE IF CHECK STATEMENT
            //OTHER ERRORS INCLUDE AN INDEXOUTOFBOUNDS EXCEPTION BEING THROWN WHEN SETTING THE LISTS OF STRING (DIETLABELS AND DISHTYPES)
            try {
                if (jsonFood != null) {
                        var calories = jsonFood.hits[index].recipe.calories.toInt() / 4
                        var calorieFloat = calories.toFloat()
                        recipe = Recipe(
                            jsonFood.hits[index].recipe.label,
                            jsonFood.hits[index].recipe.image,
                            jsonFood.hits[index].recipe.url,
                            calorieFloat,
                            jsonFood.hits[index].recipe.dietLabels,
                            jsonFood.hits[index].recipe.dishType)
                }
            } catch (e: Exception) {
                println(e.toString())
            }
            //HERE WE RETURN THE FINISHED OBJECT
            return recipe
        }

    }
}