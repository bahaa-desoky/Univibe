package com.team10210.univibe.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.R
import com.team10210.univibe.ui.theme.Shapes
import com.team10210.univibe.ui.theme.UnivibeTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLogin: () -> Unit,
    onSignUpButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    val loginState by remember {
        loginViewModel.loginState
    }
    val passwordFocusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val loginErrorMessage = remember { mutableStateOf<String?>(null) }
    val loginSuccess = remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(loginErrorMessage.value) {
        loginErrorMessage.value?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
            }
            loginErrorMessage.value = null // Reset the error message state
        }
    }

    LaunchedEffect(loginSuccess.value) {
        if (loginSuccess.value) {
            onLogin()
            loginSuccess.value = false // Reset the navigation trigger
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
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
                    modifier = Modifier.size(screenHeight * 0.24f)
                )
                Text(
                    text = stringResource(R.string.log_in),
                    style = typography.displaySmall,
                    modifier = Modifier.padding(bottom = screenHeight * 0.015f)
                )
                TextInput(
                    value = loginState.email,
                    onValueChanged = { loginViewModel.updateEmail(it) },
                    label = stringResource(R.string.email),
                    icon = Icons.Default.Person,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    visualTransformation = VisualTransformation.None
                )
                TextInput(
                    value = loginState.password,
                    onValueChanged = { loginViewModel.updatePassword(it) },
                    label = stringResource(R.string.password),
                    icon = Icons.Default.Lock,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    focusRequester = passwordFocusRequester
                )
                Button(
                    onClick = {
                          if (!loginViewModel.validLogin()) {
                              scope.launch {
                                  snackbarHostState.showSnackbar(
                                      message = context.resources
                                          .getString(R.string.log_in_invalid),
                                      duration = SnackbarDuration.Short
                                  )
                              }
                          } else {
                              loginViewModel.onLoginButtonClicked(
                                  onError = {
                                      error -> loginErrorMessage.value = error
                                  },
                                  onSuccess = {
                                      loginSuccess.value = true
                                  }
                              )
                          }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        text = stringResource(R.string.log_in),
                        Modifier.padding(vertical = screenHeight * 0.01f),
                        style = typography.titleLarge
                    )
                }
                Divider(
                    color = Color.Black.copy(alpha = 0.3f),
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = screenHeight * 0.03f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.no_account_prompt),
                        style = typography.titleMedium
                    )
                    TextButton(onClick = onSignUpButtonClicked) {
                        Text(
                            text = stringResource(R.string.sign_up),
                            style = typography.titleMedium
                        )
                    }
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
fun LoginScreenPreview() {
    UnivibeTheme {
        LoginScreen(onLogin = { }, onSignUpButtonClicked = { })
    }
}