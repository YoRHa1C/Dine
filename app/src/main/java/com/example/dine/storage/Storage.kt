package com.example.dine.storage

import com.example.dine.model.DailyIntake
import com.example.dine.model.Food
import com.example.dine.model.Recipe
import com.google.firebase.auth.FirebaseUser

object Storage {
    lateinit var recipe: Recipe
    var suggestRecipes = arrayListOf<Recipe>()
    var suggestFoods = arrayListOf<Food>()
    lateinit var user: FirebaseUser
    val standardDailyIntake = DailyIntake(
        valueList = arrayListOf(
            "2000 cal",
            "65 g",
            "20 g",
            "300 mg",
            "2300 mg",
            "275 g",
            "25 g",
            "25 g",
            "50 g",
            "3500 mg"
        )
    )

}