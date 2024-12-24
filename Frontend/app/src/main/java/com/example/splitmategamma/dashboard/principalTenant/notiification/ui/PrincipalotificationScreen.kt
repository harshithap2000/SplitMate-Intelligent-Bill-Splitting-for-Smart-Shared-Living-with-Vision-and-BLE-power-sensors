package com.example.splitmategamma.dashboard.principalTenant.notiification.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.dashboard.principalTenant.notification.viewmodel.NotificationViewModelFactory
import com.example.splitmategamma.dashboard.principalTenant.notiification.model.NotificationResponse
import com.example.splitmategamma.dashboard.principalTenant.notiification.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalNotificationScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(ApiService.create()))
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    val error by viewModel.error.collectAsState()

    // Fetch notifications once the screen is displayed
    LaunchedEffect(Unit) {
        val token = PreferenceManager.getUserToken() ?: ""
        if (token.isNotEmpty()) {
            viewModel.fetchNotifications(token)
        }
    }

    // Show error if present
    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification = notification, viewModel)
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationCard(notification: NotificationResponse, viewModel: NotificationViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRead by remember { mutableStateOf(notification.status == "read") }

    // Set card and text colors based on notification type and read status
    val backgroundColor = when {
        isRead -> Color.LightGray
        notification.type == "alert" -> Color(0xFFFFEBEE) // Subtle red hue for alert notifications
        else -> Color.White
    }

    val textColor = if (notification.type == "alert") {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = notification.message,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (!isRead) {
                    TextButton(onClick = {
                        scope.launch {
                            val token = PreferenceManager.getUserToken() ?: return@launch
                            viewModel.markNotificationAsRead(token, notification._id)
                            isRead = true
                        }
                    }) {
                        Text(text = "Mark as Read", color = textColor)
                    }
                }
                TextButton(onClick = {
                    scope.launch {
                        val token = PreferenceManager.getUserToken() ?: return@launch
                        viewModel.dismissNotification(token, notification._id)
                    }
                }) {
                    Text(text = "Dismiss", color = textColor)
                }
            }
        }
    }
}