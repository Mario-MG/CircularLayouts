package com.mariomg.circularrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mariomg.circularrow.ui.composables.*
import com.mariomg.circularrow.ui.theme.CircularRowTheme
import kotlinx.coroutines.delay

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
                    val rotationState = rememberRotationState(0f)
                    LaunchedEffect(null) {
                        delay(2000)
                        rotationState.animateRotateTo(360f, animationSpec = tween(6000))
                    }
                    LaunchedEffect(null) {
                        delay(5000)
                        rotationState.stopRotation()
                    }
                    CircularRow(
                        modifier = Modifier
                            .weight(1f)
                            .rotatable(rotationState),
                        radius = 300.dp,
                        rotationState = rotationState,
                    ) {
                        for (index in 1..5) {
                            Text("Prueba de CircularRow $index", modifier = Modifier.background(Color.Red))
                        }
                    }
                }
            }
        }
    }
}
