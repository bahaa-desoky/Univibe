package com.team10210.univibe.screens.createcomment

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.R
import com.team10210.univibe.ui.theme.Shapes
import com.team10210.univibe.ui.theme.UnivibeTheme
import kotlinx.coroutines.launch


@Composable
fun CreateCommentScreen(
    postId: String,
    createCommentViewModel: CreateCommentViewModel = viewModel(),
    goBackToPost: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var content by remember { mutableStateOf("") }

    val createCommentSuccess = remember { mutableStateOf(false) }

    LaunchedEffect(createCommentSuccess.value) {
        if (createCommentSuccess.value) {
            goBackToPost()
            createCommentSuccess.value = false // Reset the navigation trigger
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
                    .padding(horizontal = 8.dp, vertical = 30.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    space = screenHeight * 0.01f,
                    alignment = Alignment.Top
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.new_comment),
                    style = typography.titleLarge,
                    modifier = Modifier.padding(bottom = screenHeight * 0.015f)
                )
                TextInput(
                    value = content,
                    onValueChanged = { content = it },
                    label = stringResource(R.string.new_comment_content),
                    icon = Icons.Default.Edit,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

                )

                Spacer(modifier = Modifier.weight(1f))
                Divider(
                    color = Color.Black.copy(alpha = 0.3f),
                    thickness = 3.dp
                )
                Button(
                    onClick = {
                        if (content.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please add some content",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            createCommentViewModel.addCommentToDatabase(
                                content = content, postId = postId,
                                onSuccess = {
                                    createCommentSuccess.value = true
                                })
                        }
                    },
                    modifier = Modifier.width(screenWidth * 0.8f),
                    shape = Shapes.medium
                ) {
                    Text(
                        text = stringResource(R.string.post),
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
    keyboardOptions: KeyboardOptions
) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(imageVector = icon, null) },
        placeholder = { Text(
            text = label,
            style = typography.bodyLarge
        ) },
        shape = Shapes.small,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = false,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun NewPostButton(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    onClicked: () -> Unit
) {
    Button(
        onClick = onClicked,
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 8.dp,
            bottom = 8.dp
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.medium,

    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(25.dp),
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Text(
                text = stringResource(text),
                Modifier.padding(vertical = 4.dp),
                style = typography.titleMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreatePostPreview() {
    UnivibeTheme {
        CreateCommentScreen(goBackToPost = {}, postId = "")
    }
}
