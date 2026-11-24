package com.nabadi.groundwork

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nabadi.groundwork.navigation.GroundWorkNavDisplay
import com.nabadi.groundwork.navigation.GroundWorkRoute
import com.nabadi.groundwork.navigation.Navigator
import com.nabadi.groundwork.navigation.rememberNavigationState

private val TopLevelRoutes = setOf(
    GroundWorkRoute.SitesList,
    GroundWorkRoute.FieldNotesList,
)

@Composable
fun GroundWorkApp(
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationState(
        startRoute = GroundWorkRoute.SitesList,
        topLevelRoutes = TopLevelRoutes,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }

    // The bar only shows on a tab's root screen, so tabs can't change mid-flow.
    val showBottomBar = navigationState.currentRoute in TopLevelRoutes

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                GroundWorkBottomBar(
                    selectedRoute = navigationState.topLevelRoute as? GroundWorkRoute,
                    onSitesClick = { navigator.navigate(GroundWorkRoute.SitesList) },
                    onFieldNotesClick = { navigator.navigate(GroundWorkRoute.FieldNotesList) },
                )
            }
        },
    ) { innerPadding ->
        GroundWorkNavDisplay(
            navigationState = navigationState,
            navigator = navigator,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
