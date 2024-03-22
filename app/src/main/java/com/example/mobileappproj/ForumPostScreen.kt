package com.example.mobileappproj

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForumPostScreen(navController: NavController, viewModel: ForumScreenViewModel = hiltViewModel()) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userProfile by viewModel.getForumUserProfile(userId).observeAsState(UserProfile())
    val context = LocalContext.current

    var name by remember { mutableStateOf(userProfile.name) }
    var contactNumber by remember { mutableStateOf(userProfile.contactNumber) }
    var role by remember { mutableStateOf(userProfile.role) }
    var expanded by remember { mutableStateOf(false) }
    val roleOptions = listOf("Teacher", "Student")
    var postTitle by remember { mutableStateOf("") }
    var postDescription by remember { mutableStateOf("") }
    val categories = listOf("Interest-base", "Study", "Buddies")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    LaunchedEffect(userProfile) {
        name = userProfile.name
        contactNumber = userProfile.contactNumber
        role = userProfile.role
    }

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roleOptions.forEach { roles ->
                DropdownMenuItem(
                    text = { Text(roles) },
                    onClick = {
                        role = roles
                        expanded = false
                    }
                )
            }
        }
        Text("Name: $name", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Role: $role", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(8.dp))

        // Category dropdown
        Text("Choose Your Category:")
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedCategory = category }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (category == selectedCategory),
                    onClick = { selectedCategory = category }
                )
                Text(text = category)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Post Title field
        OutlinedTextField(
            value = postTitle,
            onValueChange = { postTitle = it },
            label = { Text("Post Title") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Post Description field
        OutlinedTextField(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.8f),
            value = postDescription,
            onValueChange = { postDescription = it },
            label = { Text("Post Description") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = {
                val newPost = ForumPost(
                    userId = userId,
                    userName = name,
                    title = postTitle,
                    category = selectedCategory,
                    description = postDescription
                )
                viewModel.createForumPost(
                    post = newPost,
                    onSuccess = {
                        Toast.makeText(context, "Post Created!", Toast.LENGTH_SHORT).show()
                        // You may want to navigate away or clear the form here
                    },
                    onFailure = { e ->
                        Toast.makeText(context, "Failed to create post: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                )
                navController.navigate("forum")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")
        }
    }
}

@Preview
@Composable
fun ForumPostScreenPreview() {
    ForumPostScreen(navController = NavController(LocalContext.current), viewModel = hiltViewModel())
}