package com.team10210.univibe.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.models.CommentModel

@Composable
fun MyCommentsScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onCommentClick: (String) -> Unit,
    paddingValues: PaddingValues
) {
    profileViewModel.fetchMyComments()
    CommentList(
        comments = profileViewModel.commentList.value,
        onCommentClick = onCommentClick,
        paddingValues = paddingValues
    )
}

@Composable
fun CommentList(
    comments: List<CommentModel>,
    onCommentClick: (String) -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn (contentPadding = paddingValues){
        items(comments) { comment ->
            CommentItem(
                comment = comment,
                onClick = onCommentClick
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CommentItem(
    comment: CommentModel,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = { onClick(comment.postId) })
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