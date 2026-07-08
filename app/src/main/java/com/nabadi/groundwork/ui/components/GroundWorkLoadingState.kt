package com.nabadi.groundwork.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R

@Composable
internal fun GroundWorkLoadingState(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(dimensionResource(R.dimen.size_loading_indicator)),
        strokeWidth = dimensionResource(R.dimen.stroke_width_loading_indicator),
    )
}

@Preview(name = "GroundWorkLoadingState")
@Composable
private fun GroundWorkLoadingStatePreview() {
    GroundWorkPreviewSurface {
        GroundWorkLoadingState()
    }
}

@Preview(
    name = "GroundWorkLoadingState - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkLoadingStateDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkLoadingState()
    }
}