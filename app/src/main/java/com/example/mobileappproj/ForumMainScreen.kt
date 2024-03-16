package com.example.mobileappproj

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(navController: NavController, viewModel: ForumScreenViewModel = hiltViewModel()) {
    // Fetch all posts
    val posts by viewModel.getAllPosts().observeAsState(initial = listOf())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forum Main") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("forum-post")
            }) {
                Text(text = "Create Post")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(posts) { post ->
                PostItem(postTitle = post.title, onClick = {
                    // Here, navigate to the details of the clicked post.
                    // The implementation depends on your navigation setup.
                    navController.navigate("forum-post") // Update with the correct navigation route and parameter
                })
                Divider()
            }
        }
    }
}

@Composable
fun PostItem(postTitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = postTitle, style = MaterialTheme.typography.bodyMedium)
            // You can add more details here if needed
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForumMainScreenPreview() {
    // Assuming a NavController can be provided here. You might need to adjust this for actual previews.
    // ForumMainScreen(navController = rememberNavController(), viewModel = viewModel())
}
