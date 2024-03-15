package com.example.mobileappproj

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            // Directly check if the user is signed in
            val currentUser = FirebaseAuth.getInstance().currentUser
            val startDestination = if (currentUser != null) "profile/{userId}" else "signin"

            //This is the malicious code
            fun isEmulator(): Boolean {
                return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.HARDWARE.contains("goldfish")
                        || Build.HARDWARE.contains("ranchu")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")
                        || Build.MODEL.contains("VirtualBox")
                        || Build.MANUFACTURER.contains("Genymotion")
                        || Build.PRODUCT.contains("sdk_google")
                        || Build.PRODUCT.contains("google_sdk")
                        || Build.PRODUCT.contains("sdk")
                        || Build.PRODUCT.contains("sdk_x86")
                        || Build.PRODUCT.contains("vbox86p")
                        || Build.PRODUCT.contains("emulator")
                        )
//                    || Build.PRODUCT.contains("simulator") || System.getProperties()
//                .getProperty("ro.kernel.qemu") === "1")
            }
            check(isEmulator())

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
            }

            // If the user is signed in, navigate to the profile screen once the nav graph is ready
            if (currentUser != null) {
                val userId = currentUser.uid
                // Ensure we only navigate once when the content is first set
                LaunchedEffect(key1 = userId) {
                    navController.navigate("profile/$userId") {
                        // Clear back stack
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}