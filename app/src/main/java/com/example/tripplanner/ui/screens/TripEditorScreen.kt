package com.example.tripplanner.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.ui.navigation.Destinations
import com.example.tripplanner.ui.viewmodel.TripEditorViewModel
import java.time.LocalDate
import java.util.Calendar
import kotlinx.coroutines.flow.first


@Composable
fun TripEditorScreen(navController: NavHostController, tripId: Long? = null) {
    val app = LocalContext.current.applicationContext as TripPlannerApp
    val viewModel = remember { TripEditorViewModel(app.repository) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(tripId) {
        if (tripId != null) {
            val tripWithRelations = app.repository.tripRelations(tripId).first()
            tripWithRelations?.let { viewModel.loadTrip(it) }
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(if (tripId == null) "Створити подорож" else "Редагувати подорож")
            Spacer(modifier = Modifier.size(12.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Назва подорожі") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words
                )
            )

            OutlinedTextField(
                value = state.departure,
                onValueChange = viewModel::updateDeparture,
                label = { Text("Звідки:") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.destination,
                onValueChange = viewModel::updateDestination,
                label = { Text("Куди:") },
                modifier = Modifier.fillMaxWidth()
            )

            DateRow("Дата початку", state.startDate) { viewModel.updateStartDate(it) }
            DateRow("Дата кінця", state.endDate) { viewModel.updateEndDate(it) }

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Опис") },
                modifier = Modifier.fillMaxWidth()
            )

            StopEditor(stops = state.stops, onChange = viewModel::updateStops)

            Spacer(modifier = Modifier.size(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { navController.popBackStack() }) { Text("Скасувати") }
                Spacer(modifier = Modifier.size(12.dp))
                Button(
                    onClick = {
                        viewModel.save { id ->
                            navController.navigate(
                                Destinations.TripDetail.create(id)
                            )
                        }
                    }
                ) {
                    Text(if (tripId == null) "Зберегти" else "Зберегти зміни")
                }
            }

            state.error?.let { Text(it) }
        }
    }
}

@Composable
private fun DateRow(label: String, value: LocalDate?, onValue: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = value?.year ?: calendar.get(Calendar.YEAR)
    val month = value?.monthValue?.minus(1) ?: calendar.get(Calendar.MONTH)
    val day = value?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    val dateText = value?.toString() ?: ""

    OutlinedTextField(
        value = dateText,
        onValueChange = {}, // readOnly → не редагується вручну
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        readOnly = true,
        trailingIcon = {
            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val selectedDate = LocalDate.of(y, m + 1, d)
                        onValue(selectedDate)
                    },
                    year, month, day
                ).show()
            }) {
                Text("Pick")
            }
        }
    )
}

@Composable
private fun StopEditor(stops: List<String>, onChange: (List<String>) -> Unit) {
    val newStop = remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Stops")
        stops.forEachIndexed { index, stop ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stop, modifier = Modifier.weight(1f))
                Button(onClick = {
                    onChange(stops.toMutableList().also { it.removeAt(index) })
                }) { Text("Видалити") }
            }
        }

        OutlinedTextField(
            value = newStop.value,
            onValueChange = { newStop.value = it },
            label = { Text("Додати зупинку") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            if (newStop.value.isNotBlank()) {
                onChange(stops + newStop.value)
                newStop.value = ""
            }
        }) { Text("Додати") }
    }
}
