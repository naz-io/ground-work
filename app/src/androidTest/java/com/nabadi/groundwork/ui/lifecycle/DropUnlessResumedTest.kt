package com.nabadi.groundwork.ui.lifecycle

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.navigation.GroundWorkRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DropUnlessResumedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var openSite: (String) -> Unit

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            NavHost(navController = navController, startDestination = GroundWorkRoute.SitesList) {
                composable<GroundWorkRoute.SitesList> {
                    val navigateToSite: (String) -> Unit = { siteId ->
                        navController.navigate(GroundWorkRoute.SiteDetail(siteId = siteId))
                    }
                    openSite = navigateToSite.dropUnlessResumed()
                }
                composable<GroundWorkRoute.SiteDetail> {}
            }
        }
    }

    @Test
    fun aSecondTapDuringNavigation_isDropped() {
        composeTestRule.runOnIdle {
            openSite("site-001")
            openSite("site-001")

            val detailEntries = navController.currentBackStack.value.count { entry ->
                entry.destination.hasRoute<GroundWorkRoute.SiteDetail>()
            }
            assertEquals(1, detailEntries)
        }
    }

    @Test
    fun aTapOnAResumedScreen_navigates() {
        composeTestRule.runOnIdle {
            openSite("site-001")

            assertTrue(navController.currentDestination?.hasRoute<GroundWorkRoute.SiteDetail>() == true)
        }
    }
}
