package com.example.tripplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tripplanner.R
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.data.entity.Trip
import com.example.tripplanner.ui.navigation.Destinations
import com.example.tripplanner.ui.viewmodel.TripListViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Brightness4 // темна тема
import androidx.compose.material.icons.filled.Brightness7


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, onToggleTheme: () -> Unit, isDarkTheme: Boolean) {
    val context = LocalContext.current.applicationContext as TripPlannerApp
    val viewModel = remember { TripListViewModel(context.repository) }
    val trips by viewModel.trips.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val search = remember { mutableStateOf("") }
    val selectedTab = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Профіль", modifier = Modifier.padding(16.dp))
                Text("Сповіщення", modifier = Modifier.padding(16.dp))
                Text("Підтримка", modifier = Modifier.padding(16.dp))
                Text("Мова", modifier = Modifier.padding(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding().padding(top = 8.dp),
                    title = { Text("TripPlanner",
                        fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {coroutineScope.launch { drawerState.open()} }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDarkTheme)
                                    Icons.Default.Brightness4
                                else
                                    Icons.Default.Brightness7,
                                contentDescription = "Змінити тему"
                            )
                        }

                        IconButton(onClick = { /* profile */ }) { Icon(painterResource(id = R.drawable.ic_notification), contentDescription = null) }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab.value == 0,
                        onClick = { selectedTab.value = 0 },
                        label = { Text("Заплановані подорожі") },
                        icon = { Icon(Icons.Default.Check, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = selectedTab.value == 1,
                        onClick = { selectedTab.value = 1 },
                        label = { Text("Архів") },
                        icon = { Icon(Icons.Default.List, contentDescription = null) }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Destinations.CreateTrip.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Створити подорож")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                OutlinedTextField(
                    value = search.value,
                    onValueChange = {
                        search.value = it
                        viewModel.onSearch(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Пошук") }
                )
                TabRow(selectedTabIndex = selectedTab.value) {
                    Tab(selected = selectedTab.value == 0, onClick = { selectedTab.value = 0 }) { Text("Заплановані подорожі") }
                    Tab(selected = selectedTab.value == 1, onClick = { selectedTab.value = 1 }) { Text("Архів") }
                }
                val filtered = if (selectedTab.value == 0) trips.filter { !it.archived } else trips.filter { it.archived }
                TripList(trips = filtered, onView = { navController.navigate(Destinations.TripDetail.create(it.tripId)) })
            }
        }
    }
}

@Composable
private fun TripList(trips: List<Trip>, onView: (Trip) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(trips) { trip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(trip.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${trip.startDate} - ${trip.endDate}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(trip.destination, fontWeight = FontWeight.Bold)
                        Button(onClick = { onView(trip) }) { Text("Переглянути") }
                    }
                }
            }
        }
    }
}
