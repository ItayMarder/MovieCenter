package com.app.moviecenter.singup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.moviecenter.login.UserCredentials
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class SignUpViewModel : ViewModel() {
    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    fun signUpUser(credentials: UserCredentials, userProperties: UserProperties) {
        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener {
                val user = returnUserAsJson(userProperties)

                db.collection("Users").document(credentials.email)
                    .set(user)
                    .addOnSuccessListener {
                        _signUpResult.value = "Success"
                        Log.w("APP", "created user")
                    }
                    .addOnFailureListener {
                        _signUpResult.value = "Cannot upload profile image"
                        Log.v("APP", "Cannot upload profile image")
                    }
            }

            .addOnFailureListener {
                _signUpResult.value = "The email is already in use"
                Log.v("APP", "The email is already in use")
            }
    }

    fun clearSignUpResult() {
        _signUpResult.value = ""
    }


    private fun returnUserAsJson(userProperties: UserProperties)
            : MutableMap<String, Any> {
        val user: MutableMap<String, Any> = HashMap()
        user["firstName"] = userProperties.firstName.replaceFirstChar(Char::titlecase)
        user["lastName"] = userProperties.lastName.replaceFirstChar(Char::titlecase)
        val randomUuid = UUID.randomUUID().toString()
        user["profilePhoto"] = "profilePictures/$randomUuid.jpg"
        return user
    }
}
