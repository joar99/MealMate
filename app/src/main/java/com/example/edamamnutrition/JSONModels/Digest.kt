package com.example.edamamnutrition.JSONModels

data class Digest(
    val daily: Float,
    val hasRDI: Boolean,
    val label: String,
    val schemaOrgTag: String,
    //val sub: Sub,
    val tag: String,
    val total: Float,
    val unit: String
)