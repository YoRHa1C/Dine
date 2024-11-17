package com.example.dine.model
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.dine.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AI {
    suspend fun converter(context: Context, input:String):String{
        val model = GenerativeModel(
            "gemini-1.5-flash",
            context.getString(R.string.ai_api_key),
            generationConfig = generationConfig {
                stopSequences = listOf("in conclusion", "-----", "do you need")
                responseMimeType = "text/plain"
            },
        )

        try {
            val response = model.generateContent(input)
            val output = response.text!!
            if (output.isNotEmpty()) {
                Log.e("check", output)
                return output
            }
            else {
                Log.e("check", "reset0")
                return converter(context,input)
            }
        } catch (e:Exception){
            Log.e("check", "reset1")
            return converter(context,input)
        }

    }
}