package com.team10210.univibe.screens.register

data class RegisterUiState (
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val university: String = "",
    val isRegisterSuccessful: Boolean = false
)