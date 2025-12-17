package com.example.tripplanner.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.data.entity.Flight
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlightScreen(
    navController: NavHostController,
    tripId: Long
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val app = context.applicationContext as TripPlannerApp
    val repository = app.repository

    var airline by remember { mutableStateOf("") }
    var flightNumber by remember { mutableStateOf("") }
    var seat by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var depDate by remember { mutableStateOf(LocalDate.now()) }
    var depTime by remember { mutableStateOf(LocalTime.now()) }
    var arrDate by remember { mutableStateOf(LocalDate.now()) }
    var arrTime by remember { mutableStateOf(LocalTime.now().plusHours(2)) }

    val calendar = Calendar.getInstance()

    fun pickDate(onPick: (LocalDate) -> Unit) {
        DatePickerDialog(
            context,
            { _, y, m, d -> onPick(LocalDate.of(y, m + 1, d)) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime(onPick: (LocalTime) -> Unit) {
        TimePickerDialog(
            context,
            { _, h, m -> onPick(LocalTime.of(h, m)) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Додати переліт") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(airline, { airline = it }, label = { Text("Авіакомпанія") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(flightNumber, { flightNumber = it }, label = { Text("Номер рейсу") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(seat, { seat = it }, label = { Text("Місце") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(price, { price = it }, label = { Text("Ціна") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = { pickDate { depDate = it } }) {
                Text("Дата вильоту: $depDate")
            }

            Button(onClick = { pickTime { depTime = it } }) {
                Text("Час вильоту: $depTime")
            }

            Button(onClick = { pickDate { arrDate = it } }) {
                Text("Дата прибуття: $arrDate")
            }

            Button(onClick = { pickTime { arrTime = it } }) {
                Text("Час прибуття: $arrTime")
            }

            Button(
                onClick = {
                    val flight = Flight(
                        airline = airline,
                        flightNumber = flightNumber,
                        seatNumber = seat,
                        price = price.toDoubleOrNull() ?: 0.0,
                        departureTime = LocalDateTime.of(depDate, depTime),
                        arrivalTime = LocalDateTime.of(arrDate, arrTime),
                        tripId = tripId
                    )

                    scope.launch {
                        repository.upsertFlight(flight)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти")
            }
            Button(onClick = { navController.popBackStack() }) { Text("Скасувати") }
        }
    }
}
