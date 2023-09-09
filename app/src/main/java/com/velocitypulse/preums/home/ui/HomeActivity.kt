package com.velocitypulse.preums.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velocitypulse.preums.core.di.ApplicationInitializer
import com.velocitypulse.preums.home.theme.PreumsTheme
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

class HomeActivity : ComponentActivity() {

    private var applicationInitializer = ApplicationInitializer()

    override fun onCreate(savedInstanceState: Bundle?) {

        applicationInitializer.run(application)

        super.onCreate(savedInstanceState)

        setContent { MainView() }
    }


    @Composable
    fun ExerciseGraph(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            val rectColor = MaterialTheme.colorScheme.onPrimary
            val actionColor = MaterialTheme.colorScheme.secondary
            Spacer(modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {

                        val path = generatePath(getData())

                        drawBackgroundGraph(this, rectColor)

                        drawPath(path, actionColor, style = Stroke(2.dp.toPx()))
                    }
                })
        }
    }

    fun generatePath(graphData: List<Balance>, size: Size) {

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

    fun getData(): List<Balance> {
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

    @Composable
    fun MainView() {
        PreumsTheme {
            ExerciseGraph(Modifier.background(MaterialTheme.colorScheme.primary)) //                DrawRosace(Modifier.background(Color.Transparent))
        }

        /*
                PreumsTheme { // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondary),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PushableButton(MaterialTheme.colorScheme.errorContainer)
                    }
                }
        */
    }

    @Composable
    fun PushableButton(color: Color, modifier: Modifier = Modifier) {

        val color2 = MaterialTheme.colorScheme.primary
        Surface {
            Canvas(
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                drawCircle(
                    color = color2, radius = 100.dp.toPx()
                )
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!", modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() { //        ExerciseGraph()
        MainView()
    }
}



