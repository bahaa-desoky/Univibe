package com.team10210.univibe.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.team10210.univibe.R
import com.team10210.univibe.models.UniversityModel

// UW, UofT, McMaster, UofOttawa, WLU, TMU, Western, UBC, UoAlberta, McGill

val universityList = listOf(
    UniversityModel("University of Waterloo", R.drawable.uw),
    UniversityModel("University of Toronto", R.drawable.uoft),
    UniversityModel("McMaster University", R.drawable.mcmaster),
    UniversityModel("University of Ottawa", R.drawable.uofottawa),
    UniversityModel("Wilfred Laurier University", R.drawable.wlu),
    UniversityModel("Toronto Metropolitan University", R.drawable.tmu),
    UniversityModel("Western University", R.drawable.western),
    UniversityModel("University of British Columbia", R.drawable.ubc),
    UniversityModel("University of Alberta", R.drawable.uofalberta),
    UniversityModel("McGill University", R.drawable.mcgill),
)

@Composable
fun UniversityList(
    registerViewModel: RegisterViewModel,
    hideBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            item(span = {GridItemSpan(2)}) {
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                    text = stringResource(R.string.select_uni_screen),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
            items(universityList) { uni ->
                ElevatedCard(
                    modifier = Modifier
                        .clickable(onClick = {
                            registerViewModel.updateUniversity(uni.name)
                            hideBottomSheet()
                        })
                        .padding(16.dp)
                        .size(300.dp, 150.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        painter = painterResource(id = uni.image),
                        contentDescription = uni.name,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                }
            }
        }
    }
}