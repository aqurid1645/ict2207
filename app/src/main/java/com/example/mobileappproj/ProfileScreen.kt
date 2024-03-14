package com.example.mobileappproj
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileScreenViewModel = hiltViewModel()) {
    // Retrieve current user ID using Firebase Auth
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Observe the user profile from the ViewModel
    val userProfile by viewModel.getUserProfile(userId).observeAsState(UserProfile())

    // UI state
    val context = LocalContext.current
    var name by remember { mutableStateOf(userProfile.name) }
    var bio by remember { mutableStateOf(userProfile.bio) }
    var contactNumber by remember { mutableStateOf(userProfile.contactNumber) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
        TextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text("Contact Number") },
            isError = !isValidInternationalContact(contactNumber)
        )
        Button(onClick = {
            if (isValidInternationalContact(contactNumber)) {
                viewModel.updateUserProfile(userId, UserProfile(name, bio, contactNumber)) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid contact number", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Save Profile")
        }
        Button(onClick = {
            viewModel.signout() // Ensure this method correctly signs out the user
            navController.navigate("signin") {
                popUpTo(0) { inclusive = true } // Use the ID of your start destination in the NavGraph
            }
        }) {
            Text("Logout")
        }
    }
}

fun isValidInternationalContact(contact: String): Boolean {
    return contact.matches(Regex("^\\+?[1-9]\\d{1,14}$"))
}