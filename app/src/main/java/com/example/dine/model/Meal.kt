package com.example.dine.model

import java.io.Serializable
import java.time.LocalDate

data class Meal(
    var nameList: ArrayList<String>,
    var imageList:ArrayList<String>,
    var calories: ArrayList<String>,
    var total_fat: ArrayList<String>,
    var saturated_fat: ArrayList<String>,
    var cholesterol : ArrayList<String>,
    var sodium : ArrayList<String>,
    var total_carbohydrate : ArrayList<String>,
    var dietary_fiber : ArrayList<String>,
    var sugars : ArrayList<String>,
    var protein : ArrayList<String>,
    var potassium : ArrayList<String>,
):Serializable