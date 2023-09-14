package com.velocitypulse.preums.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorThemePrinter(modifier: Modifier = Modifier) {
    Column(modifier.padding(3.dp)) {
        val boxModifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
            .clip(RoundedCornerShape(1.dp))
            .border(1.dp, Color.Green)
            .weight(1f)
        Box(boxModifier.background(MaterialTheme.colorScheme.primary))
        Box(boxModifier.background(MaterialTheme.colorScheme.onPrimary))
        Box(boxModifier.background(MaterialTheme.colorScheme.primaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.onPrimaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.inversePrimary))
        Box(boxModifier.background(MaterialTheme.colorScheme.secondary))
        Box(boxModifier.background(MaterialTheme.colorScheme.onSecondary))
        Box(boxModifier.background(MaterialTheme.colorScheme.secondaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.onSecondaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.tertiary))
        Box(boxModifier.background(MaterialTheme.colorScheme.onTertiary))
        Box(boxModifier.background(MaterialTheme.colorScheme.tertiaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.onTertiaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.background))
        Box(boxModifier.background(MaterialTheme.colorScheme.onBackground))
        Box(boxModifier.background(MaterialTheme.colorScheme.surface))
        Box(boxModifier.background(MaterialTheme.colorScheme.onSurface))
        Box(boxModifier.background(MaterialTheme.colorScheme.surfaceVariant))
        Box(boxModifier.background(MaterialTheme.colorScheme.onSurfaceVariant))
        Box(boxModifier.background(MaterialTheme.colorScheme.surfaceTint))
        Box(boxModifier.background(MaterialTheme.colorScheme.inverseSurface))
        Box(boxModifier.background(MaterialTheme.colorScheme.inverseOnSurface))
        Box(boxModifier.background(MaterialTheme.colorScheme.error))
        Box(boxModifier.background(MaterialTheme.colorScheme.onError))
        Box(boxModifier.background(MaterialTheme.colorScheme.errorContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.onErrorContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.outline))
        Box(boxModifier.background(MaterialTheme.colorScheme.outlineVariant))
        Box(boxModifier.background(MaterialTheme.colorScheme.scrim))
    }
}

@Composable
fun ClearColorThemePrinter(modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(3.dp)
    ) {
        val boxModifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(1.dp, Color.Green)
            .weight(1f)
        Box(boxModifier.background(MaterialTheme.colorScheme.onPrimary))
        Box(boxModifier.background(MaterialTheme.colorScheme.primaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.inversePrimary))
        Box(boxModifier.background(MaterialTheme.colorScheme.secondary))
        Box(boxModifier.background(MaterialTheme.colorScheme.onSecondary))
        Box(boxModifier.background(MaterialTheme.colorScheme.secondaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.tertiary))
        Box(boxModifier.background(MaterialTheme.colorScheme.onTertiary))
        Box(boxModifier.background(MaterialTheme.colorScheme.tertiaryContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.background))
        Box(boxModifier.background(MaterialTheme.colorScheme.surface))
        Box(boxModifier.background(MaterialTheme.colorScheme.surfaceVariant))
        Box(boxModifier.background(MaterialTheme.colorScheme.inverseOnSurface))
        Box(boxModifier.background(MaterialTheme.colorScheme.onError))
        Box(boxModifier.background(MaterialTheme.colorScheme.errorContainer))
        Box(boxModifier.background(MaterialTheme.colorScheme.outlineVariant))
    }
}

@Composable
fun BackgroundColorPrinter(modifier: Modifier = Modifier.fillMaxSize()) {
    ExerciseGraph(modifier)
}
