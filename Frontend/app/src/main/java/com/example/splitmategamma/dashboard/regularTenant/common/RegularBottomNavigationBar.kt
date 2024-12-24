package com.example.splitmategamma.dashboard.regularTenant.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
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

@Composable
fun RegularBottomNavigationBar(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 8.dp
    ) {
        val items = listOf("Home", "Bills", "Profile")
        val icons = listOf(
            Icons.Filled.Home,
            Icons.Filled.ReceiptLong,
            Icons.Filled.Person
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = false,
                onClick = {
                    when (item) {
                        "Home" -> navController.navigate(Routes.REGULAR_DASHBOARD)
                        "Bills" -> navController.navigate(Routes.BILLING)
                        "Profile" -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }
    }
}