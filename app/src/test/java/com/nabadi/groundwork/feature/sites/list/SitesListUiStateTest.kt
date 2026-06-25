package com.nabadi.groundwork.feature.sites.list

import com.nabadi.groundwork.feature.sites.TestSite.site
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SitesListUiStateTest {

    @Test
    fun `isError is false by default`() {
        val state = SitesListUiState()

        assertFalse(state.isError)
    }

    @Test
    fun `isError is true when error message exists`() {
        val state = SitesListUiState(errorMessage = "Unable to load sites.")

        assertTrue(state.isError)
    }

    @Test
    fun `isSearching is false when search query is blank`() {
        val state = SitesListUiState(searchQuery = "   ")

        assertFalse(state.isSearching)
    }

    @Test
    fun `isSearching is true when search query is not blank`() {
        val state = SitesListUiState(searchQuery = "client")

        assertTrue(state.isSearching)
    }

    @Test
    fun `isFiltering is false by default`() {
        val state = SitesListUiState()

        assertFalse(state.isFiltering)
    }

    @Test
    fun `isFiltering is true when status filter is selected`() {
        val state = SitesListUiState(selectedStatus = SiteStatus.ACTIVE)

        assertTrue(state.isFiltering)
    }

    @Test
    fun `isFiltering is true when priority filter is selected`() {
        val state = SitesListUiState(selectedPriority = SitePriority.HIGH)

        assertTrue(state.isFiltering)
    }

    @Test
    fun `hasActiveCriteria is false by default`() {
        val state = SitesListUiState()

        assertFalse(state.hasActiveCriteria)
    }

    @Test
    fun `hasActiveCriteria is true when searching`() {
        val state = SitesListUiState(searchQuery = "client")

        assertTrue(state.hasActiveCriteria)
    }

    @Test
    fun `hasActiveCriteria is true when filtering by status`() {
        val state = SitesListUiState(selectedStatus = SiteStatus.ACTIVE)

        assertTrue(state.hasActiveCriteria)
    }

    @Test
    fun `hasActiveCriteria is true when filtering by priority`() {
        val state = SitesListUiState(selectedPriority = SitePriority.HIGH)

        assertTrue(state.hasActiveCriteria)
    }

    @Test
    fun `loading state does not show empty state no matches state or content`() {
        val state = SitesListUiState(isLoading = true)

        assertFalse(state.shouldShowEmptyState)
        assertFalse(state.shouldShowNoMatchesState)
        assertFalse(state.shouldShowContent)
    }

    @Test
    fun `error state does not show empty state no matches state or content`() {
        val state = SitesListUiState(
            isLoading = false,
            errorMessage = "Unable to load sites.",
        )

        assertFalse(state.shouldShowEmptyState)
        assertFalse(state.shouldShowNoMatchesState)
        assertFalse(state.shouldShowContent)
    }

    @Test
    fun `empty sites without active criteria shows empty state`() {
        val state = SitesListUiState(
            isLoading = false,
            sites = emptyList(),
        )

        assertTrue(state.shouldShowEmptyState)
        assertFalse(state.shouldShowNoMatchesState)
        assertFalse(state.shouldShowContent)
    }

    @Test
    fun `empty sites with search query shows no matches state and content`() {
        val state = SitesListUiState(
            isLoading = false,
            searchQuery = "client",
            sites = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
        assertTrue(state.shouldShowNoMatchesState)
        assertTrue(state.shouldShowContent)
    }

    @Test
    fun `empty sites with status filter shows no matches state and content`() {
        val state = SitesListUiState(
            isLoading = false,
            selectedStatus = SiteStatus.ACTIVE,
            sites = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
        assertTrue(state.shouldShowNoMatchesState)
        assertTrue(state.shouldShowContent)
    }

    @Test
    fun `empty sites with priority filter shows no matches state and content`() {
        val state = SitesListUiState(
            isLoading = false,
            selectedPriority = SitePriority.HIGH,
            sites = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
        assertTrue(state.shouldShowNoMatchesState)
        assertTrue(state.shouldShowContent)
    }

    @Test
    fun `non empty sites show content`() {
        val state = SitesListUiState(
            isLoading = false,
            sites = listOf(site(id = "1")),
        )

        assertFalse(state.shouldShowEmptyState)
        assertFalse(state.shouldShowNoMatchesState)
        assertTrue(state.shouldShowContent)
    }
}