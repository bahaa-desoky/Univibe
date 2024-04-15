package com.team10210.univibe.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team10210.univibe.R
import com.team10210.univibe.ui.theme.UnivibeTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@Composable
fun OnboardingScreen(
    onHomepageButtonClicked: () -> Unit
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            count = 4,
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            // Content for each page here
            when (page) {
                0 -> PagerScreen(
                    image = R.drawable.welcome,
                    title = "Say Hello to Univibe!",
                    description = "Univibe is your ultimate companion for discovering and sharing the " +
                            "best of your university campus. Explore hidden gems, find study spots, " +
                            "and connect with fellow students like never before. "
                )
                1 -> PagerScreen(
                    image = R.drawable.share,
                    title = "Centralized Experience",
                    description = "Learn about other student's favourite things to do on campus with " +
                            "ease by sharing your thoughts, uploading photos, and inspiring others to " +
                            "explore and enjoy the unique places around campus"
                )
                2 -> PagerScreen(
                    image = R.drawable.explore,
                    title = "Your Path to Discovery",
                    description = "Discover new places, find popular hangouts, and explore campus like a pro. " +
                            "With real-time updates and user-generated content, our map is your guide " +
                            "to the best spots on campus."
                )
                else -> PagerScreen(
                    image = R.drawable.award,
                    title = "Collect Campus Treasures",
                    description = "Earn Explorer Badges as you uncover hidden gems, share insider tips, " +
                            "and engage with the Univibe community! " +
                            "Your posts and activity level determine your progress towards earning unique badges. " +
                            "Showcase your campus expertise and become a top explorer!"
                )
            }
        }
        FinishButton(
            modifier = Modifier.weight(1f),
            pagerState = pagerState,
            onClick = onHomepageButtonClicked
        )
        // Pager indicators
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)
                )
            }
        }
    }
}

@Composable
fun PagerScreen(
    image: Int,
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 18.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                painter = painterResource(id = image),
                contentScale = ContentScale.Crop,
                contentDescription = "Pager Image"
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                text = title,
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                text = description,
                style = typography.bodyLarge
            )
        }
    }
}

@Composable
fun FinishButton(
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 40.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = pagerState.currentPage == 3
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Get Started",
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    UnivibeTheme {
        OnboardingScreen(onHomepageButtonClicked = { })
    }
}
