package com.team10210.univibe.screens.register

import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.team10210.univibe.models.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var registerState = mutableStateOf(RegisterUiState())
        private set

    fun updateEmail(email: String) {
        registerState.value = registerState.value.copy(
            email = email
        )
    }

    fun updateUsername(username: String) {
        registerState.value = registerState.value.copy(
            username = username
        )
    }

    fun updatePassword(password: String) {
        registerState.value = registerState.value.copy(
            password = password
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        registerState.value = registerState.value.copy(
            confirmPassword = confirmPassword
        )
    }

    fun updateUniversity(university: String) {
        registerState.value = registerState.value.copy(
            university = university
        )
    }

    /**
     * Checks if entered email is valid and free
     */
    fun validEmail(): Boolean{
        return !TextUtils.isEmpty(registerState.value.email)
                    && android.util.Patterns.EMAIL_ADDRESS
                        .matcher(registerState.value.email)
                        .matches()
    }

    /**
     * Checks if entered username is valid and free
     */
    fun validUsername(): Boolean {
        return registerState.value.username.isNotBlank()
    }

    /**
     * Checks if confirm password field matches password field
     */
    fun checkPasswordMatch(): Boolean {
        return registerState.value.password == registerState.value.confirmPassword
    }

    private fun validUniversity(): Boolean {
        return registerState.value.university.isNotBlank()
    }

    fun validRegistration() : Boolean {
        return (validEmail() && validUsername() && checkPasswordMatch()
                && registerState.value.password.isNotBlank() && validUniversity())
    }

    fun onSignUpButtonClicked(onError: (String) -> Unit, onSuccess: () -> Unit)  {
        // Do validation checks here too
        createUserInFirebase(
            registerState.value.email,
            registerState.value.password,
            registerState.value.username,
            onError,
            onSuccess
        )
    }

    private fun createUserInFirebase(email: String, password: String, username: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.updateProfile(userProfileChangeRequest {
                    displayName = username
                })
                addUserToDatabase()
                onSuccess()
            }
            .addOnFailureListener {
                onError("Error: ${it.message.toString()}")
            }
    }

    private fun addUserToDatabase() {
        val newUserRef = db.collection("users").document()
        val initialBadgeId = "rdx6J4BSWEIz3dFx3oTW"
        val user = UserModel(
            id = newUserRef.id,
            comments = emptyList(),
            likedPosts = emptyList(),
            userId = Firebase.auth.currentUser?.uid,
            created = Timestamp.now(),
            badgeIds = listOf(initialBadgeId),
            posts = emptyList(),
            university = registerState.value.university,
            lastSpinWheelTime = null,
            numberOfLikes = 0,
            numberOfComments = 0,
            numberOfPosts = 0,
        )
        newUserRef.set(user)
            .addOnSuccessListener { println("User added")}
            .addOnFailureListener { println("Error writing document") }
    }
}