package com.nabadi.groundwork.ui.components

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GroundWorkFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        },
        shape = GroundWorkShapes.Control,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Preview(name = "GroundWorkFilterChip - Selected")
@Composable
private fun GroundWorkFilterChipSelectedPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = true,
            onClick = {},
            label = "Active",
        )
    }
}

@Preview(
    name = "GroundWorkFilterChip - Selected Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkFilterChipSelectedDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = true,
            onClick = {},
            label = "Active",
        )
    }
}

@Preview(name = "GroundWorkFilterChip - Unselected")
@Composable
private fun GroundWorkFilterChipUnselectedPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Drafts",
        )
    }
}

@Preview(
    name = "GroundWorkFilterChip - Unselected Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkFilterChipUnselectedDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Drafts",
        )
    }
}

@Preview(name = "GroundWorkFilterChip - Long Label")
@Composable
private fun GroundWorkFilterChipLongLabelPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Recently Updated Field Notes",
        )
    }
}

@Preview(
    name = "GroundWorkFilterChip - Long Label Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkFilterChipLongLabelDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Recently Updated Field Notes",
        )
    }
}

@Preview(name = "GroundWorkFilterChip - Disabled")
@Composable
private fun GroundWorkFilterChipDisabledPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Archived",
            enabled = false,
        )
    }
}

@Preview(
    name = "GroundWorkFilterChip - Disabled Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkFilterChipDisabledDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFilterChip(
            selected = false,
            onClick = {},
            label = "Archived",
            enabled = false,
        )
    }
}
