package com.example.dine.component.mainComponents

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.example.dine.activity.DetailActivity
import com.example.dine.activity.LoadingDialog
import com.example.dine.activity.getContrastingColor
import com.example.dine.model.Food
import com.example.dine.network.DataCrawler
import com.example.dine.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoodComponent(){
    var searchText by remember {
        mutableStateOf("")
    }
    var searchLabel by remember {
        mutableStateOf("Search food")
    }
    var foodList by remember {
        mutableStateOf(ArrayList<Food>())
    }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isLoading by remember {
        mutableStateOf(false)
    }
    var isShowSuggest by remember {
        mutableStateOf(true)
    }
    val viewConfiguration = LocalViewConfiguration.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(
                fontSize = 18.sp
            ),
            label = {
                if (searchText.isEmpty())
                    Text(
                        text = searchLabel
                    )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    CoroutineScope(Dispatchers.IO).launch {
                        isLoading = true
                        foodList = DataCrawler.getSearchFoodList(
                            context = context,
                            keyword = searchText.trim()
                        )
                        isShowSuggest=false
                        isLoading = false
                    }
                }
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            },
            trailingIcon = {
                if (searchText.isNotEmpty())
                    IconButton(onClick = { searchText = "" }) {
                        Icon(imageVector = Icons.Default.Cancel, "")
                    }
            },
            maxLines = 1,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        searchLabel = ""
                    } else {
                        searchText = searchText.trim()
                        searchLabel = "Search food"
                    }
                }
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (isShowSuggest){
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Storage.suggestFoods.forEach { food ->
                    val palette = Palette.from(food.thumbnail).generate()
                    val dominantColor = Color(palette.dominantSwatch!!.rgb)
                    val reverseColor = getContrastingColor(dominantColor)
                    Box(
                        modifier = Modifier
                            .width((screenWidth-30.dp)/2)
                            .height(viewConfiguration.minimumTouchTargetSize.height * 2f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                            .clickable {
                                val intent = Intent(context, DetailActivity::class.java)
                                intent.putExtra("foodName", food.name.lowercase())
                                intent.putExtra("type","food")
                                context.startActivity(intent)
                            },
                    ) {
                        AsyncImage(
                            model = food.thumbnail,
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(screenWidth * 0.3f)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(dominantColor, reverseColor.copy(alpha = 0.25f))
                                    )
                                )
                        )
                        Text(
                            text = food.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = reverseColor,
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
        }else
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(foodList) { food ->
                val palette = Palette.from(food.thumbnail).generate()
                val dominantColor = Color(palette.dominantSwatch!!.rgb)
                val reverseColor = getContrastingColor(dominantColor)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(viewConfiguration.minimumTouchTargetSize.height * 1.5f)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .clickable {
                            val intent = Intent(context, DetailActivity::class.java)
                            intent.putExtra("foodName", food.name.lowercase())
                            intent.putExtra("type","food")
                            context.startActivity(intent)
                        },
                ) {
                    AsyncImage(
                        model = food.thumbnail,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(screenWidth * 0.3f)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(dominantColor, reverseColor.copy(alpha = 0.5f))
                                )
                            )
                    )
                    Text(
                        text = food.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = reverseColor,
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
    if (isLoading) LoadingDialog()
}