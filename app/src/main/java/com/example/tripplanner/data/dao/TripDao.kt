package com.example.tripplanner.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tripplanner.data.entity.*
import com.example.tripplanner.domain.model.TripWithRelations
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): Flow<User?>
}

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips ORDER BY startDate ASC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE tripId = :tripId")
    fun getTrip(tripId: Long): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE name LIKE '%' || :query || '%' OR destination LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE startDate >= :from AND endDate <= :to")
    fun filterByDate(from: LocalDate, to: LocalDate): Flow<List<Trip>>

    @Query("UPDATE trips SET archived = :archived WHERE tripId = :tripId")
    suspend fun setArchived(tripId: Long, archived: Boolean)

    @Query("SELECT * FROM trips WHERE archived = :archived")
    fun getTripsByArchived(archived: Boolean): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE archived = 0")
    suspend fun getActiveTripsOnce(): List<Trip>
}

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: Event): Long

    @Query("SELECT * FROM events WHERE tripId = :tripId")
    fun eventsForTrip(tripId: Long): Flow<List<Event>>

    @Delete
    suspend fun delete(event: Event)
}

@Dao
interface AccommodationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(accommodation: Accommodation): Long

    @Query("SELECT * FROM accommodations WHERE tripId = :tripId")
    fun accommodationsForTrip(tripId: Long): Flow<List<Accommodation>>

    @Delete
    suspend fun delete(accommodation: Accommodation)
}

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(flight: Flight): Long

    @Query("SELECT * FROM flights WHERE tripId = :tripId")
    fun flightsForTrip(tripId: Long): Flow<List<Flight>>

    @Delete
    suspend fun delete(flight: Flight)
}

@Dao
interface CarSharingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(carSharing: CarSharing): Long

    @Query("SELECT * FROM car_shares WHERE tripId = :tripId")
    fun carSharesForTrip(tripId: Long): Flow<List<CarSharing>>

    @Delete
    suspend fun delete(carSharing: CarSharing)
}

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: Note): Long

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE tripId = :tripId ORDER BY createdAt DESC")
    fun notesForTrip(tripId: Long): Flow<List<Note>>
}

@Dao
interface TripRelationsDao {
    @Transaction
    @Query("SELECT * FROM trips WHERE tripId = :tripId")
    fun getTripRelations(tripId: Long): Flow<TripWithRelations>

    @Transaction
    @Query("SELECT * FROM trips")
    fun getTripRelationsStream(): Flow<List<TripWithRelations>>
}

