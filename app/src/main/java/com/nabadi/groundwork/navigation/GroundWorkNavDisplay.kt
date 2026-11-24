package com.nabadi.groundwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nabadi.groundwork.feature.fieldnotes.editor.FieldNoteEditorRoute
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNotesListRoute
import com.nabadi.groundwork.feature.sites.detail.SiteDetailRoute
import com.nabadi.groundwork.feature.sites.editor.SiteEditorRoute
import com.nabadi.groundwork.feature.sites.list.SitesListRoute

@Composable
fun GroundWorkNavDisplay(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val entryProvider = entryProvider<NavKey> {
        entry<GroundWorkRoute.FieldNotesList> {
            FieldNotesListRoute(
                onFieldNoteClick = { fieldNoteId ->
                    navigator.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = fieldNoteId.value))
                },
                onAddFieldNoteClick = {
                    navigator.navigate(GroundWorkRoute.FieldNoteEditor())
                },
            )
        }

        entry<GroundWorkRoute.FieldNoteEditor> { route ->
            FieldNoteEditorRoute(
                route = route,
                onEditorFinished = navigator::goBack,
            )
        }

        entry<GroundWorkRoute.SitesList> {
            SitesListRoute(
                onOpenSiteClick = { siteId ->
                    navigator.navigate(GroundWorkRoute.SiteDetail(siteId = siteId.value))
                },
                onAddSiteClick = {
                    navigator.navigate(GroundWorkRoute.SiteEditor())
                },
            )
        }

        entry<GroundWorkRoute.SiteDetail> { route ->
            SiteDetailRoute(
                route = route,
                onBackClick = navigator::goBack,
                onEditSiteClick = { siteId ->
                    navigator.navigate(GroundWorkRoute.SiteEditor(siteId = siteId.value))
                },
                onFieldNoteClick = { fieldNoteId ->
                    navigator.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = fieldNoteId.value))
                },
                onAddFieldNoteClick = { siteId ->
                    navigator.navigate(GroundWorkRoute.FieldNoteEditor(siteId = siteId.value))
                },
            )
        }

        entry<GroundWorkRoute.SiteEditor> { route ->
            SiteEditorRoute(
                route = route,
                onEditorFinished = navigator::goBack,
                // A deleted site's detail screen has nothing left to show, so skip past it.
                onSiteDeleted = { navigator.goBackTo(GroundWorkRoute.SitesList) },
            )
        }
    }

    NavDisplay(
        entries = navigationState.toDecoratedEntries(entryProvider),
        onBack = { navigator.goBack() },
        modifier = modifier,
    )
}
