package com.example.tripplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tripplanner.data.dao.*
import com.example.tripplanner.data.entity.*

@Database(
    entities = [User::class, Trip::class, Event::class, Accommodation::class, Flight::class, CarSharing::class, Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TripDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun eventDao(): EventDao
    abstract fun accommodationDao(): AccommodationDao
    abstract fun flightDao(): FlightDao
    abstract fun carSharingDao(): CarSharingDao
    abstract fun noteDao(): NoteDao
    abstract fun tripRelationsDao(): TripRelationsDao

    companion object {
        @Volatile
        private var INSTANCE: TripDatabase? = null

        fun getInstance(context: Context): TripDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                TripDatabase::class.java,
                "tripplanner.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
