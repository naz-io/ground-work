package com.nabadi.groundwork

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nabadi.groundwork.navigation.GroundWorkNavHost
import com.nabadi.groundwork.navigation.GroundWorkRoute

@Composable
fun GroundWorkApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val shouldShowBottomBar = currentRoute in setOf(
        GroundWorkRoute.SITES_LIST,
        GroundWorkRoute.FIELD_NOTES_LIST,
    )

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (shouldShowBottomBar) {
                GroundWorkBottomBar(
                    selectedRoute = currentRoute,
                    onSitesClick = {
                        navController.navigateToTopLevelDestination(GroundWorkRoute.SITES_LIST)
                    },
                    onFieldNotesClick = {
                        navController.navigateToTopLevelDestination(GroundWorkRoute.FIELD_NOTES_LIST)
                    },
                )
            }
        },
    ) { innerPadding ->
        GroundWorkNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private fun NavHostController.navigateToTopLevelDestination(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
    }
}
