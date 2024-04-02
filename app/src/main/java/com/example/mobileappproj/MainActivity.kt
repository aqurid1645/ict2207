package com.example.mobileappproj

import ForumPostDetailScreen
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobileappproj.security.ServiceManager
import com.example.mobileappproj.security.ServiceStarter
import com.inf2007team12mobileapplication.Screen
import com.inf2007team12mobileapplication.presentation.homepage.HomePageScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
//                    finish()
                    checkPermissionsAndInitialize()
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
            MyAppTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screensWithIcons = listOf(Screen.Home,Screen.Chat,Screen.Forum)
    val hideBottomBarRoutes = listOf(
        Screen.SignIn.route,
        Screen.SignUp.route)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomNavigationBar(navController, screensWithIcons)
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Screen.SignIn.route) {
            composable("signup") {
                SignUpScreen(navController = navController)
            }
            composable("signin") {
                SignInScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable("home") {
              HomePageScreen(navController = navController)
            }
            composable("forum") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ForumMainScreen(navController = navController)
            }
            composable("chat") { backStackEntry ->
                ChatScreen(navController = navController)
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
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, screens: List<Screen>) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val currentRoute = navController.currentDestination?.route
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = null) } ?: Spacer(Modifier) },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Clear the back stack when navigating to avoid going back to the SignIn screen
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    // Define other colors for your dark theme
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
    // Define other colors for your light theme
    surface = Color.White, // Color for the BottomNavigation background
    onSurface = Color.Black // Color for the BottomNavigation text and icons
    // Define other colors for your light theme
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}