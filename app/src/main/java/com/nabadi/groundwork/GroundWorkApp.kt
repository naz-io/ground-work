package com.nabadi.groundwork

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
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
    val currentDestination = currentBackStack?.destination
    val selectedTopLevelRoute = when {
        currentDestination?.hasRoute<GroundWorkRoute.SitesList>() == true -> GroundWorkRoute.SitesList
        currentDestination?.hasRoute<GroundWorkRoute.FieldNotesList>() == true -> GroundWorkRoute.FieldNotesList
        else -> null
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (selectedTopLevelRoute != null) {
                GroundWorkBottomBar(
                    selectedRoute = selectedTopLevelRoute,
                    onSitesClick = {
                        navController.navigateToTopLevelDestination(GroundWorkRoute.SitesList)
                    },
                    onFieldNotesClick = {
                        navController.navigateToTopLevelDestination(GroundWorkRoute.FieldNotesList)
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

private fun NavHostController.navigateToTopLevelDestination(route: GroundWorkRoute) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
    }
}
