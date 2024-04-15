package com.team10210.univibe.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.R
import com.team10210.univibe.ui.theme.Purple80
import com.team10210.univibe.ui.theme.Shapes
import com.team10210.univibe.ui.theme.UnivibeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    onSignUp: () -> Unit
) {
    val registerState by remember {
        registerViewModel.registerState
    }
    val context = LocalContext.current
    val passwordFocusRequester = FocusRequester()
    val usernameFocusRequester = FocusRequester()
    val confirmPasswordFocusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val signUpErrorMessage = remember { mutableStateOf<String?>(null) }
    val signUpSuccess = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(signUpErrorMessage.value) {
        signUpErrorMessage.value?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
            }
            signUpErrorMessage.value = null // Reset the error message state
        }
    }

    LaunchedEffect(signUpSuccess.value) {
        if (signUpSuccess.value) {
            onSignUp()
            signUpSuccess.value = false // Reset the navigation trigger
        }
    }

    // Scaffold needed to use snackbar
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        // Bottom sheet to show university list
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                UniversityList(
                    registerViewModel = registerViewModel,
                    hideBottomSheet = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    space = screenHeight * 0.03f,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.app_name),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(screenHeight * 0.20f)
                )
                Text(
                    text = stringResource(R.string.sign_up),
                    style = typography.displaySmall,
                    modifier = Modifier.padding(bottom = screenHeight * 0.01f)
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showBottomSheet = true
                    },
                    shape = Shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple80,
                        contentColor = Color.DarkGray
                    )
                ) {
                    Icon(
                        painterResource(id = R.drawable.school),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(25.dp)
                    )
                    if (registerState.university == "") {
                        Text(text = stringResource(R.string.select_uni),
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    } else {
                        Text(text = registerState.university,
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                // Email field
                TextInput(
                    value = registerState.email,
                    onValueChanged = {registerViewModel.updateEmail(it)},
                    label = stringResource(R.string.email),
                    icon = Icons.Default.Email,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            usernameFocusRequester.requestFocus()
                            if (!registerViewModel.validEmail()) {
                                registerViewModel.updateEmail("")
                                focusManager.clearFocus()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.resources
                                            .getString(R.string.email_invalid),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    visualTransformation = VisualTransformation.None
                )
                // Username field
                TextInput(
                    value = registerState.username,
                    onValueChanged = {registerViewModel.updateUsername(it)},
                    label = stringResource(R.string.username),
                    icon = Icons.Default.Person,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    visualTransformation = VisualTransformation.None,
                    focusRequester = usernameFocusRequester
                )
                // Password field
                TextInput(
                    value = registerState.password,
                    onValueChanged = {registerViewModel.updatePassword(it)},
                    label = stringResource(R.string.password),
                    icon = Icons.Default.Lock,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            confirmPasswordFocusRequester.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    focusRequester = passwordFocusRequester
                )
                // Confirm Password field
                TextInput(
                    value = registerState.confirmPassword,
                    onValueChanged = {registerViewModel.updateConfirmPassword(it)},
                    label = stringResource(R.string.confirm_password),
                    icon = Icons.Default.Lock,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (!registerViewModel.checkPasswordMatch()) {
                                focusManager.clearFocus()
                                registerViewModel.updateConfirmPassword("")
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.resources
                                            .getString(R.string.password_match_error),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    focusRequester = confirmPasswordFocusRequester
                )
                // Sign Up button
                Button(
                    onClick = {
                        // Registration wasn't valid
                        if (!registerViewModel.validRegistration()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.resources
                                        .getString(R.string.sign_up_invalid),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            registerViewModel.onSignUpButtonClicked(
                                onError = {
                                    error -> signUpErrorMessage.value = error
                                },
                                onSuccess = {
                                    signUpSuccess.value = true
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        text = stringResource(R.string.sign_up),
                        Modifier.padding(vertical = screenHeight * 0.01f),
                        style = typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun TextInput(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    icon: ImageVector,
    visualTransformation: VisualTransformation,
    focusRequester: FocusRequester? = null,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions
) {

    TextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester ?: FocusRequester()),
        leadingIcon = { Icon(imageVector = icon, null) },
        label = { Text(
            text = label,
            style = typography.bodyLarge
        ) },
        shape = Shapes.large,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    UnivibeTheme {
        RegisterScreen(onSignUp = { println("CLICKED!!!") })
    }
}