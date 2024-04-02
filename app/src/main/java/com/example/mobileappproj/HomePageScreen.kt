package com.inf2007team12mobileapplication.presentation.homepage

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ReplyAll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePageScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to chatting app", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {

                        navController.navigate("profile")
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "User Profile", Modifier.size(20.dp))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Select Your Feature:", fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(60.dp))
            Row {
                ActionButton(
                    icon = Icons.AutoMirrored.Filled.Message,
                    text = "Chat",
                ) {
                    navController.navigate("chat") // Replace with your correct route
                }
                Spacer(modifier = Modifier.height(100.dp))
                ActionButton(icon = Icons.AutoMirrored.Filled.Feed, text = "Forum") {
                    navController.navigate("forum") // Replace with your correct route
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(40.dp)) // Adjust icon size if desired
        Text(text)
    }
}

@Preview
@Composable
fun HomePageScreenPreview() {
    HomePageScreen(navController = rememberNavController())
}