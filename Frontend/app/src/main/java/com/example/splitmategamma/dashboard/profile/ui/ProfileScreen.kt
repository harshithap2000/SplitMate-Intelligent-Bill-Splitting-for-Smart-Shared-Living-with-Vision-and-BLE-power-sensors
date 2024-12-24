package com.example.splitmategamma.dashboard.profile.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.User
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.dashboard.profile.repository.ProfileRepository
import com.example.splitmategamma.dashboard.profile.viewmodel.ProfileViewModel
import com.example.splitmategamma.dashboard.profile.viewmodel.ProfileViewModelFactory
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.utils.Constants
import java.io.ByteArrayInputStream

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = ApiService.create()
    val profileRepository = ProfileRepository(apiService)
    val viewModelFactory = ProfileViewModelFactory(profileRepository)
    val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)

    var user by remember { mutableStateOf<User?>(null) }
    var house by remember { mutableStateOf<House?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile(onSuccess = { fetchedUser ->
            user = fetchedUser

            if (fetchedUser.role == Constants.ROLE_PRINCIPAL) {
                // Fetch the selected house for principal tenant
                val selectedHouseId = PreferenceManager.getSelectedHouseId()
                if (selectedHouseId != null) {
                    viewModel.fetchAllHouses(onSuccess = { houses ->
                        val matchingHouse = houses.find { it._id == selectedHouseId }
                        if (matchingHouse != null) {
                            house = matchingHouse
                        } else {
                            Toast.makeText(context, "House not found", Toast.LENGTH_SHORT).show()
                        }
                    }, onError = { errorMessage ->
                        Toast.makeText(context, "Error fetching houses: $errorMessage", Toast.LENGTH_SHORT).show()
                    })
                }
            } else {
                // Fetch house by houseId for regular tenant
                fetchedUser.houseId?.let { houseId ->
                    viewModel.fetchAllHouses(onSuccess = { houses ->
                        val matchingHouse = houses.find { it._id == houseId }
                        if (matchingHouse != null) {
                            house = matchingHouse
                        } else {
                            Toast.makeText(context, "House not found", Toast.LENGTH_SHORT).show()
                        }
                    }, onError = { errorMessage ->
                        Toast.makeText(context, "Error fetching houses: $errorMessage", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }, onError = { errorMessage ->
            Toast.makeText(context, "Error fetching user profile: $errorMessage", Toast.LENGTH_SHORT).show()
        })
    }

    user?.let { currentUser ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)) // Light grey background for the whole screen
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top AppBar with Back Button
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0E0E0)))

            // Profile Image and Name Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                currentUser.photo?.let { base64Photo ->
                    val imageBitmap = base64ToImageBitmap(base64Photo)
                    imageBitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp)) // White background for details section
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserDetailsItem(label = "Email", value = currentUser.email)
                UserDetailsItem(
                    label = "Role",
                    value = if (currentUser.role == Constants.ROLE_PRINCIPAL) "Principal Tenant" else "Regular Tenant"
                )
                house?.let { currentHouse ->
                    UserDetailsItem(label = "House Name", value = currentHouse.name)
                    UserDetailsItem(label = "House Address", value = currentHouse.address)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Out Button
            Button(
                onClick = {
                    Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(50)) // Rounded button shape
            ) {
                Text(text = "Sign Out", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // Read EXIF data from the byte array
        val exifInterface = ExifInterface(ByteArrayInputStream(decodedBytes))
        val correctedBitmap = bitmap?.let { rotateBitmapIfNeeded(it, exifInterface) }

        correctedBitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

fun rotateBitmapIfNeeded(bitmap: Bitmap, exifInterface: ExifInterface): Bitmap {
    val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val rotationAngle = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    val matrix = android.graphics.Matrix()
    matrix.postRotate(rotationAngle)

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun UserDetailsItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                fontSize = 14.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = NavController(LocalContext.current))
}