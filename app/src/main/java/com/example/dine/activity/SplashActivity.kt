package com.example.dine.activity

import android.app.Activity.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dine.R
import com.example.dine.define.Define
import com.example.dine.model.AI
import com.example.dine.network.DataCrawler
import com.example.dine.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScene()
        }
    }
}

@Preview
@Composable
fun SplashScene() {
    val context = LocalContext.current
    var isLoading by remember {
        mutableStateOf(false)
    }
    val sharedPreferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    val mPrefEditor = sharedPreferences.edit()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.gradient0), colorResource(id = R.color.gradient1)
                    ),
                )
            )
    ) {
        Column(
            modifier = Modifier.align(
                Alignment.Center
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "D I N E",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(50.dp))
            if (isLoading) CircularProgressIndicator(color = Color.White)
        }
    }

    LaunchedEffect(Unit) {
        delay(1000);
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 0..5){
                Storage.suggestRecipes.add(DataCrawler.getRandomRecipe(context))
            }
            val result = AI.converter(context = context, "Give me other 6 random common food in nutritionx write result only as a list and separate by ,")
            result.split(",").forEach{ food ->
                Storage.suggestFoods.add(DataCrawler.getSearchFoodFirst(context = context, keyword = food.trim()))
            }
            if (!sharedPreferences.getBoolean(Define.LOG_IN_STATE,false)){
                context.startActivity(Intent(context,LoginActivity::class.java))
            } else{
                context.startActivity(Intent(context,MainActivity::class.java))
            }
        }
    }
}