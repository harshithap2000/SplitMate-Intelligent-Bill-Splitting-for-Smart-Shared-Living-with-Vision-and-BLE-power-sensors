package com.example.splitmategamma.dashboard.regularTenant.notification.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularNotificationScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        content = { paddingValues ->
            // Debug log to check if this composable is getting recomposed
            println("NotificationScreen content called")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Debug text to ensure the screen is loaded
                Text(text = "Notification Screen Loaded", modifier = Modifier.padding(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    val notifications = getMockNotifications() // Fetch notifications
                    if (notifications.isEmpty()) {
                        item {
                            Text("No notifications available", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        items(notifications) { notification ->
                            NotificationItem(notification)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = notification.message,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = notification.date,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

fun getMockNotifications(): List<Notification> {
    return listOf(
        Notification(title = "New Bill Available", message = "Your new utility bill for September is available.", date = "Sep 10, 2024"),
        Notification(title = "Payment Reminder", message = "Your utility payment is due in 3 days.", date = "Sep 5, 2024"),
        Notification(title = "Maintenance Alert", message = "Scheduled maintenance on Sep 15, 2024.", date = "Sep 1, 2024"),
        Notification(title = "Welcome to SplitMate", message = "Thanks for joining SplitMate. Start managing your utilities now!", date = "Aug 30, 2024")
    )
}

data class Notification(
    val title: String,
    val message: String,
    val date: String
)

@Preview(showBackground = true)
@Composable
fun RegularNotificationScreenPreview() {
    RegularNotificationScreen(navController = NavController(LocalContext.current))
}
