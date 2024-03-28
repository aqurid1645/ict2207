package com.example.mobileappproj

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database


@Composable
fun ChatScreen(navController: NavController, userId: String) {
    val messages = remember { mutableStateListOf<MessageModel>() }

    LaunchedEffect(Unit) {
        listenForMessages { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
        }
    }

    ChatUI(messages = messages, onSend = { content ->
        val message = MessageModel(sender = userId, content = content)

        val database =
            Firebase.database("https://mobile-android-10925-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Convert the message to a map
        val messageMap = mapOf(
            "sender" to "Username here",
            "content" to message.content,
            "userId" to userId,
            "timestamp" to message.timestamp
        )

        // Add the message to the database
        val messageId =
            database.child("group_chat").push().key // Generate a unique ID for the new message
        if (messageId != null) {
            database.child("group_chat").child(messageId).setValue(messageMap)
                .addOnSuccessListener {
                    // Message added successfully
                }.addOnFailureListener {
                    // Failed to add message
                }
        }
    }, userId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatUI(messages: List<MessageModel>, onSend: (String) -> Unit, userId: String) {
    var text by remember { mutableStateOf("") }

    Column {
        TopAppBar(title = {
            Text(text = "Chat")
        },)
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            itemsIndexed(messages) { _, message ->
                TextBubble(
                    message = message.content,
                    isOwnMessage = message.userId == userId // Assuming "User" is the identifier for the current user
                )
            }
        }
        Row(
            modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    }
                )
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

fun listenForMessages(onMessagesUpdated: (List<MessageModel>) -> Unit) {
    val database =
        Firebase.database("https://mobile-android-10925-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    val query = database.child("group_chat").orderByChild("timestamp").limitToLast(20)

    val messages = mutableListOf<MessageModel>()

    query.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue(MessageModel::class.java)
            message?.let {
                messages.add(it)
                messages.sortByDescending { msg -> msg.timestamp }
                onMessagesUpdated(messages)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            // Handle changes if necessary
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            // Handle removals if necessary
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            // Handle moves if necessary
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle possible errors
        }
    })
}

@Composable
fun TextBubble(
    message: String,
    isOwnMessage: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color = if (isOwnMessage) Color(0xFF9EE7FF) else Color.LightGray,
    textColor: Color = Color.Black,
    padding: Dp = 8.dp,
    cornerRadius: Dp = 12.dp
) {
    Surface(
        modifier = modifier
            .padding(padding)
            .wrapContentWidth(if (isOwnMessage) Alignment.End else Alignment.Start),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(padding),
            color = textColor
        )
    }
}