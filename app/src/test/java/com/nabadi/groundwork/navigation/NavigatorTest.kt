package com.nabadi.groundwork.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigatorTest {

    private val state = NavigationState(
        startRoute = GroundWorkRoute.SitesList,
        topLevelRoute = mutableStateOf(GroundWorkRoute.SitesList),
        backStacks = mapOf(
            GroundWorkRoute.SitesList to NavBackStack<NavKey>(GroundWorkRoute.SitesList),
            GroundWorkRoute.FieldNotesList to NavBackStack<NavKey>(GroundWorkRoute.FieldNotesList),
        ),
    )
    private val navigator = Navigator(state)

    @Test
    fun `navigate opens a screen on the current tab`() {
        navigator.navigate(GroundWorkRoute.SiteDetail(siteId = "site-001"))

        assertEquals(GroundWorkRoute.SitesList, state.topLevelRoute)
        assertEquals(GroundWorkRoute.SiteDetail(siteId = "site-001"), state.currentRoute)
    }

    @Test
    fun `goBack after saving an edited site returns to its detail screen`() {
        navigator.navigate(GroundWorkRoute.SiteDetail(siteId = "site-001"))
        navigator.navigate(GroundWorkRoute.SiteEditor(siteId = "site-001"))

        navigator.goBack()

        assertEquals(GroundWorkRoute.SiteDetail(siteId = "site-001"), state.currentRoute)
    }

    @Test
    fun `goBackTo the sites list skips the deleted site's detail screen`() {
        navigator.navigate(GroundWorkRoute.SiteDetail(siteId = "site-001"))
        navigator.navigate(GroundWorkRoute.SiteEditor(siteId = "site-001"))

        navigator.goBackTo(GroundWorkRoute.SitesList)

        assertEquals(
            listOf<NavKey>(GroundWorkRoute.SitesList),
            state.backStacks.getValue(GroundWorkRoute.SitesList).toList(),
        )
    }

    @Test
    fun `goBackTo a route that is not on this tab only closes the current screen`() {
        navigator.navigate(GroundWorkRoute.FieldNotesList)
        navigator.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = "note-001"))

        navigator.goBackTo(GroundWorkRoute.SitesList)

        assertEquals(GroundWorkRoute.FieldNotesList, state.currentRoute)
    }

    @Test
    fun `navigating to a tab switches tabs without adding a screen`() {
        navigator.navigate(GroundWorkRoute.FieldNotesList)

        assertEquals(GroundWorkRoute.FieldNotesList, state.topLevelRoute)
        assertEquals(GroundWorkRoute.FieldNotesList, state.currentRoute)
        assertEquals(1, state.backStacks.getValue(GroundWorkRoute.FieldNotesList).size)
    }

    @Test
    fun `each tab keeps its own screens`() {
        navigator.navigate(GroundWorkRoute.FieldNotesList)
        navigator.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = "note-001"))
        navigator.navigate(GroundWorkRoute.SitesList)

        assertEquals(GroundWorkRoute.SitesList, state.currentRoute)

        navigator.navigate(GroundWorkRoute.FieldNotesList)

        assertEquals(GroundWorkRoute.FieldNoteEditor(fieldNoteId = "note-001"), state.currentRoute)
    }

    @Test
    fun `goBack from another tab's root returns to the start tab`() {
        navigator.navigate(GroundWorkRoute.FieldNotesList)

        navigator.goBack()

        assertEquals(GroundWorkRoute.SitesList, state.topLevelRoute)
        assertEquals(GroundWorkRoute.SitesList, state.currentRoute)
    }
}
