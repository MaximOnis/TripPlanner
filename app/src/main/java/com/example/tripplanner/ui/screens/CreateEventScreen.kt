package com.example.tripplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.ui.components.DateTimePicker
import com.example.tripplanner.ui.viewmodel.TripDetailViewModel
import java.time.LocalDateTime

@Composable
fun CreateEventScreen(
    tripId: Long,
    navController: NavHostController
) {
    val context = LocalContext.current
    val app = context.applicationContext as TripPlannerApp
    val viewModel = remember { TripDetailViewModel(app.repository) }

    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Додати подію",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Назва події") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Тип події") },
            modifier = Modifier.fillMaxWidth()
        )

        DateTimePicker(
            label = "Початок",
            dateTime = startDateTime,
            onDateTimeSelected = { startDateTime = it }
        )

        DateTimePicker(
            label = "Кінець",
            dateTime = endDateTime,
            onDateTimeSelected = { endDateTime = it }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = startDateTime != null && endDateTime != null,
            onClick = {
                viewModel.addEvent(
                    tripId = tripId,
                    title = title,
                    start = startDateTime!!,
                    end = endDateTime!!,
                    type = type
                )
                navController.popBackStack()
            }
        ) {
            Text("Зберегти")
        }
        Button(onClick = { navController.popBackStack() }) { Text("Скасувати") }
    }
}
