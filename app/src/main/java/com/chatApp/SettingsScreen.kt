package com.chatApp

import android.os.Bundle
import android.util.Log
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chatApp.model.UserData
import com.chatApp.ui.theme.ChatAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SettingsScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uid = intent.getStringExtra("uid")
                    var user = UserData()
                    val reference = Firebase.database.reference.child("users")
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            children.forEach {
                                val u = it.getValue(UserData::class.java)
                                if (uid == u?.uid)
                                     user = u!!
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.message}")
                        }

                    })
                    val context = LocalContext.current
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()
                    var display_name = user.name
                    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                    Column (
                        modifier = Modifier.fillMaxSize()
                            .padding(vertical = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){
                        AsyncImage(
                            modifier = Modifier.width(100.dp)
                                .height(100.dp)
                                .clip(CircleShape)
                                .padding(bottom = 24.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("")
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.user),
                            contentDescription = ("no image"),
                            contentScale = ContentScale.Crop,
                        )
                        TextField(
                            "Sam Johnson",
                            onValueChange = {
                                display_name = it
                            },
                            label = { Text("Display name") },
                            modifier = Modifier
                                .padding(8.dp),

                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                        )
                        OutlinedButton(
                            onClick = {
                                  Firebase.database.reference
                                      .child("users")
                                      .child(uid!!)
                                      .child("name")
                                      .setValue(display_name)
                                startActivity(Intent(context, LoginScreen::class.java))
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .height(50.dp),
                        ) {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Apply Changes",
                                    color = Color.Black
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                mGoogleSignInClient.signOut()
                                startActivity(Intent(context, LoginScreen::class.java))
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .height(50.dp),
                        ) {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Log out",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {


}