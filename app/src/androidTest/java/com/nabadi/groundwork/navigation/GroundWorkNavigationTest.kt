package com.nabadi.groundwork.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroundWorkNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            NavHost(navController = navController, startDestination = GroundWorkRoute.SitesList) {
                composable<GroundWorkRoute.SitesList> {}
                composable<GroundWorkRoute.SiteDetail> {}
                composable<GroundWorkRoute.SiteEditor> {}
            }
        }
    }

    @Test
    fun popBackToSitesList_skipsTheDeletedSitesDetailScreen() {
        composeTestRule.runOnIdle {
            navController.navigate(GroundWorkRoute.SiteDetail(siteId = "site-001"))
            navController.navigate(GroundWorkRoute.SiteEditor(siteId = "site-001"))

            navController.popBackToSitesList()

            assertTrue(navController.currentDestination?.hasRoute<GroundWorkRoute.SitesList>() == true)
            assertTrue(
                navController.currentBackStack.value.none { entry ->
                    entry.destination.hasRoute<GroundWorkRoute.SiteDetail>()
                },
            )
        }
    }

    @Test
    fun popBackStack_afterSavingAnEditedSite_returnsToItsDetailScreen() {
        composeTestRule.runOnIdle {
            navController.navigate(GroundWorkRoute.SiteDetail(siteId = "site-001"))
            navController.navigate(GroundWorkRoute.SiteEditor(siteId = "site-001"))

            navController.popBackStack()

            assertTrue(navController.currentDestination?.hasRoute<GroundWorkRoute.SiteDetail>() == true)
        }
    }
}
