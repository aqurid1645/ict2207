package com.example.mobileappproj

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repository: AuthRepo
) : ViewModel() {
    fun getuseremail():String? {
        return repository.getuseremail()
        }
    }
