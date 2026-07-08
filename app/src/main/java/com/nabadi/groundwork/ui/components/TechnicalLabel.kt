package com.nabadi.groundwork.ui.components

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TechnicalLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
    )
}

@Preview(name = "TechnicalLabel")
@Composable
private fun TechnicalLabelPreview() {
    GroundWorkPreviewSurface {
        TechnicalLabel(text = "ASSOCIATED SITE")
    }
}

@Preview(
    name = "TechnicalLabel - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TechnicalLabelDarkPreview() {
    GroundWorkPreviewSurface {
        TechnicalLabel(text = "ASSOCIATED SITE")
    }
}
