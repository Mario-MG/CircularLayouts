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
                    val rotatableState = rememberRotatableState(0f)
                    LaunchedEffect(null) {
                        delay(4000)
                        rotatableState.animateRotateBy(144f)
                        delay(2000)
                        rotatableState.animateRotateTo(-72f)
                        delay(4000)
                        rotatableState.animateRotateBy(452f)
                        delay(2000)
                        rotatableState.animateRotateTo(720f)
                        delay(2000)
                        rotatableState.rotateInfinitely(2000)
                    }
                    CircularRow(
                        modifier = Modifier
                            .weight(1f)
                            .rotatable(rotatableState),
                        radius = 300.dp,
                        rotatableState = rotatableState,
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
