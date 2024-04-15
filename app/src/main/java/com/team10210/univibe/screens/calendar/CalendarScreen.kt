package com.team10210.univibe.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team10210.univibe.R
import com.team10210.univibe.models.PostModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventItem(event: PostModel) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.yes_event),
            contentDescription = "Event Icon",
            modifier = Modifier.size(32.dp) // Adjust size as needed
        )
        val displayText = "${event.eventTime} - ${event.description}"
        Text(
            text = displayText,
            style = TextStyle(fontSize = 16.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun NoEventItem() {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.no_event),
            contentDescription = "No Event Icon",
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "There are no events on this date",
            style = TextStyle(fontSize = 16.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel()
) {
    val state = rememberDatePickerState()
    val selectedDate = remember { mutableStateOf<Date?>(null) }
    val events = remember { mutableStateOf<List<PostModel>>(emptyList()) }
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Observe changes and update selected date
    LaunchedEffect(state.selectedDateMillis) {
        state.selectedDateMillis?.let {
            selectedDate.value = Date(it + 86400 * 1000)
            val dateString = simpleDateFormat.format(selectedDate.value)
            // Fetch events for the selected date
            calendarViewModel.fetchEventsForDate(dateString) { fetchedEvents ->
                events.value = fetchedEvents
            }
        }
    }

    Column {
        DatePicker(
            state = state
        )
        // Display events
        LazyColumn {
            items(events.value) { event ->

                EventItem(event = event)
            }
            if (events.value.isEmpty()) {
                item { NoEventItem() }
            }
        }
    }
}
