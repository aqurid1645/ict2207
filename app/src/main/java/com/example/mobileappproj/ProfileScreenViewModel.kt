package com.example.mobileappproj



import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel  @Inject constructor(private val repository :AuthRepo) :ViewModel() {
    private val db = Firebase.firestore

    fun signout() {
        repository.signout()
    }
    fun getUserProfile(userId: String): LiveData<UserProfile> {
        val userProfileLiveData = MutableLiveData<UserProfile>()
        db.collection("users").document(userId).get()
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

    fun isContactNumberTaken(userId: String, contactNumber: String, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("contactNumber", contactNumber)
            .get()
            .addOnSuccessListener { documents ->
                // If no documents found, or the only document found is for the current user, then the number is not considered taken
                val isTaken = documents.any { document -> document.id != userId }
                onResult(isTaken)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error checking if contact number is taken", exception)
                onResult(false) // or handle error as needed
            }
    }
    

}
