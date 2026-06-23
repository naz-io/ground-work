package com.nabadi.groundwork.feature.sites

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.TestSite.site
import com.nabadi.groundwork.data.repository.FakeSitesRepository
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.list.SitesListUiState
import com.nabadi.groundwork.feature.sites.list.SitesListViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SitesListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSitesRepository()
    private lateinit var viewModel: SitesListViewModel

    @Before
    fun setup() {
        viewModel = SitesListViewModel(repository)
    }

    @Test
    fun `uiState initially emits data from repository`() = runTest {
        val sites = listOf(
            site(id = "1", name = "Site 1"),
            site(id = "2", name = "Site 2"),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            val state = skipItemsUntilLoaded()
            assertFalse(state.isLoading)
            assertEquals(sites, state.sites)
        }
    }

    @Test
    fun `onSearchQueryChange updates searchQuery and filters sites`() = runTest {
        val sites = listOf(
            site(id = "1", name = "North Warehouse"),
            site(id = "2", name = "South Yard"),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "North")

            val filteredState = awaitItem()
            assertEquals("North", filteredState.searchQuery)
            assertEquals(1, filteredState.sites.size)
            assertEquals("North Warehouse", filteredState.sites[0].name)
        }
    }

    @Test
    fun `onStatusFilterChange updates selectedStatus and filters sites`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "Active site",
                status = SiteStatus.ACTIVE,
            ),
            site(
                id = "2",
                name = "Archived site",
                status = SiteStatus.ARCHIVED,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = SiteStatus.ARCHIVED)

            val filteredState = awaitItem()
            assertEquals(SiteStatus.ARCHIVED, filteredState.selectedStatus)
            assertEquals(1, filteredState.sites.size)
            assertEquals(SiteStatus.ARCHIVED, filteredState.sites[0].status)
        }
    }

    @Test
    fun `onPriorityFilterChange updates selectedPriority and filters sites`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "Normal site",
                priority = SitePriority.NORMAL,
            ),
            site(
                id = "2",
                name = "Urgent site",
                priority = SitePriority.URGENT,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onPriorityFilterChange(priority = SitePriority.URGENT)

            val filteredState = awaitItem()
            assertEquals(SitePriority.URGENT, filteredState.selectedPriority)
            assertEquals(1, filteredState.sites.size)
            assertEquals(SitePriority.URGENT, filteredState.sites[0].priority)
        }
    }

    @Test
    fun `combined search status and priority filters work correctly`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "North Warehouse",
                status = SiteStatus.ACTIVE,
                priority = SitePriority.URGENT,
            ),
            site(
                id = "2",
                name = "North Yard",
                status = SiteStatus.COMPLETED,
                priority = SitePriority.URGENT,
            ),
            site(
                id = "3",
                name = "South Warehouse",
                status = SiteStatus.ACTIVE,
                priority = SitePriority.NORMAL,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "North")
            awaitItem()

            viewModel.onStatusFilterChange(status = SiteStatus.ACTIVE)
            awaitItem()

            viewModel.onPriorityFilterChange(priority = SitePriority.URGENT)
            val finalState = awaitItem()

            assertEquals(1, finalState.sites.size)
            assertEquals("North", finalState.searchQuery)
            assertEquals(SiteStatus.ACTIVE, finalState.selectedStatus)
            assertEquals(SitePriority.URGENT, finalState.selectedPriority)
            assertEquals("North Warehouse", finalState.sites[0].name)
        }
    }

    @Test
    fun `search query filters sites by description`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "North Gate",
                description = "Loose temporary fencing reported near the north access point.",
            ),
            site(
                id = "2",
                name = "Pump Room",
                description = "Pressure gauge needs follow-up.",
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "fencing")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.sites.size)
            assertEquals("1", filteredState.sites.first().id.value)
            assertEquals("fencing", filteredState.searchQuery)
        }
    }

    @Test
    fun `search query filters sites by location`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "North Gate",
                location = "Industrial Road",
            ),
            site(
                id = "2",
                name = "Pump Room",
                location = "Riverbend Service Road",
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "Riverbend")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.sites.size)
            assertEquals("2", filteredState.sites.first().id.value)
            assertEquals("Riverbend", filteredState.searchQuery)
        }
    }

    @Test
    fun `search query is case insensitive`() = runTest {
        val sites = listOf(
            site(id = "1", name = "North Gate Safety Check"),
            site(id = "2", name = "Pump room inspection"),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "gate")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.sites.size)
            assertEquals("North Gate Safety Check", filteredState.sites.first().name)
        }
    }

    @Test
    fun `blank search query does not filter sites`() = runTest {
        val sites = listOf(
            site(id = "1", name = "North Warehouse"),
            site(id = "2", name = "South Yard"),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "   ")

            val state = awaitItem()
            assertEquals("   ", state.searchQuery)
            assertEquals(sites, state.sites)
        }
    }

    @Test
    fun `search query with no matches emits empty sites with active criteria`() = runTest {
        val sites = listOf(
            site(id = "1", name = "North Warehouse"),
            site(id = "2", name = "South Yard"),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "Bridge")

            val state = awaitItem()
            assertEquals("Bridge", state.searchQuery)
            assertEquals(emptyList<Any>(), state.sites)
            assertFalse(state.isLoading)
            assertEquals(true, state.hasActiveCriteria)
            assertEquals(true, state.shouldShowNoMatchesState)
        }
    }


    @Test
    fun `repository updates are filtered using current search query`() = runTest {
        repository.setSites(
            listOf(
                site(id = "1", name = "North Warehouse"),
            ),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "Bridge")
            awaitItem()

            repository.setSites(
                listOf(
                    site(id = "1", name = "North Warehouse"),
                    site(id = "2", name = "River Bridge"),
                ),
            )

            val updatedState = awaitItem()
            assertEquals("Bridge", updatedState.searchQuery)
            assertEquals(1, updatedState.sites.size)
            assertEquals("River Bridge", updatedState.sites.first().name)
        }
    }

    @Test
    fun `onClearCriteriaClick clears search status and priority filters`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "North Warehouse",
                status = SiteStatus.ACTIVE,
                priority = SitePriority.URGENT,
            ),
            site(
                id = "2",
                name = "South Yard",
                status = SiteStatus.ARCHIVED,
                priority = SitePriority.NORMAL,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "North")
            awaitItem()

            viewModel.onStatusFilterChange(status = SiteStatus.ACTIVE)
            awaitItem()

            viewModel.onPriorityFilterChange(priority = SitePriority.URGENT)
            awaitItem()

            viewModel.onClearCriteriaClick()

            val clearedState = skipItemsUntilCriteriaCleared()
            assertEquals("", clearedState.searchQuery)
            assertNull(clearedState.selectedStatus)
            assertNull(clearedState.selectedPriority)
            assertEquals(sites, clearedState.sites)
        }
    }

    @Test
    fun `clearing status filter shows all sites that match remaining criteria`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "Active site",
                status = SiteStatus.ACTIVE,
            ),
            site(
                id = "2",
                name = "Archived site",
                status = SiteStatus.ARCHIVED,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = SiteStatus.ARCHIVED)
            awaitItem()

            viewModel.onStatusFilterChange(status = null)

            val clearedState = awaitItem()
            assertNull(clearedState.selectedStatus)
            assertEquals(sites, clearedState.sites)
        }
    }

    @Test
    fun `clearing priority filter shows all sites that match remaining criteria`() = runTest {
        val sites = listOf(
            site(
                id = "1",
                name = "Normal site",
                priority = SitePriority.NORMAL,
            ),
            site(
                id = "2",
                name = "Urgent site",
                priority = SitePriority.URGENT,
            ),
        )
        repository.setSites(sites)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onPriorityFilterChange(priority = SitePriority.URGENT)
            awaitItem()

            viewModel.onPriorityFilterChange(priority = null)

            val clearedState = awaitItem()
            assertNull(clearedState.selectedPriority)
            assertEquals(sites, clearedState.sites)
        }
    }

    @Test
    fun `uiState emits error message when repository fails`() = runTest {
        repository.setShouldThrowError(true)

        val errorViewModel = SitesListViewModel(repository)

        errorViewModel.uiState.test {
            val state = awaitItem()
            if (state.errorMessage == null) {
                val nextState = awaitItem()
                assertNotNull(nextState.errorMessage)
                assertEquals("Unable to load sites.", nextState.errorMessage)
            } else {
                assertNotNull(state.errorMessage)
                assertEquals("Unable to load sites.", state.errorMessage)
            }
        }
    }

    private suspend fun ReceiveTurbine<SitesListUiState>.skipItemsUntilLoaded(): SitesListUiState {
        var item = awaitItem()
        while (item.isLoading && !item.isError) {
            item = awaitItem()
        }
        return item
    }

    private suspend fun ReceiveTurbine<SitesListUiState>.skipItemsUntilCriteriaCleared(): SitesListUiState {
        var item = awaitItem()
        while (item.searchQuery.isNotEmpty() || item.selectedStatus != null || item.selectedPriority != null) {
            item = awaitItem()
        }
        return item
    }
}