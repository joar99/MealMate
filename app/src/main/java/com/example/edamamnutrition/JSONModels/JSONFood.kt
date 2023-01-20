package com.example.edamamnutrition.JSONModels

//THIS IS THE ROOT OBJECT THAT WE GET WHEN WE CALL THE FETCHJSON FUNCTION IN UTILS/APICONNECTION
data class JSONFood(
    val _links: Links,
    val count: Int,
    val from: Int,
    val hits: List<Hit>,
    val to: Int
)