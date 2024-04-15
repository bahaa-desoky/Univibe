package com.team10210.univibe.screens.homepage

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.team10210.univibe.R
import com.team10210.univibe.exceptions.DataFetchException
import com.team10210.univibe.models.CommentModel
import com.team10210.univibe.models.PostModel
import com.team10210.univibe.screens.BadgeDialog
import com.team10210.univibe.ui.theme.Shapes
import kotlinx.coroutines.launch


@Composable
fun PostDetailScreen(
    postId: String,
    onAddCommentClick: (String) -> Unit,
    goBackHome: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var post by remember { mutableStateOf<PostModel?>(null) }
    var comments by remember { mutableStateOf<List<CommentModel>?>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var liked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableIntStateOf(0) }

    val firstCommentBadgeDialog = remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        val fetchedPost = homeViewModel.fetchPost(postId, onSuccess = {firstCommentBadgeDialog.value = true}) // fetch the post
        val commentsList = homeViewModel.fetchAllComments(postId) // fetch the comments
        post = fetchedPost
        comments = commentsList
        likesCount = fetchedPost!!.likes
        liked = fetchedPost.isLiked
        isLoading = false
    }

    // Alert dialog for the user's first comment
    when {
        firstCommentBadgeDialog.value -> {
            BadgeDialog(
                onConfirmation = {
                    firstCommentBadgeDialog.value = false
                },
                dialogTitle = "First Comment Badge Earned!",
                dialogText = "Congratulations! You earned a badge for commenting for the first time. Check out your badges in your profile page",
                iconUrl = "https://cdn-icons-png.flaticon.com/512/1069/1069870.png"
            )
        }
    }

    val context = LocalContext.current
    val mediaPlayer = MediaPlayer.create(context, R.raw.honk_sound)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {contentPadding ->
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
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
                post?.let {
                    LazyColumn(
                        modifier = Modifier
                            .padding(0.dp)
                    ) {
                        item {
                            AsyncImage(
                                model = it.imageUrl,
                                contentDescription = "Post Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(text = it.address, fontSize = 16.sp, style = MaterialTheme.typography.titleMedium)
                                    Text(text = it.date, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.titleSmall)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                it.username?.let { Text(text = it, fontWeight = FontWeight.Medium ,style = MaterialTheme.typography.bodyMedium) }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = it.description, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (it.event) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "When: ${it.eventDate}, ${it.eventTime}",
                                        fontSize = 15.sp,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            liked = !liked
                                            likesCount += if (liked) 1 else -1
                                            homeViewModel.updateLikes(it.id, likesCount, liked)
                                            if (liked) mediaPlayer.start()
                                        },
                                        shape = Shapes.medium
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.goose),
                                            contentDescription = "Like",
                                            modifier = Modifier.size(24.dp),
                                            tint = if (liked) Color(235,171,0) else Color.LightGray
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = likesCount.toString())
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Honks")
                                    }
                                    Button(onClick = { onAddCommentClick(postId) }, shape = Shapes.medium) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.addcomment),
                                            modifier = Modifier.size(24.dp),
                                            contentDescription = "Add Comment"
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Add Comment")
                                    }
                                }
                                if (homeViewModel.isPostFromCurrentUser(it.userId)) {
                                    Button(
                                        onClick = {
                                            try {
                                                homeViewModel.deletePost(postId, it.deleteImagePath)
                                                goBackHome()
                                            } catch (e: DataFetchException) {
                                                scope.launch {
                                                    e.message?.let {
                                                        snackbarHostState.showSnackbar(
                                                            message = it,
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        shape = Shapes.medium,
                                        colors = ButtonColors(
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            disabledContainerColor = Color.Red,
                                            disabledContentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            modifier = Modifier.size(24.dp),
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                            }
                        }

                        // Comments section
                        items(comments as List<CommentModel>) { comment ->
                            CommentItem(
                                comment = comment,
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CommentItem(
    comment: CommentModel,
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = comment.content,
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = comment.username,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
