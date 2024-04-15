package com.team10210.univibe.screens.spinwheel

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.commandiron.spin_wheel_compose.SpinWheel
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState
import com.team10210.univibe.R
import com.team10210.univibe.screens.profile.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.UUID
import java.util.concurrent.TimeUnit


@Preview
@Composable
fun SpinWheelScreen(profileViewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedPieIndex = remember { mutableStateOf(-1) }
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    val showCongratulationsDialog = remember { mutableStateOf(false) }
    val couponCode = remember { mutableStateOf("") }
    val confettiTrigger = remember { mutableStateOf(0) }
    val showOutOfSpinsDialog = remember { mutableStateOf(false) }
    val imagesList = listOf(
        R.drawable.lightbluegift,
        R.drawable.yellowgift,
        R.drawable.redgift,
        R.drawable.greengift,
        R.drawable.darkbluegift,
        R.drawable.purplegift,
        R.drawable.lightgreengift,
        R.drawable.cosmosgift
    )
    val priceList = listOf(
        "Dominos Pizza",
        "Tacobell",
        "McDonald's",
        "Burger King",
        "Wendy's",
        "William's Café",
        "KFC",
        "Mel's Diner"
    )

    // Function to start audio playback
    fun startAudioPlayback() {
        if (mediaPlayer.value == null) {
            mediaPlayer.value = MediaPlayer.create(context, R.raw.children_yay).apply {
                setOnCompletionListener {
                    it.release()
                    mediaPlayer.value = null
                }
            }
        }
        mediaPlayer.value?.start()
    }


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val state = rememberSpinWheelState()
            val scope = rememberCoroutineScope()
            val title = if (profileViewModel.spinAvailable.value) "Win a prize!" else "Come back soon!"

            profileViewModel.fetchLastSpin()
            Text(text = title ,style = typography.displaySmall, textAlign = TextAlign.Center)
            SpinWheel(
                state = state,
                modifier = Modifier.size(300.dp),
                onClick = {

                    if (profileViewModel.spinAvailable.value) {
                        scope.launch {
                            state.animate { pieIndex ->
                                selectedPieIndex.value = pieIndex
                                couponCode.value = UUID.randomUUID().toString()
                                startAudioPlayback()
                                showCongratulationsDialog.value = true
                                confettiTrigger.value += 1
                                profileViewModel.updateLastSpin()
                            }
                        }
                    } else {
                        showOutOfSpinsDialog.value = true
                    }
                    profileViewModel.fetchLastSpin()
                }
            ) { pieIndex ->
                Image(
                    painter = painterResource(id = imagesList[pieIndex]),
                    contentDescription = "Gift $pieIndex"
                )
            }
            val spins = "Weekly spins remaining: "
            Text(
                text = spins + if (profileViewModel.spinAvailable.value) "1/1" else "0/1",
                color = if (profileViewModel.spinAvailable.value) Color.Green else Color.Red,
                style = typography.titleLarge,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            OutOfSpinsBox(showDialog = showOutOfSpinsDialog, daysLeft = 7 - profileViewModel.daysLeft.value)
        }
    }


    if (showCongratulationsDialog.value) {
        PartyFunction(confettiTrigger.value % 2 == 0) // Trigger confetti
        CongratulationsDialog(
            showDialog = showCongratulationsDialog,
            onClose = { showCongratulationsDialog.value = false },
            couponCode = couponCode.value,
            imageIndex = selectedPieIndex.value,
            imagesList = imagesList,
            priceList = priceList
        )
    }

}




@Composable
fun PartyFunction(trigger: Boolean) {
    val party = Party(
        speed = 0f,
        maxSpeed = 15f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
    )
    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = listOf(party)
    )
}
@Composable
fun OutOfSpinsBox(showDialog: MutableState<Boolean>, daysLeft: Long) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Out of Spins") },
            text = { Text("You've run out of spins. Come back later in $daysLeft days for more chances to win!") },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun CongratulationsDialog(
    showDialog: MutableState<Boolean>,
    onClose: () -> Unit,
    couponCode: String,
    imageIndex: Int,
    imagesList: List<Int>,
    priceList: List<String>
) {
    val showCopiedText = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    "Congratulations, you won!",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = imagesList[imageIndex]),
                        contentDescription = "Prize Image",
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        "You've won a ${priceList[imageIndex]} coupon!",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        "Copy your coupon code below:",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    CouponCodeBox(couponCode = couponCode)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClose() // Close the dialog
                        showDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
        LaunchedEffect(showDialog.value) {
            if (!showDialog.value) {
                showCopiedText.value = false
            }
        }
    }
}

@Composable
fun CouponCodeBox(couponCode: String) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val showCopiedText = remember { mutableStateOf(false) }
    val boxHeight = 32.dp
    val couponCodeInnerPadding = PaddingValues(horizontal = 12.dp, vertical = 1.dp)

    Row(
        modifier = Modifier
            .height(boxHeight)
            .padding()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp)),
    ) {
        Text(
            couponCode,
            fontSize = 9.sp,
            color = Color.Black,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(4.dp))
                .align(Alignment.CenterVertically)
                .padding(couponCodeInnerPadding)
                .align(Alignment.CenterVertically)
        )
        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString(couponCode))
                showCopiedText.value = true
                scope.launch {
                    delay(2000)
                    showCopiedText.value = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxHeight()

        ) {
            Image(
                painter = painterResource(id = R.drawable.copy),
                contentDescription = "Copy",
                modifier = Modifier.fillMaxHeight()
            )
        }
    }

    if (showCopiedText.value) {
        Text("Copied to clipboard!", color = Color.Green, fontSize = 14.sp)
    }
}


