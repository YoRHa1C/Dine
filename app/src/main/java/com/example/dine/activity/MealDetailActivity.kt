package com.example.dine.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.example.dine.R
import com.example.dine.component.detailComponents.FoodDetailComponent
import com.example.dine.component.detailComponents.RecipeDetailComponent

class MealDetailActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealDetailScene(intent)
        }
    }
}

@Composable
fun MealDetailScene(intent: Intent){
    val viewConfiguration = LocalViewConfiguration.current

    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var nameList by remember {
        mutableStateOf(intent.getStringArrayListExtra("nameList"))
    }
    var imageList by remember {
        mutableStateOf(intent.getStringArrayListExtra("imageList"))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            .weight(1f).padding(top = 10.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                nameList?.let {
                    items(it.size) { index->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)),
                        ) {
                            AsyncImage(
                                model = imageList!![index],
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(screenWidth * 0.3f)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize()
                            )
                            Text(
                                text = nameList!![index],
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .width(screenWidth * 0.3f)
                                    .padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}