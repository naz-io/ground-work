package com.nabadi.groundwork.feature.sites.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SitesSearchAndFiltersTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun filters_showSeparateGroups_andDispatchIndependentSelections() {
        var selectedStatus: SiteStatus? = null
        var selectedPriority: SitePriority? = null
        var updatedSearchQuery: String? = null

        composeTestRule.setContent {
            GroundWorkTheme {
                SitesSearchAndFilters(
                    selectedStatus = null,
                    selectedPriority = null,
                    searchQuery = "warehouse",
                    onSearchQueryChange = { updatedSearchQuery = it },
                    onStatusFilterChange = { selectedStatus = it },
                    onPriorityFilterChange = { selectedPriority = it },
                )
            }
        }

        composeTestRule.onNodeWithText("STATUS").assertIsDisplayed()
        composeTestRule.onNodeWithText("PRIORITY").assertIsDisplayed()
        composeTestRule.onNodeWithText("ACTIVE").performClick()
        composeTestRule.onNodeWithText("URGENT").performClick()
        composeTestRule.onNodeWithContentDescription("Clear site search").performClick()

        composeTestRule.runOnIdle {
            assertEquals(SiteStatus.ACTIVE, selectedStatus)
            assertEquals(SitePriority.URGENT, selectedPriority)
            assertEquals("", updatedSearchQuery)
        }
    }
}
