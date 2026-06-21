package com.nabadi.groundwork

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nabadi.groundwork.navigation.GroundWorkRoute
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun GroundWorkBottomBar(
    selectedRoute: String?,
    onSitesClick: () -> Unit,
    onFieldNotesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        NavigationBarItem(
            selected = selectedRoute == GroundWorkRoute.SITES_LIST,
            onClick = onSitesClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = stringResource(R.string.sites_list_title),
                )
            },
            label = {
                Text(text = stringResource(R.string.sites_list_title))
            },
        )

        NavigationBarItem(
            selected = selectedRoute == GroundWorkRoute.FIELD_NOTES_LIST,
            onClick = onFieldNotesClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.NoteAlt,
                    contentDescription = stringResource(R.string.field_notes_list_title),
                )
            },
            label = {
                Text(text = stringResource(R.string.field_notes_list_title))
            },
        )
    }
}

@Preview(
    name = "Sites Selected",
    showBackground = true,
)
@Composable
private fun GroundWorkBottomBarPreview_SitesSelected() {
    GroundWorkTheme {
        GroundWorkBottomBar(
            selectedRoute = GroundWorkRoute.SITES_LIST,
            onSitesClick = {},
            onFieldNotesClick = {},
        )
    }
}

@Preview(
    name = "Field Notes Selected",
    showBackground = true,
)
@Composable
private fun GroundWorkBottomBarPreview_FieldNotesSelected() {
    GroundWorkTheme {
        GroundWorkBottomBar(
            selectedRoute = GroundWorkRoute.FIELD_NOTES_LIST,
            onSitesClick = {},
            onFieldNotesClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Sites Selected",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkBottomBarPreview_DarkMode_SitesSelected() {
    GroundWorkTheme {
        GroundWorkBottomBar(
            selectedRoute = GroundWorkRoute.SITES_LIST,
            onSitesClick = {},
            onFieldNotesClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Field Notes Selected",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkBottomBarPreview_DarkMode_FieldNotesSelected() {
    GroundWorkTheme {
        GroundWorkBottomBar(
            selectedRoute = GroundWorkRoute.FIELD_NOTES_LIST,
            onSitesClick = {},
            onFieldNotesClick = {},
        )
    }
}