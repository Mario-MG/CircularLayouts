package com.mariomg.circularlayouts.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mariomg.circularlayouts.CircularLayoutDirection
import com.mariomg.circularlayouts.CircularLayoutItemRotation
import com.mariomg.circularlayouts.circularrow.CircularRow
import com.mariomg.circularlayouts.rotation.rememberRotationState
import com.mariomg.circularlayouts.sample.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    val rotationState = rememberRotationState(
                        rotatableFraction = 2,
                    )
                    CircularRow(
                        modifier = Modifier.fillMaxSize(),
                        radius = 250.dp,
                        rotationState = rotationState,
                        direction = CircularLayoutDirection.CCW,
                        itemRotation = CircularLayoutItemRotation.TANGENT,
                    ) {
                        val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Gray, Color.Magenta)
                        for (index in 1..5) {
                            Text(
                                "Prueba de CircularRow $index",
                                modifier = Modifier.background(colors[index - 1]),
                            )
                        }
                    }
                }
            }
        }
    }
}
