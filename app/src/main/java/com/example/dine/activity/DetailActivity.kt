package com.example.dine.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dine.R
import com.example.dine.component.detailComponents.FoodDetailComponent
import com.example.dine.component.detailComponents.RecipeDetailComponent
import com.example.dine.component.mainComponents.RecipeComponent
import com.example.dine.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class DetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetailScene(intent)
        }
    }
}

@Composable
fun DetailScene(intent: Intent) {
    val viewConfiguration = LocalViewConfiguration.current
    var isLoading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(viewConfiguration.minimumTouchTargetSize.height)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.gradient0),
                            colorResource(id = R.color.gradient1)
                        )
                    )
                )
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable {
                        (context as Activity).finish()
                    })
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            if (intent.getStringExtra("type").equals("food")) FoodDetailComponent(foodName = intent.getStringExtra("foodName")!!)
            else RecipeDetailComponent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseMealDialog(name:String, image:String,calories: String, total_fat: String, saturated_fat: String, cholesterol: String, sodium: String, total_carbohydrate: String, dietary_fiber: String, sugars: String, protein: String, potassium: String){
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog){
        DatePickerDialog(onDismissRequest = { showDialog=false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val fileName = hashString(ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd_MM_yyyy")))
                        var file = File(context.filesDir,"${fileName}.abc")
                        if (file.exists()){
                            val objectInputStream = ObjectInputStream(FileInputStream(file))
                            val meal = objectInputStream.readObject() as Meal
                            meal.nameList.add(name)
                            meal.imageList.add(image)
                            meal.calories.add(calories)
                            meal.total_fat.add(total_fat)
                            meal.saturated_fat.add(saturated_fat)
                            meal.cholesterol.add(cholesterol)
                            meal.sodium.add(sodium)
                            meal.total_carbohydrate.add(total_carbohydrate)
                            meal.dietary_fiber.add(dietary_fiber)
                            meal.sugars.add(sugars)
                            meal.protein.add(protein)
                            meal.potassium.add(potassium)
                            file.delete()
                            file = File(context.filesDir,"${fileName}.abc")
                            val objectOutputStream = ObjectOutputStream(FileOutputStream(file))
                            objectOutputStream.writeObject(meal)
                        } else {
                            val meal = Meal(arrayListOf(), arrayListOf(), arrayListOf(),arrayListOf(), arrayListOf(), arrayListOf(),arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())
                            meal.nameList.add(name)
                            meal.imageList.add(image)
                            meal.calories.add(calories)
                            meal.total_fat.add(total_fat)
                            meal.saturated_fat.add(saturated_fat)
                            meal.cholesterol.add(cholesterol)
                            meal.sodium.add(sodium)
                            meal.total_carbohydrate.add(total_carbohydrate)
                            meal.dietary_fiber.add(dietary_fiber)
                            meal.sugars.add(sugars)
                            meal.protein.add(protein)
                            meal.potassium.add(potassium)
                            val objectOutputStream = ObjectOutputStream(FileOutputStream(file))
                            objectOutputStream.writeObject(meal)
                        }

                    }

                    showDialog=false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Text(text = "OK", color = colorResource(id = R.color.gradient1), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog=false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Text(text = "Cancel", color = colorResource(id = R.color.gradient0), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

}

fun hashString(plainText: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(plainText.toByteArray())
    val hexString = hashBytes.joinToString("") { it.toString(16).padStart(2, '0') }
    return hexString
}