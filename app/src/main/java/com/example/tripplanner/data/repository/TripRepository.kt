package com.example.tripplanner.data.repository

import com.example.tripplanner.data.dao.*
import com.example.tripplanner.data.entity.*
import com.example.tripplanner.domain.model.TripWithRelations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import com.example.tripplanner.utils.isExpired


/**
 * Repository providing CRUD operations and orchestration for related entities.
 */
class TripRepository(
    private val tripDao: TripDao,
    private val eventDao: EventDao,
    private val accommodationDao: AccommodationDao,
    private val flightDao: FlightDao,
    private val carSharingDao: CarSharingDao,
    private val noteDao: NoteDao,
    private val relationsDao: TripRelationsDao
) {
    fun trips(): Flow<List<Trip>> = tripDao.getAllTrips()

    fun searchTrips(query: String): Flow<List<Trip>> = tripDao.search(query)

    fun filterTrips(from: LocalDate, to: LocalDate): Flow<List<Trip>> = tripDao.filterByDate(from, to)

    fun trip(tripId: Long): Flow<Trip?> = tripDao.getTrip(tripId)

    fun tripRelations(tripId: Long): Flow<TripWithRelations> = relationsDao.getTripRelations(tripId)

    suspend fun upsertTrip(trip: Trip): Long = tripDao.upsertTrip(trip)

    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)

    suspend fun archiveTrip(tripId: Long, archived: Boolean) = tripDao.setArchived(tripId, archived)

    suspend fun archiveExpiredTrips() {
        val activeTrips = tripDao.getActiveTripsOnce()

        activeTrips.forEach { trip ->
            if (isExpired(trip.endDate)) {
                archiveTrip(trip.tripId, true)
            }
        }
    }

    fun getTripsByArchived(archived: Boolean): Flow<List<Trip>> =
        tripDao.getTripsByArchived(archived)

    fun eventsForTrip(tripId: Long): Flow<List<Event>> = eventDao.eventsForTrip(tripId)

    suspend fun upsertEvent(event: Event) = eventDao.upsert(event)

    suspend fun deleteEvent(event: Event) = eventDao.delete(event)

    fun accommodationsForTrip(tripId: Long): Flow<List<Accommodation>> = accommodationDao.accommodationsForTrip(tripId)

    suspend fun upsertAccommodation(accommodation: Accommodation) = accommodationDao.upsert(accommodation)

    suspend fun deleteAccommodation(accommodation: Accommodation) = accommodationDao.delete(accommodation)

    fun flightsForTrip(tripId: Long): Flow<List<Flight>> = flightDao.flightsForTrip(tripId)

    suspend fun upsertFlight(flight: Flight) = flightDao.upsert(flight)

    suspend fun deleteFlight(flight: Flight) = flightDao.delete(flight)

    fun carSharesForTrip(tripId: Long): Flow<List<CarSharing>> = carSharingDao.carSharesForTrip(tripId)

    suspend fun upsertCarShare(carSharing: CarSharing) = carSharingDao.upsert(carSharing)

    suspend fun deleteCarShare(carSharing: CarSharing) = carSharingDao.delete(carSharing)

    fun notesForTrip(tripId: Long): Flow<List<Note>> = noteDao.notesForTrip(tripId)

    suspend fun upsertNote(note: Note) = noteDao.upsert(note)

    suspend fun deleteNote(note: Note) = noteDao.delete(note)

    fun offlineCache(): Flow<List<TripWithRelations>> = tripDao.getAllTrips().combine(relationsDao.getTripRelationsStream()) { trips, _ ->
        // Placeholder: clients should call tripRelations(tripId) to observe details; this method keeps Flow type.
        trips.map { TripWithRelations(it, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()) }
    }
}
