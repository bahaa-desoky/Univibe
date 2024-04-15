package com.team10210.univibe.screens.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    paddingValues: PaddingValues,
    onMarkerClick: (String) -> Unit
) {
    var showMarkerPost by remember { mutableStateOf(false) }
    var mapItem by remember { mutableStateOf<MapItem?>(null) }
    var userUniversity by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val universityGeocodes = mapOf(
        "University of Waterloo" to LatLng(43.4723, -80.5449),
        "University of Toronto" to LatLng(43.6635, -79.3961),
        "McMaster University" to LatLng(43.2609, -79.9192),
        "University of Ottawa" to LatLng(45.4231, -75.6831),
        "Wilfred Laurier University" to LatLng(43.4738, -80.5275),
        "Toronto Metropolitan University" to LatLng(43.6577, -79.3788),
        "Western University" to LatLng(43.0096, -81.2737),
        "University of British Columbia" to LatLng(49.2606, -123.2460),
        "University of Alberta" to LatLng(53.5232, -113.5263),
        "McGill University" to LatLng(45.5048, -73.5772))

    // Get university of user to set starting camera position on map
    LaunchedEffect(Unit) {
        val fetchedUserUniversity = mapViewModel.getUserUniversity()
        userUniversity = fetchedUserUniversity
        isLoading = false
    }

    Box(
        modifier = Modifier
            .padding(paddingValues)
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
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(universityGeocodes[userUniversity]!!, 12f)
            }
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                properties = properties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                mapViewModel.fetchMapMarkers()
                // Get all posts and put them in a list
                val postMarkerList = mutableListOf<MapItem>()
                for (post in mapViewModel.postList.value) {
                    post.location?.let { LatLng(it.latitude, post.location.longitude) }?.let {
                        MapItem(
                            it,
                            "${post.description.take(20)}...",
                            post.address,
                            post.event,
                            "${post.eventDate} ${post.eventTime}",
                            post.username,
                            post.description,
                            post.imageUrl,
                            post.id
                        )
                    }?.let {
                        postMarkerList.add(
                            it
                        )
                    }
                }
                // Will cluster markers that are close together
                Clustering(
                    items = postMarkerList,
                    clusterItemContent = {
                        CustomMarker(data = it)
                    },
                    onClusterItemClick = {
                        val showPopUp = { marker: MapItem ->
                            mapItem = marker
                            showMarkerPost = true
                        }
                        showPopUp(it)
                        true
                    }
                )
            }
        }
    }
    // Pop-up for post on the map if marker dialog is clicked
    if (showMarkerPost) {
        MapMarkerDialog(
            onDismissRequest = { showMarkerPost = false },
            post = mapItem,
            onMarkerClick = onMarkerClick
        )
    }
}

@Composable
fun MapMarkerDialog(
    onDismissRequest: () -> Unit,
    post: MapItem?,
    onMarkerClick: (String) -> Unit
) {
    if (post != null) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            IconButton(
                onClick = { onDismissRequest() },
                modifier = Modifier
                    .offset(x = 5.dp, y = 5.dp)
                    .zIndex(1f)
                    .size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .offset(x = 5.dp, y = 5.dp)
                        .zIndex(1f)
                        .size(32.dp),
                    tint = Color.White
                )
            }
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onMarkerClick(post.postId)
                        onDismissRequest()
                    }
            ) {
                Column(
                    modifier = Modifier.padding(0.dp)
                ) {
                    AsyncImage(
                        model = post.postImageUrl,
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(text = post.itemSnippet, fontSize = 16.sp, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))
                        post.username?.let { Text(text = it, fontWeight = FontWeight.SemiBold ,style = MaterialTheme.typography.bodyMedium) }
                        Spacer(modifier = Modifier.height(6.dp))
                        val truncatedDesc = post.fullDescription.take(100)
                        val desc = if (post.fullDescription.length == truncatedDesc.length) post.fullDescription else "${truncatedDesc}..."
                        Text(text = desc, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (post.isEvent) {
                            Text(
                                text = "When: ${post.eventDateTime}",
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomMarker(data: MapItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.isEvent) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Blue
            )

        } else {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Red
            )

        }
    }
}

// Marker on map for clustering
data class MapItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    val isEvent: Boolean,
    val eventDateTime: String,
    val username: String?,
    val fullDescription: String,
    val postImageUrl: String,
    val postId: String
) : ClusterItem {
    override fun getPosition(): LatLng =
        itemPosition

    override fun getTitle(): String =
        itemTitle

    override fun getSnippet(): String =
        itemSnippet

    override fun getZIndex(): Float {
        return 0f
    }
}
