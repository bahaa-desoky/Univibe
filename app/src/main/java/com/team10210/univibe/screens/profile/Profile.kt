package com.team10210.univibe.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.R
import com.team10210.univibe.ui.theme.Purple40
import com.team10210.univibe.ui.theme.UnivibeTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun Profile(
    profileViewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit,
    onLikedPostsClicked: () -> Unit,
    onBadgeButtonClick: () -> Unit,
    onCommentsClicked: () -> Unit,
    onMyPostsClicked: () -> Unit,
    paddingValues: PaddingValues
) {
    val currentUser = Firebase.auth.currentUser
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Box(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(
                    space = screenHeight * 0.03f,
                ),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 40.sp)) {
                            append("Hello ")
                        }
                        withStyle(style = SpanStyle(fontSize = 40.sp, color = Purple40, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
                            append("${currentUser?.displayName}!")
                        }
                    },
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlushButton(
                    text = "My liked posts",
                    onClick = { onLikedPostsClicked() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    drawable = R.drawable.goose
                )
                Divider(modifier = Modifier
                    .width(screenWidth * 0.95f)
                    .align(Alignment.CenterHorizontally))
                FlushButton(
                    text = "My posts",
                    onClick = { onMyPostsClicked() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    drawable = R.drawable.imgicon
                )
                FlushButton(
                    text = "My comments",
                    onClick = { onCommentsClicked() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    drawable = R.drawable.comment
                )
                FlushButton(
                    text = "My badges",
                    onClick = { onBadgeButtonClick() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    drawable = R.drawable.badge_icon
                )
                Spacer(modifier = Modifier.weight(1f))
                FlushButton(
                    text = "Logout",
                    onClick = {
                        profileViewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    image = Icons.AutoMirrored.Filled.ExitToApp
                )
            }
        }
    }
}


@Composable
fun FlushButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    drawable: Int? = null,
    image: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
    ) {
        if (drawable != null) {
            Icon(
                painter = painterResource(id = drawable),
                modifier = Modifier.size(24.dp),
                contentDescription = "icon",
            )
        } else if (image != null) {
            Icon(
                imageVector = image,
                modifier = Modifier.size(24.dp),
                contentDescription = "icon",
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            style = TextStyle(
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    UnivibeTheme {
        Profile(onLogout = {}, onLikedPostsClicked = {}, onCommentsClicked = {}, onBadgeButtonClick = {}, onMyPostsClicked = {}, paddingValues = PaddingValues())
    }
}
