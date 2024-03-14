package com.example.mobileappproj



import androidx.lifecycle.ViewModel
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel  @Inject constructor(private val repository :AuthRepo) :ViewModel() {
    private val db = Firebase.firestore

    fun signout() {
        repository.signout()
    }
    fun getUserProfile(userId: String): LiveData<UserProfile> {
        val userProfileLiveData = MutableLiveData<UserProfile>()
        db.collection("userProfiles").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                userProfile?.let { userProfileLiveData.value = it }
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.e("ProfileViewModel", "Error fetching user profile", e)
            }
        return userProfileLiveData
    }

    fun updateUserProfile(userId: String, userProfile: UserProfile, onSuccess: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).set(userProfile)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }
}
