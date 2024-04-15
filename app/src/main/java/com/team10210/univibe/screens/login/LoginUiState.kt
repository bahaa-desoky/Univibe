package com.team10210.univibe.screens.login

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val isLoginSuccessful: Boolean = false
)