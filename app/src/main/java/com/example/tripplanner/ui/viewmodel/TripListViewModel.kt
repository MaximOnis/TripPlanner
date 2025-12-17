package com.example.tripplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripplanner.data.entity.Trip
import com.example.tripplanner.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripListViewModel(private val repository: TripRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val dateFilter = MutableStateFlow<Pair<LocalDate?, LocalDate?>>(null to null)

    init {
        viewModelScope.launch {
            repository.archiveExpiredTrips()
        }
    }

    val trips: StateFlow<List<Trip>> = combine(
        repository.trips(),
        searchQuery,
        dateFilter
    ) { trips, query, datePair ->
        val filteredByQuery = if (query.isBlank()) trips else trips.filter {
            it.name.contains(query, ignoreCase = true) || it.destination.contains(query, ignoreCase = true)
        }
        datePair.first?.let { from ->
            datePair.second?.let { to ->
                return@combine filteredByQuery.filter { it.startDate >= from && it.endDate <= to }
            }
        }
        filteredByQuery
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearch(query: String) {
        searchQuery.value = query
    }

    fun onDateFilter(from: LocalDate?, to: LocalDate?) {
        dateFilter.value = from to to
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repository.deleteTrip(trip) }
    }
}
