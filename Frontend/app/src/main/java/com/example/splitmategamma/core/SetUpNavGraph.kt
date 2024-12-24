package com.example.splitmategamma.core

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.splitmategamma.auth.ui.LoginScreen
import com.example.splitmategamma.auth.ui.SignupScreen
import com.example.splitmategamma.dashboard.bill.ui.BillScreen
import com.example.splitmategamma.dashboard.bill.ui.UploadBillScreen
import com.example.splitmategamma.dashboard.bill.ui.UtilityDetailsScreen
import com.example.splitmategamma.dashboard.bill.ui.UtilityUsageScreen
import com.example.splitmategamma.dashboard.principalTenant.home.ui.PrincipalDashboardScreen
import com.example.splitmategamma.dashboard.principalTenant.house.ui.AddHouseScreen
import com.example.splitmategamma.dashboard.principalTenant.house.ui.HouseListScreen
import com.example.splitmategamma.dashboard.principalTenant.notiification.ui.PrincipalNotificationScreen
import com.example.splitmategamma.dashboard.regularTenant.home.ui.RegularDashboardScreen
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.ui.TenantManagementScreen
import com.example.splitmategamma.dashboard.principalTenant.utility.ui.RegisterUtilityScreen
import com.example.splitmategamma.dashboard.principalTenant.utility.ui.UtilityManagementScreen
import com.example.splitmategamma.dashboard.profile.ui.ProfileScreen
import com.example.splitmategamma.welcome.ui.WelcomeScreen
import com.example.splitmategamma.dashboard.regularTenant.notification.ui.RegularNotificationScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        //WELCOME
        composable(Routes.WELCOME) { WelcomeScreen(navController) }

        //AUTH
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.SIGNUP) { SignupScreen(navController) }

        //PROFILE
        composable(Routes.PROFILE) { ProfileScreen(navController) }

        //BILL
        composable(route = Routes.BILLING) { BillScreen(navController = navController) }

        //DASHBOARD
        composable(
            route = "${Routes.PRINCIPAL_DASHBOARD}/{houseId}",
            arguments = listOf(
                navArgument("houseId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId")
            PrincipalDashboardScreen(navController = navController, houseId = houseId)
        }

        composable(Routes.MANAGE_TENANTS) { TenantManagementScreen(navController) }
        composable(Routes.UTILITY_MANAGEMENT) { UtilityManagementScreen(navController = navController) }
        composable(Routes.PRINCIPAL_NOTIFICATION) { PrincipalNotificationScreen(navController = navController) }
        composable(Routes.HOUSE_LIST) { HouseListScreen(navController = navController) }
        composable(Routes.ADD_HOUSE) { AddHouseScreen(navController = navController) }
        composable(Routes.REGISTER_UTILITY) { RegisterUtilityScreen(navController = navController) }

        // Utility Usage and Details Screens
        composable(
            route = "${Routes.UTILITY_USAGE}/{selectedMonth}",
            arguments = listOf(navArgument("selectedMonth") { type = NavType.IntType })
        ) { backStackEntry ->
            val selectedMonth = backStackEntry.arguments?.getInt("selectedMonth") ?: 1
            UtilityUsageScreen(navController = navController, selectedMonth)
        }

        composable(
            route = "${Routes.UTILITY_DETAILS}/{utilityName}/{selectedMonth}",
            arguments = listOf(
                navArgument("utilityName") { type = NavType.StringType },
                navArgument("selectedMonth") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val utilityName = backStackEntry.arguments?.getString("utilityName") ?: ""
            val selectedMonth = backStackEntry.arguments?.getInt("selectedMonth") ?: 1
            UtilityDetailsScreen(navController = navController, utilityName, selectedMonth)
        }

        composable(Routes.UPLOAD_BILL){ UploadBillScreen(navController = navController) }

        composable(Routes.REGULAR_DASHBOARD) { RegularDashboardScreen(navController) }
        composable(Routes.REGULAR_NOTIFICATION) { RegularNotificationScreen(navController = navController) }
    }
}