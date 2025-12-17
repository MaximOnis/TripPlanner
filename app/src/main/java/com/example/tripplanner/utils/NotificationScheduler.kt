package com.example.tripplanner.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tripplanner.MainActivity
import com.example.tripplanner.R
import com.example.tripplanner.data.entity.Event
import com.example.tripplanner.data.entity.Flight
import java.time.Duration
import java.time.LocalDateTime

/**
 * Simple helper that builds local notifications for upcoming events and flights.
 */
object NotificationScheduler {
    private const val CHANNEL_ID = "tripplanner.events"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Trip Planner Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleEventNotification(context: Context, event: Event) {
        if (!shouldNotify(event.startTime)) return
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            event.eventId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming event: ${event.title}")
            .setContentText("Starts at ${event.startTime}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).notify(event.eventId.toInt(), notification)
        }
    }

    fun scheduleFlightNotification(context: Context, flight: Flight) {
        if (!shouldNotify(flight.departureTime)) return
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            flight.flightId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Flight ${flight.flightNumber} approaching")
            .setContentText("Departs at ${flight.departureTime}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).notify(flight.flightId.toInt(), notification)
        }
    }

    private fun shouldNotify(time: LocalDateTime): Boolean {
        val diff = Duration.between(LocalDateTime.now(), time)
        return diff.toHours() in 0..24
    }
}
