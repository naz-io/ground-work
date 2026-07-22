package com.nabadi.groundwork.feature.sites.list

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestSites
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SiteCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun siteCard_displaysOperationalDetails_andOpensSiteWhenClicked() {
        var openClickCount = 0
        val site = TestSites.site(
            id = "GW-4901",
            name = "North Basin Facility",
            description = "Main storage facility with recurring access issues.",
            location = "North Basin Sector A",
            priority = SitePriority.URGENT,
            status = SiteStatus.ACTIVE,
            updatedAt = 0L,
        )

        composeTestRule.setContent {
            GroundWorkTheme {
                SiteCard(
                    site = site,
                    noteCount = 12,
                    onOpenSiteClick = { openClickCount++ },
                )
            }
        }

        composeTestRule.onNodeWithText("SITE #GW-4901").assertIsDisplayed()
        composeTestRule.onNodeWithText("URGENT").assertIsDisplayed()
        composeTestRule.onNodeWithText("North Basin Facility")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        composeTestRule.onNodeWithText("North Basin Sector A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Main storage facility with recurring access issues.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("ACTIVE").assertIsDisplayed()
        composeTestRule.onNodeWithText("12 NOTES").assertIsDisplayed()
        composeTestRule.onNodeWithText("UPDATED: Unknown").assertIsDisplayed()

        composeTestRule.runOnIdle {
            assertEquals(1, openClickCount)
        }
    }
}
