package com.example.tripplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripplanner.data.entity.Accommodation
import com.example.tripplanner.data.entity.Event
import com.example.tripplanner.data.entity.Flight
import com.example.tripplanner.data.entity.Note
import com.example.tripplanner.data.entity.Trip
import com.example.tripplanner.data.repository.TripRepository
import com.example.tripplanner.domain.model.TripWithRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TripDetailViewModel(private val repository: TripRepository) : ViewModel() {
    private val selectedTripId = MutableStateFlow<Long?>(null)

    val trip: StateFlow<TripWithRelations?> = selectedTripId
        .filterNotNull()
        .flatMapLatest { repository.tripRelations(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun loadTrip(tripId: Long) {
        selectedTripId.value = tripId
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repository.deleteTrip(trip) }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun deleteFlight(flight: Flight) {
        viewModelScope.launch {
            repository.deleteFlight(flight)
        }
    }

    fun deleteAccommodation(acc: Accommodation) {
        viewModelScope.launch {
            repository.deleteAccommodation(acc)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun addEvent(
        tripId: Long,
        title: String,
        start: LocalDateTime,
        end: LocalDateTime,
        type: String
    ) = viewModelScope.launch {
        repository.upsertEvent(
            Event(
                title = title,
                startTime = start,
                endTime = end,
                eventType = type,
                tripId = tripId
            )
        )
    }

    fun addAccommodation(
        tripId: Long,
        name: String,
        address: String,
        checkIn: LocalDateTime,
        checkOut: LocalDateTime,
        price: Double,
        contact: String
    ) = viewModelScope.launch {
        repository.upsertAccommodation(
            Accommodation(
                name = name,
                address = address,
                checkIn = checkIn,
                checkOut = checkOut,
                price = price,
                contact = contact,
                tripId = tripId
            )
        )
    }

    fun addFlight(
        tripId: Long,
        airline: String,
        flightNumber: String,
        seat: String,
        departure: LocalDateTime,
        arrival: LocalDateTime,
        price: Double
    ) = viewModelScope.launch {
        repository.upsertFlight(
            Flight(
                airline = airline,
                flightNumber = flightNumber,
                seatNumber = seat,
                departureTime = departure,
                arrivalTime = arrival,
                price = price,
                tripId = tripId
            )
        )
    }
}
