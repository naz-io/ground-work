package com.nabadi.groundwork.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.R

@Composable
fun GroundWorkFloatingActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomStrokeWidth = dimensionResource(R.dimen.stroke_width_primary_button_bottom)
    val shape = RoundedCornerShape(dimensionResource(R.dimen.radius_fab))
    val size = dimensionResource(R.dimen.size_fab)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = shape
        ) {}

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(size - bottomStrokeWidth),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            elevation = null,
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(dimensionResource(R.dimen.size_button_icon)),
            )
        }
    }
}

@Preview(name = "GroundWorkFloatingActionButton - Light")
@Composable
private fun GroundWorkFloatingActionButtonPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFloatingActionButton(
            icon = Icons.Filled.Add,
            contentDescription = "Add",
            onClick = {},
        )
    }
}

@Preview(
    name = "GroundWorkFloatingActionButton - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun GroundWorkFloatingActionButtonDarkPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFloatingActionButton(
            icon = Icons.Filled.Add,
            contentDescription = "Add",
            onClick = {},
        )
    }
}

@Preview(name = "GroundWorkFloatingActionButton - Edit")
@Composable
private fun GroundWorkFloatingActionButtonEditPreview() {
    GroundWorkPreviewSurface {
        GroundWorkFloatingActionButton(
            icon = Icons.Filled.Edit,
            contentDescription = "Edit",
            onClick = {},
        )
    }
}

@Preview(name = "GroundWorkFloatingActionButton - Save")
@Composable
private fun GroundWorkFloatingActionButtonSavePreview() {
    GroundWorkPreviewSurface {
        GroundWorkFloatingActionButton(
            icon = Icons.Filled.Save,
            contentDescription = "Save",
            onClick = {},
        )
    }
}
