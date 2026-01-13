package com.ocpp.sample.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ocpp.sample.ui.theme.ElectricGreen

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Default.Home),
    Charging("charging", "Charging", Icons.Default.Bolt),
    History("history", "History", Icons.Default.History),
    Settings("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        Screen.entries.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { 
                    Icon(
                        imageVector = screen.icon, 
                        contentDescription = screen.title,
                        tint = if (selected) ElectricGreen else Color.Gray
                    )
                },
                label = { 
                    Text(
                        screen.title,
                        color = if (selected) ElectricGreen else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = ElectricGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}
