package com.example.tripplanner


import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tripplanner.data.local.TripDatabase
import com.example.tripplanner.data.repository.TripRepository
import com.example.tripplanner.utils.CalendarSyncWorker
import java.util.concurrent.TimeUnit

class TripPlannerApp : Application() {
    lateinit var repository: TripRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = TripDatabase.getInstance(this)
        repository = TripRepository(
            tripDao = database.tripDao(),
            eventDao = database.eventDao(),
            accommodationDao = database.accommodationDao(),
            flightDao = database.flightDao(),
            carSharingDao = database.carSharingDao(),
            noteDao = database.noteDao(),
            relationsDao = database.tripRelationsDao()
        )

        // Schedule periodic sync with Google Calendar to keep events aligned
        val syncRequest = PeriodicWorkRequestBuilder<CalendarSyncWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "calendar_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
