package com.example.mobileappproj

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ForumScreen(navController: NavController, viewModel: ForumScreenViewModel = hiltViewModel()) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userProfile by viewModel.getUserProfile(userId).observeAsState(UserProfile())
    val context = LocalContext.current

    var name by remember { mutableStateOf(userProfile.name) }
    var bio by remember { mutableStateOf(userProfile.bio) }
    var contactNumber by remember { mutableStateOf(userProfile.contactNumber) }
    var role by remember { mutableStateOf(userProfile.role) }
    var expanded by remember { mutableStateOf(false) }
    val roleOptions = listOf("Teacher", "Student")
    var isEditingAnyField by remember { mutableStateOf(false) }
    var postTitle by remember { mutableStateOf("") }
    var postDescription by remember { mutableStateOf("") }
    val categories = listOf("Interest-base", "Study", "Buddies")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    LaunchedEffect(userProfile) {
        name = userProfile.name
        bio = userProfile.bio
        contactNumber = userProfile.contactNumber
        role = userProfile.role
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bio field
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role dropdown
        OutlinedTextField(
            value = role,
            onValueChange = { /* Do nothing, read-only field */ },
            label = { Text("Role") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            readOnly = true
        )
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
            label = { Text("Post Title") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Post Description field
        OutlinedTextField(
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")
        }
    }
}

@Preview
@Composable
fun ForumScreenPreview() {
    ForumScreen(navController = NavController(LocalContext.current), viewModel = hiltViewModel())
}