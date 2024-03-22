import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobileappproj.ForumScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumPostDetailScreen(navController: NavController, title: String, viewModel: ForumScreenViewModel = hiltViewModel()) {
    val posts by viewModel.getAllPosts().observeAsState(initial = listOf())
    val forumPost = posts.find { it.title == title } ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Make sure this navigation path matches your navGraph
                    navController.navigate("forum")
                }
            ) {
                Text(text = "Return")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            // Displaying the post title
            Text(
                text = "Title: ${forumPost.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Displaying the post category
            Text(
                text = "Category: ${forumPost.category}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Displaying the author's name
            Text(
                text = "Posted by: ${forumPost.userName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Displaying the post description
            Text(
                text = "Description: ${forumPost.description}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Optionally, you can add more details or interactive elements here
        }
    }
}
