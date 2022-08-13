package com.mariomg.circularrow

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
import com.mariomg.circularrow.ui.composables.*
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
                    val rotatableState = rememberRotatableState(0f)
                    CircularRow(
                        modifier = Modifier
                            .weight(1f)
                            .rotatable(rotatableState),
                        radius = 400.dp,
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