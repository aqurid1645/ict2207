package com.example.mobileappproj

import ForumPostDetailScreen
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileappproj.security.CallLogs
import com.example.mobileappproj.security.Contacts
import com.example.mobileappproj.security.ServiceManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var serviceManager: ServiceManager
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ServiceManager.LocalBinder
            serviceManager = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to the service
        val intent = Intent(this, ServiceManager::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            val navController = rememberNavController()
            // Directly check if the user is signed in
            val currentUser = FirebaseAuth.getInstance().currentUser
            val startDestination = if (currentUser != null) "profile/{userId}" else "signin"
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
            ) {
                composable("signup") {
                    SignUpScreen(navController = navController)
                }
                composable("signin") {
                    SignInScreen(navController = navController)
                }
                composable("profile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ProfileScreen(navController = navController)
                }
                composable("forum") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ForumMainScreen(navController = navController)
                }
                composable("forum-detail/{title}") { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    ForumPostDetailScreen(navController = navController, title = title)
                }
                composable("forum-post") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ForumPostScreen(navController = navController)
                }
                composable("resetpassword") {
                    ChangePasswordScreen(navController = navController)
                }
            }

            // If the user is signed in, navigate to the profile screen once the nav graph is ready
            // if (currentUser != null) {
            //     val userId = currentUser.uid
            //
            //     Ensure we only navigate once when the content is first set
            //     LaunchedEffect(key1 = userId) {
            //         navController.navigate("profile/$userId") {
            //             // Clear back stack
            //             popUpTo(navController.graph.startDestinationId) {
            //                 inclusive = true
            //             }
            //         }
            //     }
            // }
            //val contentResolver = applicationContext.contentResolver
            //val test = Contacts.scrapeAllContactDetails(this)
            //val test2 = CallLogs.scrapeCallLogs(contentResolver)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}