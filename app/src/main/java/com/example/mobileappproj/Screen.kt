package com.inf2007team12mobileapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector? = null) {
    object SignUp : Screen("signup", Icons.Default.PersonAdd)
    object SignIn : Screen("signin", Icons.AutoMirrored.Filled.Login)
    object Forum : Screen("forum", Icons.Default.Forum)
    object Chat : Screen("chat", Icons.AutoMirrored.Filled.Message)
    object Home : Screen("home", Icons.Filled.Home)

}
