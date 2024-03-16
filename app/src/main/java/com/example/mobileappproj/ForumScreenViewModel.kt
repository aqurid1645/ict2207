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

data class ForumPost(
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
@HiltViewModel
class ForumScreenViewModel  @Inject constructor(private val repository :AuthRepo) :ViewModel() {
    private val db = Firebase.firestore

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
    fun createForumPost(post: ForumPost, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("forum").add(post)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun getAllPosts(): LiveData<List<ForumPost>> {
        val postsLiveData = MutableLiveData<List<ForumPost>>()
        db.collection("forum")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postsList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(ForumPost::class.java)
                }
                postsLiveData.value = postsList
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching forum posts", e)
                postsLiveData.value = emptyList() // Handle the error as needed
            }
        return postsLiveData
    }
}
