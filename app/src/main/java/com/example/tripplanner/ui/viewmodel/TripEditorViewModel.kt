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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripEditorViewModel(private val repository: TripRepository) : ViewModel() {
    data class EditorState(
        val tripId: Long? = null,
        val name: String = "",
        val departure: String = "",
        val destination: String = "",
        val startDate: LocalDate? = null,
        val endDate: LocalDate? = null,
        val description: String = "",
        val participants: String = "",
        val stops: List<String> = emptyList(),
        val events: List<Event> = emptyList(),
        val flights: List<Flight> = emptyList(),
        val accommodations: List<Accommodation> = emptyList(),
        val notes: List<Note> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state

    fun loadTrip(tripWithRelations: TripWithRelations) {
        _state.value = _state.value.copy(
            tripId = tripWithRelations.trip.tripId,
            name = tripWithRelations.trip.name,
            departure = tripWithRelations.trip.departureLocation,
            destination = tripWithRelations.trip.destination,
            startDate = tripWithRelations.trip.startDate,
            endDate = tripWithRelations.trip.endDate,
            description = tripWithRelations.trip.description,
            participants = tripWithRelations.trip.participants,
            stops = tripWithRelations.trip.stops,
            events = tripWithRelations.events,
            flights = tripWithRelations.flights,
            accommodations = tripWithRelations.accommodations,
            notes = tripWithRelations.notes,
            error = null
        )
    }


    fun updateName(value: String) = update { it.copy(name = value) }
    fun updateDeparture(value: String) = update { it.copy(departure = value) }
    fun updateDestination(value: String) = update { it.copy(destination = value) }
    fun updateStartDate(value: LocalDate) = update { it.copy(startDate = value) }
    fun updateEndDate(value: LocalDate) = update { it.copy(endDate = value) }
    fun updateDescription(value: String) = update { it.copy(description = value) }
    fun updateParticipants(value: String) = update { it.copy(participants = value) }
    fun updateStops(value: List<String>) = update { it.copy(stops = value) }

    private fun update(reducer: (EditorState) -> EditorState) {
        _state.value = reducer(_state.value)
    }

    fun save(onSaved: (Long) -> Unit) {
        val snapshot = _state.value

        val validation = validate(snapshot)
        if (validation != null) {
            _state.value = snapshot.copy(error = validation)
            return
        }
        val trip = Trip(
            tripId = snapshot.tripId ?: 0,
            name = snapshot.name,
            departureLocation = snapshot.departure,
            destination = snapshot.destination,
            startDate = snapshot.startDate!!,
            endDate = snapshot.endDate!!,
            description = snapshot.description,
            participants = snapshot.participants,
            stops = snapshot.stops,
            userId = null
        )
        viewModelScope.launch {
            val id = repository.upsertTrip(trip)
            _state.value = snapshot.copy(tripId = id)
            state.value.events.forEach { event ->
                repository.upsertEvent(event.copy(tripId = id))
            }
            state.value.flights.forEach { flight ->
                repository.upsertFlight(flight.copy(tripId = id))
            }
            state.value.accommodations.forEach { acc ->
                repository.upsertAccommodation(acc.copy(tripId = id))
            }
            state.value.notes.forEach { note ->
                repository.upsertNote(note.copy(tripId = id))
            }
            onSaved(id)
        }
    }

    private fun validate(state: EditorState): String? {
        if (state.name.isBlank()) return "Trip name required"
        if (state.departure.isBlank() || state.destination.isBlank()) return "Locations required"
        if (state.startDate == null || state.endDate == null) return "Dates must be selected"
        if (state.endDate.isBefore(state.startDate)) return "End date must be after start date"
        return null
    }
}
