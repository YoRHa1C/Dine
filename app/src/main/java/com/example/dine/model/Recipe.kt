package com.example.dine.model

import android.graphics.Bitmap

class Recipe() {
    lateinit var name: String
    lateinit var instruction: String
    lateinit var instructionVideoURL: String
    lateinit var thumbnail: Bitmap
    lateinit var thumbnailURL:String
    var ingredient = arrayListOf<String>()
    var measure = arrayListOf<String>()
    var serving = 1f
    var calories = "0f"
    var total_fat = "0f"
    var saturated_fat = "0f"
    var cholesterol = "0f"
    var sodium = "0f"
    var total_carbohydrate = "0f"
    var dietary_fiber = "0f"
    var sugars = "0f"
    var protein = "0f"
    var potassium = "0f"
}