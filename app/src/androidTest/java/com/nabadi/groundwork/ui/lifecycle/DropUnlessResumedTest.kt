package com.nabadi.groundwork.ui.lifecycle

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.navigation.GroundWorkRoute
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DropUnlessResumedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val backStack = mutableStateListOf<NavKey>(GroundWorkRoute.SitesList)
    private lateinit var openSite: (String) -> Unit

    @Before
    fun setUp() {
        composeTestRule.setContent {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<GroundWorkRoute.SitesList> {
                        val navigateToSite: (String) -> Unit = { siteId ->
                            backStack.add(GroundWorkRoute.SiteDetail(siteId = siteId))
                        }
                        openSite = navigateToSite.dropUnlessResumed()
                    }
                    entry<GroundWorkRoute.SiteDetail> {}
                },
            )
        }
    }

    @Test
    fun aTapOnAResumedScreen_navigates() {
        composeTestRule.runOnIdle { openSite("site-001") }

        composeTestRule.runOnIdle { assertEquals(1, detailScreenCount()) }
    }

    @Test
    fun aSecondTapDuringTheTransition_isDropped() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.runOnIdle { openSite("site-001") }

        // Navigation 3 moves the list screen out of RESUMED one frame after it composes the new
        // screen (about 32 ms in), so test a second tap 50 ms later: faster than a person can
        // double-tap, and still in the middle of the transition.
        composeTestRule.mainClock.advanceTimeBy(50)
        composeTestRule.runOnIdle { openSite("site-001") }

        composeTestRule.runOnIdle { assertEquals(1, detailScreenCount()) }
    }

    @Test
    fun aTapAfterTheScreenHasLeft_isDropped() {
        composeTestRule.runOnIdle { openSite("site-001") }
        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle { openSite("site-001") }

        composeTestRule.runOnIdle { assertEquals(1, detailScreenCount()) }
    }

    private fun detailScreenCount(): Int =
        backStack.count { it is GroundWorkRoute.SiteDetail }
}
