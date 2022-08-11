package com.mariomg.circularrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mariomg.circularrow.ui.composables.CircularRow
import com.mariomg.circularrow.ui.theme.CircularRowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularRowTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    var numberOfClicks by remember { mutableStateOf(0) }
                    val offset by animateFloatAsState(
                        targetValue = numberOfClicks * 360f,
                        animationSpec = tween(3000),
                    )
                    CircularRow(
                        modifier = Modifier.weight(1f),
                        radius = 400.dp,
                        angularOffset = offset,
                    ) {
                        for (index in 1..5) {
                            Text("Prueba de CircularRow $index", modifier = Modifier.background(Color.Red))
                        }
                    }

                    Button(onClick = { numberOfClicks++ }) {
                        Text("Rotate!")
                    }
                }
            }
        }
    }
}
