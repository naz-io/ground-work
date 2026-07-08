package com.nabadi.groundwork.ui.components

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R

@Composable
fun BackButton(onBackClick: () -> Unit) {
    IconButton(
        onClick = onBackClick,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_description_back),
        )
    }
}

@Preview(
    name = "Back Button",
    showBackground = true,
)
@Composable
private fun BackButtonPreview() {
    GroundWorkPreviewSurface {
        BackButton(onBackClick = {})
    }
}

@Preview(
    name = "Back Button - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun BackButtonPreview_Dark() {
    GroundWorkPreviewSurface {
        BackButton(onBackClick = {})
    }
}
