package com.example.tripplanner.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tripplanner.data.entity.Accommodation
import com.example.tripplanner.data.entity.CarSharing
import com.example.tripplanner.data.entity.Event
import com.example.tripplanner.data.entity.Flight
import com.example.tripplanner.data.entity.Note
import com.example.tripplanner.data.entity.Trip

/**
 * Aggregated view of a trip with its child collections.
 */
data class TripWithRelations(
    @Embedded val trip: Trip,
    @Relation(parentColumn = "tripId", entityColumn = "tripId") val events: List<Event>,
    @Relation(parentColumn = "tripId", entityColumn = "tripId") val accommodations: List<Accommodation>,
    @Relation(parentColumn = "tripId", entityColumn = "tripId") val flights: List<Flight>,
    @Relation(parentColumn = "tripId", entityColumn = "tripId") val carShares: List<CarSharing>,
    @Relation(parentColumn = "tripId", entityColumn = "tripId") val notes: List<Note>
)
