package com.team10210.univibe.screens.createpost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.team10210.univibe.R
import com.team10210.univibe.exceptions.DataFetchException
import com.team10210.univibe.ui.theme.Purple80
import com.team10210.univibe.ui.theme.Shapes
import com.team10210.univibe.ui.theme.UnivibeTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


@Composable
fun CreatePostScreen(
    createPostViewModel: CreatePostViewModel = viewModel(),
    goBackHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var eventChecked by remember { mutableStateOf(false) }
    val eventDate = remember {mutableStateOf("")}
    val eventTime = remember {mutableStateOf("")}
    val dateOpenDialog = remember { mutableStateOf(false) }
    val timeOpenDialog = remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val singleImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )
    var isImageDisplay by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val createPostSuccess = remember { mutableStateOf(false) }

    LaunchedEffect(createPostSuccess.value) {
        if (createPostSuccess.value) {
            goBackHome()
            createPostSuccess.value = false // Reset the navigation trigger
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
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                        text = stringResource(R.string.new_post),
                        style = typography.titleLarge,
                        modifier = Modifier.padding(bottom = screenHeight * 0.015f)
                    )
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )

                    PostOrEventSwitch(
                        isChecked = eventChecked,
                        onIsCheckedChanged = {eventChecked = it}
                    )
                    TextInput(
                        value = description,
                        onValueChanged = { description = it },
                        label = stringResource(R.string.new_post_desc),
                        icon = Icons.Default.Edit,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    TextInput(
                        value = address,
                        onValueChanged = { address = it },
                        label = stringResource(R.string.new_post_addr),
                        icon = Icons.Default.LocationOn,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    // Only show if the post type is an event
                    if (eventChecked) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Button(
                                onClick = {
                                    dateOpenDialog.value = true
                                },
                                shape = Shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Purple80,
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                if (eventDate.value == "") {
                                    Text(text = stringResource(id = R.string.new_event_date))
                                } else {
                                    Text(text = eventDate.value)
                                }
                            }
                            Button(
                                onClick = {
                                    timeOpenDialog.value = true
                                },
                                shape = Shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Purple80,
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.clock),
                                    contentDescription = null
                                )
                                if (eventTime.value == "") {
                                    Text(text = stringResource(id = R.string.new_event_time))
                                } else {
                                    Text(text = eventTime.value)
                                }
                            }
                        }
                        DatePickerPage(dateOpenDialog = dateOpenDialog, date = eventDate)
                        TimePickerPage(timeOpenDialog = timeOpenDialog, time = eventTime)
                    }
                    NewPostButton(
                        icon = R.drawable.imgicon,
                        text = R.string.new_post_image,
                        onClicked = {
                            singleImageLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            isImageDisplay = true
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Divider(
                        color = Color.Black.copy(alpha = 0.3f),
                        thickness = 3.dp
                    )
                    Button(
                        onClick = {
                            if (eventChecked) {
                                if (eventDate.value.isBlank() || eventTime.value.isBlank()
                                    || description.isBlank() || address.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "One or more fields are empty",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    if (selectedImageUri != null) {
                                        createPostViewModel.saveImageToDatabaseAndGetUrl(selectedImageUri) { imageUrl, imagePath ->
                                            if (imageUrl != null) {
                                                try {
                                                    createPostViewModel
                                                        .addPostToDatabase(context, description, address, imageUrl,
                                                            imagePath, eventChecked, eventDate.value, eventTime.value,
                                                            onSuccess = {
                                                                createPostSuccess.value = true
                                                            })
                                                } catch (e: DataFetchException) {
                                                    createPostViewModel.deleteImage(imagePath)
                                                    scope.launch {
                                                        e.message?.let {
                                                            snackbarHostState.showSnackbar(
                                                                message = it,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                println("Image url not found")
                                            }
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "No image added!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            } else {
                                if (description.isBlank() || address.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "One or more fields are empty",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    if (selectedImageUri != null) {
                                        isLoading = true
                                        createPostViewModel.saveImageToDatabaseAndGetUrl(selectedImageUri) { imageUrl, imagePath ->
                                            if (imageUrl != null) {
                                                try {
                                                    createPostViewModel
                                                        .addPostToDatabase(context, description, address, imageUrl,
                                                            imagePath, eventChecked, eventDate.value, eventTime.value,
                                                            onSuccess = {
                                                                createPostSuccess.value = true
                                                            })
                                                } catch (e: DataFetchException) {
                                                    createPostViewModel.deleteImage(imagePath)
                                                    scope.launch {
                                                        e.message?.let {
                                                            snackbarHostState.showSnackbar(
                                                                message = it,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                println("Image url not found")
                                            }
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "No image added!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
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
        label = { Text(
            text = label,
            style = typography.bodyLarge
        ) },
        shape = Shapes.small,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
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

@Composable
fun PostOrEventSwitch(
    isChecked: Boolean,
    onIsCheckedChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(id = R.string.new_post_or_event),
            style = typography.bodyLarge
        )
        Switch(
            modifier = Modifier.padding(start = 8.dp),
            checked = isChecked,
            onCheckedChange = onIsCheckedChanged,
            thumbContent = if (isChecked) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerPage(
    dateOpenDialog: MutableState<Boolean>,
    date: MutableState<String>
) {
    val state = rememberDatePickerState()

    // Open date picker screen
    if (dateOpenDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                dateOpenDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateOpenDialog.value = false

                        // Convert time to readable date
                        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                        if (state.selectedDateMillis != null) {
                            val dateString = simpleDateFormat.format(state.selectedDateMillis!! + 86400*1000)
                            date.value = dateString
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dateOpenDialog.value = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(
                state = state
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    text = title,
                    style = typography.titleSmall
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerPage(
    timeOpenDialog: MutableState<Boolean>,
    time: MutableState<String>
) {
    val state = rememberTimePickerState()

    // Open time picker screen
    if (timeOpenDialog.value) {
        TimePickerDialog(
            onDismissRequest = {
                timeOpenDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        timeOpenDialog.value = false
                        var min = ""
                        min = if (state.minute < 10) {
                            "0${state.minute}"
                        } else {
                            "${state.minute}"
                        }
                        time.value = "${state.hour}:${min}"
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        timeOpenDialog.value = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            TimePicker(
                state = state
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostPreview() {
    UnivibeTheme {
        CreatePostScreen(goBackHome = {})
    }
}
