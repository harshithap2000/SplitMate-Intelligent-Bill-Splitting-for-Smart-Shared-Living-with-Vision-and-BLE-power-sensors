package com.example.splitmategamma.dashboard.principalTenant.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.network.PreferenceManager
import kotlinx.coroutines.launch

@Composable
fun PrincipalBottomNavigationBar(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val selectedHouseId = PreferenceManager.getSelectedHouseId()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    val houseId = selectedHouseId ?: "null"
                    navController.navigate("principal_dashboard/$houseId") {
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ReceiptLong, contentDescription = "Bills") },
            label = { Text("Bills") },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    navController.navigate(Routes.BILLING)
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.SupervisorAccount, contentDescription = "Tenants") },
            label = { Text("Tenants") },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    navController.navigate(Routes.MANAGE_TENANTS)
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Power, contentDescription = "Utilities") },
            label = { Text("Utilities") },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    navController.navigate(Routes.UTILITY_MANAGEMENT)
                }
            }
        )
    }
}