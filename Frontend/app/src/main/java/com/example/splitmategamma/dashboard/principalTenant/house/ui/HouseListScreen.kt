package com.example.splitmategamma.dashboard.principalTenant.house.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.home.repository.PrincipalRepository
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModel
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModelFactory
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseListScreen(navController: NavController) {
    val context = LocalContext.current
    PreferenceManager.initialize(context)

    val principalRepository = PrincipalRepository(ApiService.create())
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val viewModel: PrincipalViewModel = ViewModelProvider(
        viewModelStoreOwner!!,
        PrincipalViewModelFactory(principalRepository)
    )[PrincipalViewModel::class.java]

    val houses by viewModel.houses.collectAsState(initial = emptyList())
    val token = PreferenceManager.getUserToken()

    LaunchedEffect(Unit) {
        viewModel.fetchHouses(token)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Select House",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_house") }) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add House")
                    }
                    IconButton(onClick = { navController.navigate("principal_notification") }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.shadow(8.dp)
            )
        },
        bottomBar = {
            PrincipalBottomNavigationBar(navController)
        },
        content = { paddingValues ->
            if (houses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No houses available. Please add a new house.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(houses) { house ->
                        HouseItem(house = house, onClick = {
                            PreferenceManager.saveSelectedHouseId(house._id) // Save selected house ID
                            Log.d("HouseListScreen", "Selected house ID: ${house._id}")
                            navController.navigate("principal_dashboard/${house._id}")
                        })
                    }
                }
            }
        }
    )
}

@Composable
fun HouseItem(house: House, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = house.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = house.address ?: "No address available", fontSize = 16.sp)
        }
    }
}
