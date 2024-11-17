package com.example.dine.model

data class DailyIntake (
    val nutritionList: ArrayList<String> = arrayListOf(
        "Calories",
        "Total Fat",
        "Saturated Fat",
        "Cholesterol",
        "Sodium",
        "Carbohydrate",
        "Dietary Fiber",
        "Sugars",
        "Protein",
        "Potassium"),
    val valueList: ArrayList<String> = arrayListOf<String>()
)