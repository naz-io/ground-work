package com.nabadi.groundwork.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nabadi.groundwork.ui.theme.GroundWorkTheme


internal const val PREVIEW_API_LEVEL = 35

@Composable
internal fun GroundWorkPreviewSurface(
    content: @Composable () -> Unit,
) {
    GroundWorkTheme {
        PreviewSurface {
            content()
        }
    }
}

@Composable
private fun PreviewSurface(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier,
        ) {
            content()
        }
    }
}
