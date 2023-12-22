package com.chatApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.chatApp.ui.theme.ChatAppTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row {
                            Text(text = "SamChat", fontSize = 48.sp)
                        }
                        val context = LocalContext.current
                        LaunchedEffect(key1 = true) {
                            delay(10000L)
                            val shared = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                            val isLogged = shared.getBoolean("logged", false)
                            val i = if (isLogged) {
                                Intent(context, ContactsScreen::class.java)
                            } else {
                                Intent(context, LoginScreen::class.java)
                            }
                            startActivity(i)
                        }
                    }
                }
            }
        }
    }
}
