package com.example.tripplanner.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.ui.navigation.Destinations
import com.example.tripplanner.ui.viewmodel.TripDetailViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.tripplanner.ui.utils.geocode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.model.BitmapDescriptorFactory


@Composable
fun TripDetailScreen(
    navController: NavHostController,
    tripId: Long
) {
    val context = LocalContext.current
    val app = context.applicationContext as TripPlannerApp
    val viewModel = remember { TripDetailViewModel(app.repository) }
    val tripWithRelations by viewModel.trip.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    tripWithRelations?.let { data ->
        val trip = data.trip
        val context = LocalContext.current

        val startLatLng = remember(trip.departureLocation) {
            geocode(context, trip.departureLocation)
        }

        val endLatLng = remember(trip.destination) {
            geocode(context, trip.destination)
        }

        val stopsLatLng = remember(trip.stops) {
            trip.stops.mapNotNull { geocode(context, it) }
        }

        val routeText = remember(trip) {
            buildList {
                add(trip.departureLocation)
                addAll(trip.stops)
                add(trip.destination)
            }.joinToString(" → ")
        }

        val routePoints = remember(startLatLng, endLatLng, stopsLatLng) {
            buildList {
                startLatLng?.let { add(it) }
                addAll(stopsLatLng)
                endLatLng?.let { add(it) }
            }
        }

        val cameraPositionState = rememberCameraPositionState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Заголовок та маршрут
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(Destinations.Home.route) {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "На головний екран"
                        )
                    }

                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }


                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${trip.startDate} – ${trip.endDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Маршрут:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = routeText,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Карта
            item {
                Spacer(Modifier.height(12.dp))
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState
                ) {

                    startLatLng?.let {
                        Marker(
                            state = MarkerState(it),
                            title = "Старт",
                            snippet = trip.departureLocation,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                    }

                    endLatLng?.let {
                        Marker(
                            state = MarkerState(it),
                            title = "Ціль",
                            snippet = trip.destination,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                    }

                    stopsLatLng.forEachIndexed { index, pos ->
                        Marker(
                            state = MarkerState(pos),
                            title = "Зупинка ${index + 1}",
                            snippet = trip.stops[index],
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE
                            )
                        )
                    }

                    if (routePoints.size > 1) {
                        Polyline(
                            points = routePoints,
                            color = MaterialTheme.colorScheme.primary,
                            width = 8f
                        )
                    }
                }
            }

            // Опис подорожі
            if (trip.description.isNotBlank()) {
                item {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Опис",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = trip.description,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Події
            sectionHeaderWithAdd(
                title = "Події",
                onAddClick = {
                    navController.navigate(Destinations.AddEvent.create(trip.tripId))
                }
            )
            items(data.events) { event ->
                EventCard(
                    event = event,
                    onDelete = { viewModel.deleteEvent(event) }
                )
            }

            // Перельоти
            sectionHeaderWithAdd(
                title = "Перельоти",
                onAddClick = {
                    navController.navigate(Destinations.AddFlight.create(trip.tripId))
                }
            )
            items(data.flights) { flight ->
                FlightCard(
                    flight,
                    onDelete = { viewModel.deleteFlight(flight) }
                )
            }

            // Проживання
            sectionHeaderWithAdd(
                title = "Проживання",
                onAddClick = {
                    navController.navigate(Destinations.AddAccommodation.create(trip.tripId))
                }
            )
            items(data.accommodations) { acc ->
                AccommodationCard(
                    acc = acc,
                    onDelete = { viewModel.deleteAccommodation(acc) }
                )
            }

            // Нотатки
            sectionHeaderWithAdd(
                title = "Нотатки",
                onAddClick = {
                    navController.navigate(Destinations.Notes.create(trip.tripId))
                }
            )
            items(data.notes) { note ->
                InfoCard(
                    text = note.content,
                    onDelete = { viewModel.deleteNote(note) }
                )
            }

            // Action Buttons
            item {
                Spacer(Modifier.height(16.dp))
                ActionButtons(
                    onEdit = {
                        navController.navigate(
                            Destinations.EditTrip.create(trip.tripId)
                        )
                    },
                    onDelete = {
                        viewModel.deleteTrip(trip)
                        navController.popBackStack()
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join my trip to ${trip.destination} from ${trip.startDate} to ${trip.endDate}"
                            )
                        }
                        context.startActivity(
                            Intent.createChooser(intent, "Поділитись")
                        )
                    }
                )
            }

        }

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/* ---------- UI COMPONENTS ---------- */

@Composable
private fun ActionButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Column {
        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text("Редагувати")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Видалити")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
            Text("Поділитись")
        }
    }
}

@Composable
private fun InfoCard(text: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EventCard(event: com.example.tripplanner.data.entity.Event, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(event.title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Видалити",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text("Тип: ${event.eventType}")
            Text("Початок: ${event.startTime}")
            Text("Кінець: ${event.endTime}")
        }
    }
}

@Composable
private fun FlightCard(flight: com.example.tripplanner.data.entity.Flight, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${flight.flightNumber} • ${flight.airline}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            Text("Виліт: ${flight.departureTime}")
            Text("Приліт: ${flight.arrivalTime}")
        }
    }
}

@Composable
private fun AccommodationCard(acc: com.example.tripplanner.data.entity.Accommodation, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(acc.name, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            Text("Адреса: ${acc.address}")
            Text("Заїзд: ${acc.checkIn} | Виїзд: ${acc.checkOut}")
        }
    }
}


/* ---------- Section Headers ---------- */

private fun LazyListScope.sectionHeader(title: String) {
    item {
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

private fun LazyListScope.sectionHeaderWithAdd(
    title: String,
    onAddClick: () -> Unit
) {
    item {
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Додати"
                )
            }
        }
    }
}

