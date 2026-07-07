package com.nabadi.groundwork.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

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


@Preview(showBackground = true)
@Composable
private fun TechnicalLabelPreview() {
    GroundWorkTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TechnicalLabel(text = "ASSOCIATED SITE")
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TechnicalLabelDarkPreview() {
    GroundWorkTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TechnicalLabel(text = "ASSOCIATED SITE")
        }
    }
}
