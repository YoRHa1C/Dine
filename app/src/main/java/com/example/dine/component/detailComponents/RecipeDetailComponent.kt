package com.example.dine.component.detailComponents

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dine.R
import com.example.dine.activity.ChooseMealDialog
import com.example.dine.activity.LoadingDialog
import com.example.dine.network.DataCrawler
import com.example.dine.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailComponent() {
    var isLoading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val viewConfiguration = LocalViewConfiguration.current
    var serving by remember {
        mutableStateOf(1f)
    }
    var servingText by remember {
        mutableStateOf("1.0")
    }
    var chooseMeal by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            Storage.recipe = DataCrawler.getRecipeNutrition(context, Storage.recipe)
            isLoading = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        AsyncImage(
            model = Storage.recipe.thumbnail,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.3f)
                .clip(RoundedCornerShape(20.dp))
                .padding(10.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = Storage.recipe.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ingredient:",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 0..Storage.recipe.ingredient.size - 1) {
                            Card(
                                modifier = Modifier
                                    .width((screenWidth - 50.dp) / 2)
                                    .height(screenHeight * 0.25f)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = context.getString(R.string.ingredient_image_url) + Storage.recipe.ingredient.get(
                                            i
                                        ).lowercase().replaceFirstChar { it.uppercase() } + ".png",
                                        contentDescription = "",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.8f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                    ) {
                                        Text(
                                            text = Storage.recipe.measure.get(i) + " " + Storage.recipe.ingredient.get(
                                                i
                                            ).lowercase(),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                }
                            }
                        }
                    }
                    Text(
                        text = "Instruction:",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(Storage.recipe.instructionVideoURL)
                            )
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(viewConfiguration.minimumTouchTargetSize.height * 1.25f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.gradient0),
                                        colorResource(id = R.color.gradient1)
                                    )
                                )
                            ),
                    ) {
                        Text(
                            text = "Watch instruction video",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                    Text(
                        text = Storage.recipe.instruction,
                        fontSize = 28.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Nutritions:",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {

                        IconButton(
                            onClick = {
                                if (serving >= 1f) serving = serving - 1f
                                else serving = 0f
                                servingText = serving.toString()
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
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "",
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        OutlinedTextField(
                            value = servingText,
                            onValueChange = { servingText = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            textStyle = TextStyle(
                                fontSize = 18.sp
                            ),
                            shape = RoundedCornerShape(50.dp),
                            maxLines = 1,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    serving = servingText.toFloat()
                                }
                            ),
                            modifier = Modifier
                                .weight(0.6f)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        serving = servingText.toFloat()
                                    }
                                }
                        )
                        IconButton(
                            onClick = {
                                serving = serving + 1f
                                servingText = serving.toString()
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
                                imageVector = Icons.Default.ArrowDropUp,
                                contentDescription = "",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    Text(
                        text = "serving",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color.Black)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Calories",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DataCrawler.roundUpToTwoDecimals(Storage.recipe.calories.toFloat() * serving.toFloat())
                                .toString(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color.Black)
                    )
                    Text(
                        text = "Total Fat ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.total_fat.toFloat() * serving.toFloat())}g",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Text(
                        text = "Saturated Fat ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.calories.toFloat() * serving.toFloat())}g",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    HorizontalDivider()
                    Text(
                        text = "Cholesterol ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.cholesterol.toFloat() * serving.toFloat())}g",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Text(
                        text = "Sodium ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.sodium.toFloat() * serving.toFloat())}mg",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Text(
                        text = "Total Carbohydrate ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.total_carbohydrate.toFloat() * serving.toFloat())}g",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Dietary Fiber ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.dietary_fiber.toFloat() * serving.toFloat())}g",
                            fontSize = 22.sp
                        )
                        HorizontalDivider()
                        Text(
                            text = "Sugars ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.sugars.toFloat() * serving.toFloat())}g",
                            fontSize = 22.sp
                        )
                    }
                    HorizontalDivider()
                    Text(
                        text = "Protein ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.protein.toFloat() * serving.toFloat())}g",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color.Black)
                    )
                    Text(
                        text = "Potassium ${DataCrawler.roundUpToTwoDecimals(Storage.recipe.potassium.toFloat() * serving.toFloat())}mg",
                        fontSize = 22.sp
                    )
                    Button(
                        onClick = {
                            chooseMeal = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(viewConfiguration.minimumTouchTargetSize.height * 1.25f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.gradient0),
                                        colorResource(id = R.color.gradient1)
                                    )
                                )
                            ),
                    ) {
                        Text(text = "Add to meal", color = Color.White, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    if (isLoading) LoadingDialog()
    if (chooseMeal) ChooseMealDialog(
        name = Storage.recipe.name,
        image = Storage.recipe.thumbnailURL,
        calories = DataCrawler.roundUpToTwoDecimals(Storage.recipe.calories.toFloat() * serving).toString(),
        total_fat = DataCrawler.roundUpToTwoDecimals(Storage.recipe.total_fat.toFloat() * serving).toString(),
        saturated_fat = DataCrawler.roundUpToTwoDecimals(Storage.recipe.saturated_fat.toFloat() * serving).toString(),
        cholesterol = DataCrawler.roundUpToTwoDecimals(Storage.recipe.cholesterol.toFloat() * serving).toString(),
        sodium = DataCrawler.roundUpToTwoDecimals(Storage.recipe.sodium.toFloat() * serving).toString(),
        total_carbohydrate = DataCrawler.roundUpToTwoDecimals(Storage.recipe.total_carbohydrate.toFloat() * serving).toString(),
        dietary_fiber = DataCrawler.roundUpToTwoDecimals(Storage.recipe.dietary_fiber.toFloat() * serving).toString(),
        sugars = DataCrawler.roundUpToTwoDecimals(Storage.recipe.sugars.toFloat() * serving).toString(),
        protein = DataCrawler.roundUpToTwoDecimals(Storage.recipe.protein.toFloat() * serving).toString(),
        potassium = DataCrawler.roundUpToTwoDecimals(Storage.recipe.potassium.toFloat() * serving).toString()
    )
}