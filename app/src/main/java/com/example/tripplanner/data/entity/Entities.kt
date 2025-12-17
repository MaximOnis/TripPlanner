package com.example.tripplanner.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String,
    val city: String
)

@Entity(
    tableName = "trips",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Trip(
    @PrimaryKey(autoGenerate = true) val tripId: Long = 0,
    val name: String,
    val departureLocation: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String = "",
    val participants: String = "",
    val stops: List<String> = emptyList(),
    @ColumnInfo(index = true) val userId: Long? = null,
    val archived: Boolean = false
)

@Entity(
    tableName = "events",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["tripId"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Event(
    @PrimaryKey(autoGenerate = true) val eventId: Long = 0,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val eventType: String,
    @ColumnInfo(index = true) val tripId: Long
)

@Entity(
    tableName = "accommodations",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["tripId"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Accommodation(
    @PrimaryKey(autoGenerate = true) val accommodationId: Long = 0,
    val name: String,
    val address: String,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime,
    val price: Double,
    val contact: String,
    @ColumnInfo(index = true) val tripId: Long
)

@Entity(
    tableName = "flights",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["tripId"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Flight(
    @PrimaryKey(autoGenerate = true) val flightId: Long = 0,
    val airline: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val price: Double,
    val flightNumber: String,
    val seatNumber: String,
    @ColumnInfo(index = true) val tripId: Long
)

@Entity(
    tableName = "car_shares",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["tripId"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CarSharing(
    @PrimaryKey(autoGenerate = true) val carShareId: Long = 0,
    val providerName: String,
    val rentalPeriod: String,
    val vehicleInfo: String,
    val price: Double,
    val website: String,
    @ColumnInfo(index = true) val tripId: Long
)

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["tripId"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val content: String,
    val photoUri: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(index = true) val tripId: Long
)
