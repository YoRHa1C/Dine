package com.example.dine.component.mainComponents

import android.app.Activity
import android.app.Activity.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dine.R
import com.example.dine.activity.LoginActivity
import com.example.dine.activity.MealDetailActivity
import com.example.dine.activity.hashString
import com.example.dine.define.Define
import com.example.dine.model.Meal
import com.example.dine.storage.Storage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ManageComponent() {
    val viewConfiguration = LocalViewConfiguration.current
    val name = "User"
    val context = LocalContext.current
    val auth = Firebase.auth
    val sharedPreferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    val mPrefEditor = sharedPreferences.edit()
    var chosenState by remember {
        mutableStateOf(0)
    }
    val date = LocalDate.now()
    var meal by remember {
        mutableStateOf(
            Meal(
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf()
            )
        )
    }
//    val auth = Firebase.auth
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { if (chosenState != 0) chosenState = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (chosenState == 0) colorResource(id = R.color.gradient1) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            color = if (chosenState != 0) colorResource(id = R.color.gradient0) else Color.Transparent
                        ),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(
                            text = "Day",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (chosenState == 0) Color.White else Color.Black
                        )
                    }
                    Button(
                        onClick = { if (chosenState != 1) chosenState = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (chosenState == 1) colorResource(id = R.color.gradient1) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            color = if (chosenState != 1) colorResource(id = R.color.gradient0) else Color.Transparent
                        ),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(
                            text = "Week", fontSize = 18.sp, fontWeight = FontWeight.Medium,
                            color = if (chosenState == 1) Color.White else Color.Black
                        )
                    }
                    Button(
                        onClick = { if (chosenState != 2) chosenState = 2 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (chosenState == 2) colorResource(id = R.color.gradient1) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            color = if (chosenState != 2) colorResource(id = R.color.gradient0) else Color.Transparent
                        ),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(
                            text = "Month", fontSize = 18.sp, fontWeight = FontWeight.Medium,
                            color = if (chosenState == 2) Color.White else Color.Black
                        )
                    }

                }
                when (chosenState) {
                    0 -> {
                        val datePickerState = rememberDatePickerState()
                        val fileName = hashString(
                            date.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                        )
                        var file = File(context.filesDir, "${fileName}.abc")
                        if (file.exists()) {
                            val objectInputStream = ObjectInputStream(FileInputStream(file))
                            meal = objectInputStream.readObject() as Meal
                        } else {
                            meal = Meal(
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf(),
                                arrayListOf()
                            )
                        }
                        Card(
                            modifier = Modifier
                                .padding(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                        ) {
                            Column {
                                DatePicker(
                                    state = datePickerState,
                                    colors = DatePickerDefaults.colors(containerColor = Color.White)
                                )
                                datePickerState.selectedDateMillis?.let {
                                    val fileName = hashString(
                                        ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                            .format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                                    )
                                    var file = File(context.filesDir, "${fileName}.abc")
                                    if (file.exists()) {
                                        val objectInputStream =
                                            ObjectInputStream(FileInputStream(file))
                                        meal = objectInputStream.readObject() as Meal
                                        Button(
                                            onClick = {
                                                file.delete()
                                                meal = Meal(
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf(),
                                                    arrayListOf()
                                                )
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 10.dp
                                            ),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(viewConfiguration.minimumTouchTargetSize.height * 1.25f)
                                                .padding(horizontal = 10.dp),
                                        ) {
                                            Text(
                                                text = "Delete meal",
                                                color = Color.Red,
                                                fontSize = 22.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    } else {
                                        meal = Meal(
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf(),
                                            arrayListOf()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        meal = Meal(
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf()
                        )
                        val firstDayOfWeek = date.with(WeekFields.ISO.dayOfWeek(), 1)
                        var firstOfWeek by remember {
                            mutableStateOf(firstDayOfWeek)
                        }
                        for (i in 0..6){
                            val fileName = hashString(
                                firstOfWeek.plusDays(i.toLong()).format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                            )
                            val file = File(context.filesDir, "${fileName}.abc")
                            if (file.exists()){
                                val objectInputStream =
                                    ObjectInputStream(FileInputStream(file))
                                val mealTemp = objectInputStream.readObject() as Meal
                                mealTemp.nameList.forEach { meal.nameList.add(it) }
                                mealTemp.imageList.forEach { meal.imageList.add(it) }
                                mealTemp.calories.forEach { meal.calories.add(it) }
                                mealTemp.total_fat.forEach { meal.total_fat.add(it) }
                                mealTemp.saturated_fat.forEach { meal.saturated_fat.add(it) }
                                mealTemp.cholesterol.forEach { meal.cholesterol.add(it) }
                                mealTemp.sodium.forEach { meal.sodium.add(it) }
                                mealTemp.total_carbohydrate.forEach { meal.total_carbohydrate.add(it) }
                                mealTemp.dietary_fiber.forEach { meal.dietary_fiber.add(it) }
                                mealTemp.sugars.forEach { meal.sugars.add(it) }
                                mealTemp.protein.forEach { meal.protein.add(it) }
                                mealTemp.potassium.forEach { meal.potassium.add(it) }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            IconButton(
                                onClick = {
                                    firstOfWeek = firstOfWeek.minusWeeks(1)
                                }, modifier = Modifier
                                    .weight(0.2f)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(id = R.color.gradient0),
                                                colorResource(id = R.color.gradient1)
                                            )
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                            Text(
                                text = firstOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + firstOfWeek.plusWeeks(
                                    1
                                ).minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.6f),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = {
                                    firstOfWeek = firstOfWeek.plusWeeks(1)
                                }, modifier = Modifier
                                    .weight(0.2f)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(id = R.color.gradient0),
                                                colorResource(id = R.color.gradient1)
                                            )
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }

                    2 -> {
                        meal = Meal(
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf(),
                            arrayListOf()
                        )
                        var today by remember {
                            mutableStateOf(date)
                        }
                        val yearMonth = YearMonth.from(today)
                        val firstDayOfMonth = yearMonth.atDay(1)
                        for (i in 0..yearMonth.lengthOfMonth()-1){
                            val fileName = hashString(
                                firstDayOfMonth.plusDays(i.toLong()).format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
                            )
                            val file = File(context.filesDir, "${fileName}.abc")
                            if (file.exists()){
                                val objectInputStream =
                                    ObjectInputStream(FileInputStream(file))
                                val mealTemp = objectInputStream.readObject() as Meal
                                mealTemp.nameList.forEach { meal.nameList.add(it) }
                                mealTemp.imageList.forEach { meal.imageList.add(it) }
                                mealTemp.calories.forEach { meal.calories.add(it) }
                                mealTemp.total_fat.forEach { meal.total_fat.add(it) }
                                mealTemp.saturated_fat.forEach { meal.saturated_fat.add(it) }
                                mealTemp.cholesterol.forEach { meal.cholesterol.add(it) }
                                mealTemp.sodium.forEach { meal.sodium.add(it) }
                                mealTemp.total_carbohydrate.forEach { meal.total_carbohydrate.add(it) }
                                mealTemp.dietary_fiber.forEach { meal.dietary_fiber.add(it) }
                                mealTemp.sugars.forEach { meal.sugars.add(it) }
                                mealTemp.protein.forEach { meal.protein.add(it) }
                                mealTemp.potassium.forEach { meal.potassium.add(it) }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            IconButton(
                                onClick = {
                                    today = today.minusMonths(1)
                                }, modifier = Modifier
                                    .weight(0.2f)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(id = R.color.gradient0),
                                                colorResource(id = R.color.gradient1)
                                            )
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                            Text(
                                text = "${today.monthValue}/${today.year}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.6f),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = {
                                    today = today.plusMonths(1)
                                }, modifier = Modifier
                                    .weight(0.2f)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(id = R.color.gradient0),
                                                colorResource(id = R.color.gradient1)
                                            )
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                }

            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Dishes", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            meal.imageList.forEach { Log.e("check", it) }
                            if (meal.imageList.size >= 1) {
                                AsyncImage(
                                    model =meal.imageList[0],
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                )

                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            if (meal.imageList.size >= 2) {

                                AsyncImage(
                                    model = meal.imageList[1],
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            if (meal.imageList.size >= 3) {
                                AsyncImage(
                                    model = meal.imageList[2],
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .size(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (meal.nameList.isNotEmpty()) {
                                            val intent =
                                                Intent(context, MealDetailActivity::class.java)
                                            intent.putExtra("nameList", meal.nameList)
                                            intent.putExtra("imageList", meal.imageList)
                                            context.startActivity(intent)
                                        }
                                    }
                            ) {

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .align(
                                            Alignment.Center
                                        )
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(text = "Nutrition", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        for (i in 0..Storage.standardDailyIntake.valueList.size - 1) {
                            NutritionComponent(
                                name = Storage.standardDailyIntake.nutritionList[i],
                                value = if (meal.nameList.isNotEmpty()) sumOfArray(
                                    meal,
                                    i
                                ) else "0",
                                maxValue = Storage.standardDailyIntake.valueList[i],
                                chosenState = chosenState
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    auth.signOut()
                    mPrefEditor.putBoolean(Define.LOG_IN_STATE,false)
                    mPrefEditor.apply()
                    val files = context.filesDir.listFiles()
                    for (i in files){
                        if (!!i.name.equals("profileInstaller")) i.delete()
                    }
                    context.startActivity(Intent(context,LoginActivity::class.java))
                    (context as Activity).finish()
                },
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(viewConfiguration.minimumTouchTargetSize.height * 1.25f)
                    .padding(horizontal = 10.dp),
            ) {
                Text(text = "Logout", color = Color.Red, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun NutritionComponent(name: String, value: String, maxValue: String, chosenState:Int) {
    var x=0;
    val date = LocalDate.now()
    val yearMonth = YearMonth.from(date)
    when (chosenState){
        0->x=1;
        1->x=7;
        2->x=yearMonth.lengthOfMonth()
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f)
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .height(10.dp)
                .weight(0.4f)
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth(value.toFloat() / maxValue.split(" ")[0].toFloat()/x)
                    .height(10.dp)
                    .background(
                        if (value.toFloat() < maxValue.split(" ")[0].toInt()*x / 2f) colorResource(
                            id = R.color.gradient0
                        )
                        else if (maxValue.split(" ")[0].toInt()*x / 2f <= value.toFloat() && value.toFloat() <= maxValue.split(
                                " "
                            )[0].toInt()*x
                        ) colorResource(
                            id = R.color.gradient1
                        )
                        else Color.Red
                    )
            )
        }
        Text(
            text = value + "/" + maxValue.split(" ")[0].toInt()*x+" "+maxValue.split(" ")[1],
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f)
        )
    }
}

fun sumOfArray(meal: Meal, i: Int): String {
    var sum = 0f;
    when (i) {
        0 -> meal.calories.forEach { sum += it.toFloat() }
        1 -> meal.total_fat.forEach { sum += it.toFloat() }
        2 -> meal.saturated_fat.forEach { sum += it.toFloat() }
        3 -> meal.cholesterol.forEach { sum += it.toFloat() }
        4 -> meal.sodium.forEach { sum += it.toFloat() }
        5 -> meal.total_carbohydrate.forEach { sum += it.toFloat() }
        6 -> meal.dietary_fiber.forEach { sum += it.toFloat() }
        7 -> meal.sugars.forEach { sum += it.toFloat() }
        8 -> meal.protein.forEach { sum += it.toFloat() }
        9 -> meal.potassium.forEach { sum += it.toFloat() }
    }
    return sum.toString()
}
