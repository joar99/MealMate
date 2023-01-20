package com.example.edamamnutrition.Adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.edamamnutrition.JSONModels.Recipe

class FoodViewModel : ViewModel() {

    var recipeMutableLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData()
    var recipeList: MutableList<Recipe>? = null


    fun init() {

        recipeMutableLiveData.value = recipeList
    }



}