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
import com.example.tripplanner.data.entity.Accommodation
import com.example.tripplanner.data.repository.TripRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccommodationScreen(
    navController: NavHostController,
    tripId: Long
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val app = context.applicationContext as TripPlannerApp
    val repository = app.repository

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    var checkInDate by remember { mutableStateOf(LocalDate.now()) }
    var checkInTime by remember { mutableStateOf(LocalTime.of(14, 0)) }
    var checkOutDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var checkOutTime by remember { mutableStateOf(LocalTime.of(11, 0)) }

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
            CenterAlignedTopAppBar(title = { Text("Додати проживання") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(name, { name = it }, label = { Text("Назва") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(address, { address = it }, label = { Text("Адреса") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(price, { price = it }, label = { Text("Ціна") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(contact, { contact = it }, label = { Text("Контакт") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = { pickDate { checkInDate = it } }) {
                Text("Дата заїзду: $checkInDate")
            }

            Button(onClick = { pickTime { checkInTime = it } }) {
                Text("Час заїзду: $checkInTime")
            }

            Button(onClick = { pickDate { checkOutDate = it } }) {
                Text("Дата виїзду: $checkOutDate")
            }

            Button(onClick = { pickTime { checkOutTime = it } }) {
                Text("Час виїзду: $checkOutTime")
            }

            Button(
                onClick = {
                    val accommodation = Accommodation(
                        name = name,
                        address = address,
                        price = price.toDoubleOrNull() ?: 0.0,
                        contact = contact,
                        checkIn = LocalDateTime.of(checkInDate, checkInTime),
                        checkOut = LocalDateTime.of(checkOutDate, checkOutTime),
                        tripId = tripId
                    )

                    scope.launch {
                        repository.upsertAccommodation(accommodation)
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
