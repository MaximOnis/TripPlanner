package com.example.tripplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tripplanner.ui.screens.MainScreen
import com.example.tripplanner.ui.screens.TripDetailScreen
import com.example.tripplanner.ui.screens.TripEditorScreen
import com.example.tripplanner.ui.screens.TripNoteScreen
import com.example.tripplanner.ui.screens.CreateEventScreen
import com.example.tripplanner.ui.screens.CreateFlightScreen
import com.example.tripplanner.ui.screens.CreateAccommodationScreen

sealed class Destinations(val route: String) {
    object Home : Destinations("home")
    object CreateTrip : Destinations("createTrip")
    object TripDetail : Destinations("tripDetail/{tripId}") {
        fun create(tripId: Long) = "tripDetail/$tripId"
    }
    object EditTrip : Destinations("editTrip/{tripId}") {
        fun create(tripId: Long) = "editTrip/$tripId"
    }
    object Notes : Destinations("notes/{tripId}") {
        fun create(tripId: Long) = "notes/$tripId"
    }

    object AddEvent : Destinations("add_event/{tripId}") {
        fun create(tripId: Long) = "add_event/$tripId"
    }

    object AddFlight : Destinations("add_flight/{tripId}") {
        fun create(tripId: Long) = "add_flight/$tripId"
    }

    object AddAccommodation :Destinations("add_accommodation/{tripId}") {
        fun create(tripId: Long) = "add_accommodation/$tripId"
    }
}

@Composable
fun TripPlannerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    NavHost(navController = navController, startDestination = Destinations.Home.route, modifier = modifier) {
        composable(Destinations.Home.route) {
            MainScreen(navController = navController, onToggleTheme = onToggleTheme, isDarkTheme = isDarkTheme)
        }
        composable(Destinations.CreateTrip.route) {
            TripEditorScreen(navController = navController)
        }
        composable(Destinations.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable
            TripDetailScreen(navController = navController, tripId = tripId)
        }
        composable(Destinations.EditTrip.route) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable
            TripEditorScreen(navController = navController, tripId = tripId)
        }
        composable(Destinations.Notes.route) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable
            TripNoteScreen(navController = navController, tripId = tripId)
        }
        composable(Destinations.AddEvent.route) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable

            CreateEventScreen(navController = navController, tripId = tripId)
        }

        composable(Destinations.AddFlight.route) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable

            CreateFlightScreen(navController = navController, tripId = tripId)
        }

        composable(Destinations.AddAccommodation.route) { backStackEntry ->
            val tripId = backStackEntry.arguments
                ?.getString("tripId")
                ?.toLongOrNull()
                ?: return@composable

            CreateAccommodationScreen(navController = navController, tripId = tripId)
        }
    }
}
