package com.example.mobileappproj

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Composable
fun ChatScreen(navController: NavController, userId: String) {
    val messages = remember { mutableStateListOf<Message>() }

    LaunchedEffect(Unit) {
        listenForMessages { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
        }
    }

    ChatUI(messages = messages, onSend = { content ->
        val message = Message(sender = userId, content = content)
        sendMessage(message)
    })
}

@Composable
fun ChatUI(messages: List<Message>, onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            itemsIndexed(messages.reversed()) { _, message ->
                Text(text = "${message.sender}: ${message.content}")
            }
        }
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

fun sendMessage(message: Message) {
    val database = Firebase.database.reference
    println(database)
    database.child("messages").push().setValue(message)
        .addOnSuccessListener {
            Log.d("ChatApp", "Test message sent successfully")
        }
        .addOnFailureListener { e ->
            Log.e("ChatApp", "Failed to send test message", e)
        }}

fun listenForMessages(onMessagesUpdated: (List<Message>) -> Unit) {
    val database = Firebase.database.reference
    val query = database.child("messages").orderByChild("timestamp").limitToLast(20)

    val messages = mutableListOf<Message>()

    query.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue(Message::class.java)
            message?.let {
                messages.add(it)
                messages.sortByDescending { msg -> msg.timestamp }
                onMessagesUpdated(messages)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue(Message::class.java)
            message?.let {
                val index = messages.indexOfFirst { msg -> msg.timestamp == it.timestamp }
                if (index >= 0) {
                    messages[index] = it
                }
                onMessagesUpdated(messages)
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val message = snapshot.getValue(Message::class.java)
            message?.let {
                messages.removeAll { msg -> msg.timestamp == it.timestamp }
                onMessagesUpdated(messages)
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            // Handle if needed
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle possible errors
        }
    })
}
