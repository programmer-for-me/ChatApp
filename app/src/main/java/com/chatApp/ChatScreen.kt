package com.chatApp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.chatApp.model.UserData
import com.chatApp.ui.theme.ChatAppTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chatApp.model.Message
import java.text.SimpleDateFormat
import java.util.Date

class ChatScreen : ComponentActivity() {
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
                    val uid_contact = intent.getStringExtra("contact")
                    val uid = intent.getStringExtra("my")
                    val reference = Firebase.database.reference.child("users")
                    val userList = remember {
                        mutableStateListOf(UserData())
                    }
                    var text = remember {
                        mutableStateOf(TextFieldValue(""))
                    }
                    val messageList = remember {
                        mutableStateListOf(Message())
                    }
                    var user_contact = remember {
                        mutableStateOf(UserData())
                    }
                    var user = remember {
                        mutableStateOf(UserData())
                    }
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
                    val ref = Firebase.database.reference.child("users")
                        .child(uid!!)
                        .child("message")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            messageList.clear()
                            children.forEach {
                                val message = it.getValue(Message::class.java)
                                messageList.add(message ?: Message())
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("ERR", "onCancelled: ${error.message}")
                        }

                    })
                    for (u in userList) {
                        if (u.uid == uid_contact)
                            user_contact.value = u
                    }
                    for (u in userList) {
                        if (u.uid == uid)
                            user.value = u
                    }
                    Column (
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uid != null) {
                            Card (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF03FC88)
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
                                                .data(user_contact.value.photo)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = painterResource(R.drawable.user),
                                            contentDescription = ("no image"),
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.clip(CircleShape)
                                        )
                                        Text(
                                            text = "Sam",
                                            fontSize = 24.sp,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                        )
                                    }

                                }
                            )
                        }
                    }
                    LazyColumn (
                        modifier = Modifier.padding(vertical = 100.dp)
                    ) {
                        items(messageList) {
                            if (it.from == uid)
                                sentMessage(text = it.text!!, userData = user.value)
                            else
                                receivedMessage(text = it.text!!, userData = user.value)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        TextField(
                            text.value,
                            onValueChange = {
                                text.value = it
                            },
                            placeholder = { Text(text = "Hello!") },
                            label = { Text("Send Message") },
                            modifier = Modifier
                                .padding(8.dp),

                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                        )



                        Button(
                            onClick = {
                            val sdf = SimpleDateFormat("HH:mm")
                            val currentDateAndTime = sdf.format(Date())
                            val m = Message(uid_contact, uid, text.value.text, currentDateAndTime)
                            val reference = Firebase.database.reference.child("users")
                            val key = reference.push().key.toString()
                            reference.child(uid)
                                .child("message")
                                .child(key)
                                .setValue(m)
                            reference.child(uid_contact!!)
                                .child("message")
                                .child(key)
                                .setValue(m)

                            text.value = TextFieldValue("")
                        })
                        {
                            Text(text = "send")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun sentMessage(text: String, userData: UserData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ){
        Card (
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEB3B)
            ),
            content = {
                Column (
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    userData.name?.let { Text(text = it, fontSize = 8.sp) }
                    Text(text = text, fontSize = 14.sp)
                }
            }
        )
    }
}

@Composable
fun receivedMessage(text: String, userData: UserData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ){
        Card (
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF00BCD4)
            ),
            content = {
                Column (
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    userData.name?.let { Text(text = it, fontSize = 8.sp) }
                    Text(text = text, fontSize = 14.sp)
                }
            }
        )
    }
}