package com.example.edamamnutrition.JSONModels

data class Ingredient(
    val food: String,
    val foodId: String,
    val measure: String,
    val quantity: Float,
    val text: String,
    val weight: Float
)