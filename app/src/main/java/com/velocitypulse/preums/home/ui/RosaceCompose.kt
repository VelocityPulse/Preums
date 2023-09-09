package com.velocitypulse.preums.home.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velocitypulse.preums.home.theme.PreumsTheme

@Composable
fun DrawRosace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val rectColor = MaterialTheme.colorScheme.onPrimary
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
                .aspectRatio(1f)
                .fillMaxSize()
        ) {
            drawRect(color = rectColor, style = Stroke(3.dp.toPx()))

            val squarePrecision = 20

            val verticalLines = squarePrecision
            val verticalLength = size.width / (verticalLines + 1)
            repeat(verticalLines) { i ->
                val startX = verticalLength * (i + 1)
                val endX = (verticalLength * (verticalLines + 1)) - startX

                drawLine(
                    color = rectColor,
                    start = Offset(startX, 0f),
                    end = Offset(size.height, startX),
                    strokeWidth = 1.dp.toPx()
                )

                drawLine(
                    color = rectColor,
                    start = Offset(size.width, startX),
                    end = Offset(endX, size.width),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val horizontalLines = squarePrecision
            val horizontalLength = size.height / (horizontalLines + 1)
            repeat(horizontalLines) { i ->
                val startY = horizontalLength * (i + 1)
                val endY = (horizontalLength * (horizontalLines + 1)) - startY
                drawLine(
                    color = rectColor,
                    start = Offset(0f, startY),
                    end = Offset(startY, size.height),
                    strokeWidth = 1.dp.toPx()
                )

                drawLine(
                    color = rectColor,
                    start = Offset(0f, startY),
                    end = Offset(endY, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PreumsTheme {
        DrawRosace(Modifier.background(MaterialTheme.colorScheme.primary))
    }
}
