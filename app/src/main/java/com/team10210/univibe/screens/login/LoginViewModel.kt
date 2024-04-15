package com.team10210.univibe.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {
    var loginState = mutableStateOf(LoginUiState())
        private set

    fun updateEmail(email: String) {
        loginState.value = loginState.value.copy(
            email = email
        )
    }

    fun updatePassword(password: String) {
        loginState.value = loginState.value.copy(
            password = password
        )
    }

    /**
     * Checks if username and password match for user in database
     */
    fun validLogin(): Boolean {
        // TODO: Add check to see if username and password exist in database
        return (loginState.value.email.isNotBlank() && loginState.value.password.isNotBlank())
    }

    fun onLoginButtonClicked(onError: (String) -> Unit, onSuccess: () -> Unit) {
        loginWithEmailPassword(
            loginState.value.email,
            loginState.value.password,
            onError,
            onSuccess
        )
    }

    private fun loginWithEmailPassword(email: String, password: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()

            }
            .addOnFailureListener {
                onError("Error: ${it.message.toString()}")
            }
    }
}