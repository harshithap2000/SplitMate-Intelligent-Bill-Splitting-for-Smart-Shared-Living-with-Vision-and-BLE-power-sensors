package com.example.splitmategamma.dashboard.principalTenant.tenantManagement.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.repository.TenantRepository
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.viewmodel.TenantManagementViewModel
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.viewmodel.TenantManagementViewModelFactory
import com.example.splitmategamma.network.ApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantManagementScreen(navController: NavController) {
    val context = LocalContext.current
    val tenantManagementRepository = TenantRepository(ApiService.create())
    val viewModel: TenantManagementViewModel = viewModel(
        factory = TenantManagementViewModelFactory(tenantManagementRepository)
    )

    val tenants by viewModel.tenants.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTenants()
    }

    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Manage Tenants", color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            PrincipalBottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            tenants.forEach { tenant ->
                TenantItem(
                    tenant = tenant,
                    onRemoveClicked = { viewModel.removeTenant(tenant._id) }
                )
            }
        }
    }
}

@Composable
fun TenantItem(
    tenant: Tenant,
    onRemoveClicked: () -> Unit
) {
    val isPrincipalTenant = tenant.role == "p"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)) // Light background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isPrincipalTenant) "${tenant.name} (Principal Tenant)" else tenant.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (!isPrincipalTenant) {
                Button(
                    onClick = onRemoveClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),  // Darker red color
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Remove",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) // Padding inside button
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TenantManagementScreenPreview() {
    TenantManagementScreen(navController = rememberNavController())
}