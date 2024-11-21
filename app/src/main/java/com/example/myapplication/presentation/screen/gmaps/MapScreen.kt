package com.example.myapplication.presentation.screen.gmaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.myapplication.MapTypeOption
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(){
    val context = LocalContext.current
    // Infinite Learning Location
    val infiniteLearning = LatLng(1.185234585525002, 104.10199759994163)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(infiniteLearning, 20f)
    }

    var expanded by remember { mutableStateOf(false) }
    var properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true
            )
        )
    }

    val infiniteLearnings = LatLng(1.185234585525002, 104.10199759994163)
    val glints = LatLng(1.1856814467765218, 104.10192439711824)

    val routeCoordinates = listOf(
        LatLng(1.1871266771394677, 104.18824288764569),
        LatLng(1.1841681473109857, 104.18428110353128),
        LatLng(1.184988959257092, 104.10159830282369),
        LatLng(1.1852279868659894, 104.10200203014317)
    )

    var selectedMapTypeOption by remember { mutableStateOf(MapTypeOption.NORMAL) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            properties = properties.copy(isMyLocationEnabled = true)
        }
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            properties = properties.copy(isMyLocationEnabled = true)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(Unit) {
        checkLocationPermission()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maps") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {expanded = true}){
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        MapTypeOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    selectedMapTypeOption = option
                                    properties =
                                        when (selectedMapTypeOption) {
                                            MapTypeOption.NORMAL -> MapProperties(mapType = MapType.NORMAL)
                                            MapTypeOption.SATELLITE -> MapProperties(mapType = MapType.SATELLITE)
                                            MapTypeOption.HYBRID -> MapProperties(mapType = MapType.HYBRID)
                                            MapTypeOption.TERRAIN -> MapProperties(mapType = MapType.TERRAIN)
                                        }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // Map Content
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ){
            MarkerInfoWindow(
                state = MarkerState(position = infiniteLearnings),
                title = "Infinite Learning",
                snippet = "Infinite Learning"
            ) {
                Text(text = "Infinite Learning")
            }
            Marker(
                state = MarkerState(position = glints),
                title = "Glints",
                snippet = "Glints",
            )
                        Circle(
                            center = infiniteLearnings,
                            radius = 100.0,
                            fillColor = Color.Blue.copy(0.3f),
                            strokeColor = MaterialTheme.colorScheme.secondaryContainer,
                            strokeWidth = 2f
                        )
            Polyline(
                points = routeCoordinates,
                color = Color.Red,
                width = 5f
            )
        }
    }
}