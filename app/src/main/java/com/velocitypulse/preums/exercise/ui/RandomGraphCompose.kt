package com.velocitypulse.preums.exercise.ui

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun RandomGraphCompose(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val rectColor = MaterialTheme.colorScheme.secondary
        val primary = MaterialTheme.colorScheme.primary
        Spacer(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .drawWithCache {
                    val path = generateSmoothPath(getData(), size)

                    val filledPath = Path()
                    filledPath.addPath(path)
                    filledPath.lineTo(size.width, size.height)
                    filledPath.lineTo(0f, size.height)
                    filledPath.close()

                    val brush = Brush.verticalGradient(
                        listOf(primary.copy(alpha = 0.9f), Color.Transparent)
                    )

                    onDrawBehind {
                        drawBackgroundGraph(this, rectColor)
                        drawPath(filledPath, brush)
                        drawPath(path, primary, style = Stroke(2.dp.toPx()))
                    }
                }
        )
    }
}

fun generatePath(graphData: List<Balance>, graphSize: Size): Path {
    val path = Path()
    val numberEntries = graphData.size - 1
    val weekWidth = graphSize.width / numberEntries

    val max = graphData.maxBy { it.amount }
    val min = graphData.minBy { it.amount }
    val range = max.amount - min.amount
    val heightPxPerAmount = graphSize.height / range.toFloat()

    graphData.forEachIndexed { i, balance ->
        if (i == 0) {
            path.moveTo(
                0f,
                graphSize.height - (balance.amount - min.amount).toFloat() * heightPxPerAmount
            )
        }
        val balanceX = i * weekWidth
        val balanceY =
            graphSize.height - (balance.amount - min.amount).toFloat() * heightPxPerAmount
        path.lineTo(balanceX, balanceY)
    }
    return path
}

fun generateSmoothPath(data: List<Balance>, size: Size): Path {
    val path = Path()
    val numberEntries = data.size - 1
    val weekWidth = size.width / numberEntries

    val max = data.maxBy { it.amount }
    val min = data.minBy { it.amount } // will map to x= 0, y = height
    val range = max.amount - min.amount
    val heightPxPerAmount = size.height / range.toFloat()

    var previousBalanceX = 0f
    var previousBalanceY = size.height
    data.forEachIndexed { i, balance ->
        if (i == 0) {
            path.moveTo(
                0f,
                size.height - (balance.amount - min.amount).toFloat() *
                    heightPxPerAmount
            )
        }

        val balanceX = i * weekWidth
        val balanceY = size.height - (balance.amount - min.amount).toFloat() *
            heightPxPerAmount
        // to do smooth curve graph - we use cubicTo, uncomment section below for non-curve
        val controlPoint1 = PointF((balanceX + previousBalanceX) / 2f, previousBalanceY)
        val controlPoint2 = PointF((balanceX + previousBalanceX) / 2f, balanceY)
        path.cubicTo(
            controlPoint1.x,
            controlPoint1.y,
            controlPoint2.x,
            controlPoint2.y,
            balanceX,
            balanceY
        )

        previousBalanceX = balanceX
        previousBalanceY = balanceY
    }
    return path
}

fun drawBackgroundGraph(drawScope: DrawScope, rectColor: Color) {
    drawScope.apply {
        drawRect(color = rectColor, style = Stroke(3.dp.toPx()))

        val squarePrecision = 6

        val verticalLines = squarePrecision
        val verticalLength = size.width / (verticalLines + 1)
        repeat(verticalLines) { i ->
            val startX = verticalLength * (i + 1)
            drawLine(
                color = rectColor,
                start = Offset(startX, 0f),
                end = Offset(startX, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }

        val horizontalLines = squarePrecision
        val horizontalLength = size.height / (horizontalLines + 1)
        repeat(horizontalLines) { i ->
            val startY = horizontalLength * (i + 1)
            drawLine(
                color = rectColor,
                start = Offset(0f, startY),
                end = Offset(size.width, startY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

data class Balance(val date: LocalDate, val amount: BigDecimal)

private fun getData(): List<Balance> {
    return listOf(
        Balance(LocalDate.now().plusWeeks(0), getRand()),
        Balance(LocalDate.now().plusWeeks(1), getRand()),
        Balance(LocalDate.now().plusWeeks(2), getRand()),
        Balance(LocalDate.now().plusWeeks(3), getRand()),
        Balance(LocalDate.now().plusWeeks(4), getRand()),
        Balance(LocalDate.now().plusWeeks(5), getRand()),
        Balance(LocalDate.now().plusWeeks(6), getRand()),
        Balance(LocalDate.now().plusWeeks(7), getRand()),
        Balance(LocalDate.now().plusWeeks(8), getRand()),
        Balance(LocalDate.now().plusWeeks(9), getRand()),
        Balance(LocalDate.now().plusWeeks(10), getRand()),
        Balance(LocalDate.now().plusWeeks(11), getRand()),
        Balance(LocalDate.now().plusWeeks(12), getRand()),
        Balance(LocalDate.now().plusWeeks(13), getRand())
    )
}

fun getRand() = BigDecimal(Random.nextInt(30000) % 100000)
