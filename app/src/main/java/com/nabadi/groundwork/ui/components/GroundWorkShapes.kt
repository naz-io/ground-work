package com.nabadi.groundwork.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import com.nabadi.groundwork.R

object GroundWorkShapes {
    val Control: RoundedCornerShape
        @Composable get() = RoundedCornerShape(
            dimensionResource(R.dimen.radius_control),
            )
}