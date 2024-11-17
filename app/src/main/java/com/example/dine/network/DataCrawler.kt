package com.example.dine.network

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.example.dine.model.Food
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.dine.R
import com.example.dine.model.AI
import com.example.dine.model.Recipe
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object DataCrawler {
    fun getSearchFoodList(context: Context, keyword:String):ArrayList<Food>{
        val foodList = ArrayList<Food>()
        val foodIdList = ArrayList<String>()
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val builder = Request.Builder()
        builder.addHeader("content-type","application/json")
        builder.addHeader("x-app-id", context.getString(R.string.api_id))
        builder.addHeader("x-app-key", context.getString(R.string.api_key))
        builder.url(context.getString(R.string.search_food_url)+"?query="+keyword)
        val request = builder.build()
        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body!!.string())
            val common = json.getJSONArray("common")
            for (i in 0..common.length()-1){
                val item = common.getJSONObject(i)
                val food = Food()
                food.id = item.getString("tag_id")
                food.name = item.getString("tag_name")
                val requestImage = Request.Builder()
                    .url(item.getJSONObject("photo").getString("thumb"))
                    .build()
                val responseImage = client.newCall(requestImage).execute()
                if (responseImage.isSuccessful)
                {
                    food.thumbnail = responseImage.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }!!
                }
                if (!foodIdList.contains(food.id)){
                    foodList.add(food)
                    foodIdList.add(food.id)
                }
                if (foodList.size==20) break
            }
            foodIdList.clear()
            val branded = json.getJSONArray("branded")
            for (i in 0..branded.length()-1){
                val item = branded.getJSONObject(i)
                val food = Food()
                food.id = item.getString("nix_item_id")
                food.name = item.getString("food_name")
                val requestImage = Request.Builder()
                    .url(item.getJSONObject("photo").getString("thumb"))
                    .build()
                val responseImage = client.newCall(requestImage).execute()
                if (responseImage.isSuccessful)
                {
                    food.thumbnail = responseImage.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }!!
                }
                if (!foodIdList.contains(food.id)){
                    foodList.add(food)
                    foodIdList.add(food.id)
                }
                if (foodList.size==20) break
            }
        } catch (e:Exception){
            Log.e("error",e.toString())
        }
        return foodList
    }

    fun getSearchFoodFirst(context: Context, keyword:String):Food{
        val food = Food()
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val builder = Request.Builder()
        builder.addHeader("content-type","application/json")
        builder.addHeader("x-app-id", context.getString(R.string.api_id))
        builder.addHeader("x-app-key", context.getString(R.string.api_key))
        builder.url(context.getString(R.string.search_food_url)+"?query="+keyword)
        val request = builder.build()
        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body!!.string())
            val common = json.getJSONArray("common")
            val item = common.getJSONObject(0)
            food.id = item.getString("tag_id")
            food.name = item.getString("tag_name")
            val requestImage = Request.Builder()
                .url(item.getJSONObject("photo").getString("thumb"))
                .build()
            val responseImage = client.newCall(requestImage).execute()
            if (responseImage.isSuccessful)
            {
                food.thumbnail = responseImage.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }!!
            }

        } catch (e:Exception){
            Log.e("error",e.toString())
        }
        return food
    }

    suspend fun getFoodDetail(context: Context, keyword: String):Food{
        val food = Food()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        .build()
        val builder = Request.Builder()
        builder.addHeader("content-type","application/json")
        builder.addHeader("x-app-id", context.getString(R.string.api_id))
        builder.addHeader("x-app-key", context.getString(R.string.api_key))

        val json = """
            {
                "query":"$keyword"
            }
        """
        builder.url(context.getString(R.string.nutrition_url))
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(),json)
        builder.post(requestBody)
        val request = builder.build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val json = JSONObject(response.body!!.string())
                val foodDetail = json.getJSONArray("foods").getJSONObject(0)
                food.name = foodDetail.getString("food_name")
                food.serving_qty = foodDetail.getString("serving_qty")
                food.serving_unit = foodDetail.getString("serving_unit")
                food.serving_weight_grams = foodDetail.getString("serving_weight_grams")
                food.calories = foodDetail.getString("nf_calories")
                food.total_fat = foodDetail.getString("nf_total_fat")
                food.saturated_fat = foodDetail.getString("nf_saturated_fat")
                food.cholesterol = foodDetail.getString("nf_cholesterol")
                food.sodium = foodDetail.getString("nf_sodium")
                food.total_carbohydrate = foodDetail.getString("nf_total_carbohydrate")
                food.dietary_fiber = foodDetail.getString("nf_dietary_fiber")
                food.sugars = foodDetail.getString("nf_sugars")
                food.protein = foodDetail.getString("nf_protein")
                food.potassium = foodDetail.getString("nf_potassium")
                food.imageURL =foodDetail.getJSONObject("photo").getString("highres")
            } else {
                val string = AI.converter(context = context, input = "Nutrition fact of a general ${keyword} about weight, calories, total_fat, saturated_fat, cholesterol, sodium, total_carbohydrate, dietary_fiber, sugars, protein, potassium" +
                        "\r\n answer with number only, write in a line, separate by ,").split(",")
                food.name = keyword
                food.serving_qty = ""
                food.serving_unit = ""
                food.serving_weight_grams = string[0].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.calories = string[1].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.total_fat = string[2].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.saturated_fat = string[3].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.cholesterol = string[4].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.sodium = string[5].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.total_carbohydrate = string[6].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.dietary_fiber = string[7].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.sugars = string[8].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.protein = string[9].replace("[^0-9\\.]+".toRegex(), "").trim()
                food.potassium = string[10].replace("[^0-9\\.]+".toRegex(), "").trim()
            }

        } catch (e:Exception){
            val string = AI.converter(context = context, input = "Nutrition fact of a general ${keyword} about weight, calories, total_fat, saturated_fat, cholesterol, sodium, total_carbohydrate, dietary_fiber, sugars, protein, potassium" +
                    "\r\n answer with number only, write in a line, separate by ,").split(",")
            food.name = keyword
            food.serving_qty = ""
            food.serving_unit = ""
            food.serving_weight_grams = string[0].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.calories = string[1].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.total_fat = string[2].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.saturated_fat = string[3].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.cholesterol = string[4].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.sodium = string[5].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.total_carbohydrate = string[6].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.dietary_fiber = string[7].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.sugars = string[8].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.protein = string[9].replace("[^0-9\\.]+".toRegex(), "").trim()
            food.potassium = string[10].replace("[^0-9\\.]+".toRegex(), "").trim()
        }
        return food
    }

    fun getRecipeByName(context: Context, keyword: String):ArrayList<Recipe>{
        var recipeList = arrayListOf<Recipe>()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val builder = Request.Builder()
        builder.url(context.getString(R.string.search_recipe_url)+"?s="+keyword)

        val request = builder.build()
        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body!!.string())
            val itemList = json.getJSONArray("meals")
            for (i in 0..itemList.length()-1){
                val recipe = Recipe()
                val item = itemList.getJSONObject(i)
                recipe.name = item.getString("strMeal")
                recipe.instruction = item.getString("strInstructions")
                recipe.instruction = "- "+recipe.instruction.replace("\r\n","\r\n- ")
                recipe.thumbnailURL = item.getString("strMealThumb")
                val requestImage = Request.Builder()
                    .url(recipe.thumbnailURL)
                    .build()
                val responseImage = client.newCall(requestImage).execute()
                if (responseImage.isSuccessful)
                {
                    recipe.thumbnail = responseImage.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }!!
                }
                recipe.instructionVideoURL = item.getString("strYoutube")
                var index = 1
                while (true){
                    if (item.getString("strIngredient"+index).isNotEmpty()&&!item.getString("strIngredient"+index).equals("null")){
                        recipe.ingredient.add(item.getString("strIngredient"+index))
                        recipe.measure.add(item.getString("strMeasure"+index))
                        index++
                        if (index>20) break
                    } else break
                }
                recipeList.add(recipe)
            }

        } catch (e:Exception){
            Log.e("error",e.toString())
        }
        return recipeList
    }

    fun getRandomRecipe(context: Context):Recipe{
        val recipe = Recipe()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val builder = Request.Builder()
        builder.url(context.getString(R.string.random_recipe_url))

        val request = builder.build()
        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body!!.string())
            val item = json.getJSONArray("meals").getJSONObject(0)
            recipe.name = item.getString("strMeal")
            recipe.instruction = item.getString("strInstructions")
            recipe.instruction = "- "+recipe.instruction.replace("\r\n","\r\n- ")
            recipe.thumbnailURL =item.getString("strMealThumb")
            val requestImage = Request.Builder()
                .url(recipe.thumbnailURL)
                .build()
            val responseImage = client.newCall(requestImage).execute()
            if (responseImage.isSuccessful)
            {
                recipe.thumbnail = responseImage.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }!!
            }
            recipe.instructionVideoURL = item.getString("strYoutube")
            var index = 1
            while (true){
                if (item.getString("strIngredient"+index).isNotEmpty()&&!item.getString("strIngredient"+index).equals("null")){
                    recipe.ingredient.add(item.getString("strIngredient"+index))
                    recipe.measure.add(item.getString("strMeasure"+index))
                    index++
                    if (index>20) break
                } else break
            }
        } catch (e:Exception){
            Log.e("error",e.toString())
        }

        return recipe
    }
    suspend fun getRecipeNutrition(context: Context, recipe: Recipe):Recipe {
        val string = AI.converter(context = context, input = "Nutrition fact of a general ${recipe.name} dish about calories, total_fat, saturated_fat, cholesterol, sodium, total_carbohydrate, dietary_fiber, sugars, protein, potassium" +
                "\r\n answer with number only, write in a line, separate by ,").split(",")
        recipe.calories = string[0].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.total_fat = string[1].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.saturated_fat = string[2].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.cholesterol = string[3].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.sodium = string[4].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.total_carbohydrate = string[5].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.dietary_fiber = string[6].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.sugars = string[7].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.protein = string[8].replace("[^0-9\\.]+".toRegex(), "").trim()
        recipe.potassium = string[9].replace("[^0-9\\.]+".toRegex(), "").trim()
        return recipe
    }
    fun roundUpToTwoDecimals(number: Float): Float {
        return ceil(number * 100) / 100f
    }
}