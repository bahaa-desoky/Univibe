package com.team10210.univibe.screens.homepage

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.team10210.univibe.R
import com.team10210.univibe.models.PostModel
import com.team10210.univibe.ui.theme.Shapes

@Composable
fun PostList(
    posts: List<PostModel>,
    onPostClick: (String) -> Unit,
    homeViewModel: HomeViewModel,
    paddingValues: PaddingValues
) {
    LazyColumn (contentPadding = paddingValues){
        items(posts) { post ->
            PostItem(
                post = post,
                onClick = { onPostClick(post.id) },
                homeViewModel = homeViewModel
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun PostItem(
    post: PostModel,
    onClick: () -> Unit,
    homeViewModel: HomeViewModel
) {
    var liked by remember { mutableStateOf(post.isLiked) }
    var likesCount by remember { mutableIntStateOf(post.likes) }

    val context = LocalContext.current
    val mediaPlayer = MediaPlayer.create(context, R.raw.honk_sound)

    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
        ) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
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
                    Text(text = post.address, fontSize = 16.sp, style = MaterialTheme.typography.titleMedium)
                    Text(text = post.date, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.titleSmall)
                }
                Spacer(modifier = Modifier.height(12.dp))
                post.username?.let { Text(text = it, fontWeight = FontWeight.Medium ,style = MaterialTheme.typography.bodyMedium) }
                Spacer(modifier = Modifier.height(6.dp))
                val truncatedDesc = post.description.take(100)
                val desc = if (post.description.length == truncatedDesc.length) post.description else "${truncatedDesc}..."
                Text(text = desc, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                if (post.event) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When: ${post.eventDate}, ${post.eventTime}",
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
                            homeViewModel.updateLikes(post.id, likesCount, liked)
                            if (liked) mediaPlayer.start() // Play goose honk
                        },
                        shape = Shapes.medium
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.goose),
                            contentDescription = "Like",
                            modifier = Modifier.size(24.dp),
                            tint = if (liked) Color(235,171,0) else Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = likesCount.toString())
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Honks")
                    }
                    Button(onClick = onClick, shape = Shapes.medium) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Comment"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Comment")
                    }
                }
            }
        }
    }
}
