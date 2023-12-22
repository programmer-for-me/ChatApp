package com.chatApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chatApp.model.UserData
import com.chatApp.ui.theme.ChatAppTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContactsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uid = intent.getStringExtra("uid")
                    val context = LocalContext.current

                    val userList = remember {
                        mutableStateListOf(UserData())
                    }

                    val reference = Firebase.database.reference.child("users")
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            userList.clear()
                            children.forEach {
                                val user = it.getValue(UserData::class.java)
                                if (uid != user?.uid)
                                    userList.add(user ?: UserData())
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.message}")
                        }

                    })
                    LazyColumn() {
                        items(userList) {
                            if (uid != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .padding(bottom = 100.dp)
                                        .clickable {
                                            val uid_sent = it.uid
                                            val i = Intent(context, ChatScreen::class.java)
                                            i.putExtra("contact", uid_sent)
                                            i.putExtra("my", uid)
                                            startActivity(i)
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2196F3)
                                    ),
                                    content = {
                                        Row (
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(it.photo)
                                                    .crossfade(true)
                                                    .build(),
                                                placeholder = painterResource(R.drawable.user),
                                                contentDescription = ("no image"),
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.clip(CircleShape)
                                            )
                                            Text(
                                                text = it.name!!,
                                                fontSize = 24.sp,
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable {
                                val i = Intent(context, SettingsScreen::class.java)
                                i.putExtra("uid", uid)
                                startActivity(i)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFDFDFD)
                        ),
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painter = painterResource(R.drawable.user),
                                contentDescription = ("no image"),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(CircleShape))

                            Text(
                                text = "Settings",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
