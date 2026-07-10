package com.nabadi.groundwork.ui.components

import android.content.res.Configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nabadi.groundwork.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun GroundWorkPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val bottomStrokeWidth = dimensionResource(R.dimen.stroke_width_primary_button_bottom)
    val shape = GroundWorkShapes.Control

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.height_primary_action_button)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom Shadow / Depth Layer
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outlineVariant,
            shape = shape
        ) {}

        // Interactive Button Layer
        Button(
            onClick = {
                if (!isLoading) {
                    onClick()
                }
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.height_primary_action_button) - bottomStrokeWidth),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            elevation = null,
            contentPadding = PaddingValues(0.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensionResource(R.dimen.size_button_progress_indicator)),
                    strokeWidth = dimensionResource(R.dimen.stroke_width_button_progress_indicator),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_button_icon_text)),
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.size_button_icon)),
                        )
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge.copy(
                            letterSpacing = 1.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "GroundWorkPrimaryButton - Create Site",
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun GroundWorkPrimaryButtonCreateSitePreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "CREATE SITE",
            leadingIcon = Icons.Rounded.AddCircle,
            onClick = {},
        )
    }
}

@Preview(name = "GroundWorkPrimaryButton")
@Composable
private fun GroundWorkPrimaryButtonPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "RESET ALL FILTERS",
            onClick = {},
        )
    }
}

@Preview(
    name = "GroundWorkPrimaryButton - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkPrimaryButtonDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "RESET ALL FILTERS",
            onClick = {},
        )
    }
}

@Preview(name = "GroundWorkPrimaryButton - With Icon")
@Composable
private fun GroundWorkPrimaryButtonWithIconPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "RESET ALL FILTERS",
            leadingIcon = Icons.Filled.Refresh,
            onClick = {},
        )
    }
}

@Preview(
    name = "GroundWorkPrimaryButton - With Icon Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkPrimaryButtonWithIconDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "RESET ALL FILTERS",
            leadingIcon = Icons.Filled.Refresh,
            onClick = {},
        )
    }
}

@Preview(name = "GroundWorkPrimaryButton - Loading")
@Composable
private fun GroundWorkPrimaryButtonLoadingPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "SAVE CHANGES",
            onClick = {},
            isLoading = true,
        )
    }
}

@Preview(
    name = "GroundWorkPrimaryButton - Loading Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkPrimaryButtonLoadingDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "SAVE CHANGES",
            onClick = {},
            isLoading = true,
        )
    }
}

@Preview(name = "GroundWorkPrimaryButton - Disabled")
@Composable
private fun GroundWorkPrimaryButtonDisabledPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "SAVE CHANGES",
            onClick = {},
            enabled = false,
        )
    }
}

@Preview(
    name = "GroundWorkPrimaryButton - Disabled Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkPrimaryButtonDisabledDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkPrimaryButton(
            text = "SAVE CHANGES",
            onClick = {},
            enabled = false,
        )
    }
}