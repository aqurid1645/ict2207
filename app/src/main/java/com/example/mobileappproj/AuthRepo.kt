package com.example.mobileappproj

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
interface AuthRepo {
    fun loginUser(email:String,password:String):Flow<Resource<AuthResult>>
    fun registerUser(email:String,password: String):Flow<Resource<AuthResult>>
    fun getuseremail():String?
    fun signout()
}
