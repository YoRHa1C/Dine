package com.example.dine.component.logInComponents

import android.app.Activity.MODE_PRIVATE
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dine.R
import com.example.dine.activity.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.dine.define.Define
import com.example.dine.storage.Storage
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException

@Composable
fun LoginComponent(activityState:MutableState<Int>){
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var emailError by remember {
        mutableStateOf(false)
    }
    var passwordError by remember {
        mutableStateOf(false)
    }
    val viewConfiguration = LocalViewConfiguration.current
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
    val mPrefEditor = sharedPreferences.edit()
    val focusManager = LocalFocusManager.current
    var auth = Firebase.auth

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 100.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Log In", fontSize = 35.sp, style = TextStyle(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colorResource(id = R.color.gradient0),
                            colorResource(id = R.color.gradient1)
                        )
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    if (emailError) Text(text = "Email is wrong!", color = Color.Red) else
                    Text(
                        text = "Email"
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "")
                },
                shape = RoundedCornerShape(15.dp),
                isError = emailError,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) emailError = false
                    }
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    if (passwordError) Text(text = "Password is wrong!", color = Color.Red) else
                    Text(
                        text = "Password"
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "")
                },
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                },
                isError = passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) passwordError = false
                    }
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(viewConfiguration.minimumTouchTargetSize.height.value.dp * 0.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "Forgot password", style = TextStyle(
                    textDecoration = TextDecoration.Underline,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colorResource(id = R.color.gradient0),
                            colorResource(id = R.color.gradient1)
                        )
                    ),
                ),
                    modifier = Modifier.clickable{activityState.value=2})
            }

            Button(
                onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()){
                                    focusManager.clearFocus()
                                    try {
                                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Storage.user = auth.currentUser!!
                                                mPrefEditor.putBoolean(Define.LOG_IN_STATE,true)
                                                mPrefEditor.apply()
                                                context.startActivity(Intent(context, MainActivity::class.java))
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                if (task.exception!!.message!!.contains("email")) emailError = true else
                                                if (task.exception!!.message!!.contains("password")) passwordError = true
                                            }
                                        }
                                    } catch (e: FirebaseAuthException){
                                        when (e.errorCode) {
                                            "INVALID_EMAIL" -> {
                                                emailError =true
                                            }
                                            "WRONG_PASSWORD" -> {
                                                passwordError = true
                                            }
                                            else -> {
                                                Toast.makeText(context,"Oops! Something went wrong \r\nPlease try again!",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    if (email.isEmpty()) emailError = true
                                    if (password.isEmpty()) passwordError = true
                                }

                          },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colorResource(id = R.color.gradient0),
                                colorResource(id = R.color.gradient1)
                            )
                        )
                    ),
            ) {
                Text(text = "Login", fontSize = 20.sp)
            }
        }
    }
}