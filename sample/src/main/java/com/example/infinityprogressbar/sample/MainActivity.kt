package com.example.infinityprogressbar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinityprogressbar.InfinityProgressBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F0F1A) // Sleek dark space background
                ) {
                    DemoScreen()
                }
            }
        }
    }
}

@Composable
fun DemoScreen() {
    var strokeWidth by remember { mutableFloatStateOf(6f) }
    var durationMillis by remember { mutableFloatStateOf(2000f) }
    var activeSegmentLength by remember { mutableFloatStateOf(0.3f) }
    
    val primaryColor = Color(0xFF00F2FE) // Bright cyan
    val trackColor = Color(0xFF00F2FE).copy(alpha = 0.15f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Infinity Progress Bar",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Mathematical Lemniscate of Bernoulli",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Display Card (Glassmorphic vibe)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Infinity Progress Bar
                InfinityProgressBar(
                    modifier = Modifier.size(200.dp, 100.dp),
                    color = primaryColor,
                    trackColor = trackColor,
                    strokeWidth = strokeWidth.dp,
                    durationMillis = durationMillis.toInt(),
                    activeSegmentLength = activeSegmentLength
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161624), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            // Stroke Width Control
            Text(
                text = "Stroke Width: ${String.format("%.1f", strokeWidth)} dp",
                color = Color.White,
                fontSize = 14.sp
            )
            Slider(
                value = strokeWidth,
                onValueChange = { strokeWidth = it },
                valueRange = 2f..16f,
                colors = SliderDefaults.colors(
                    activeTrackColor = primaryColor,
                    thumbColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Duration (Speed) Control
            Text(
                text = "Animation Duration: ${durationMillis.toInt()} ms",
                color = Color.White,
                fontSize = 14.sp
            )
            Slider(
                value = durationMillis,
                onValueChange = { durationMillis = it },
                valueRange = 500f..5000f,
                colors = SliderDefaults.colors(
                    activeTrackColor = primaryColor,
                    thumbColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Segment Length Control
            Text(
                text = "Active Length: ${(activeSegmentLength * 100).toInt()}%",
                color = Color.White,
                fontSize = 14.sp
            )
            Slider(
                value = activeSegmentLength,
                onValueChange = { activeSegmentLength = it },
                valueRange = 0.05f..0.8f,
                colors = SliderDefaults.colors(
                    activeTrackColor = primaryColor,
                    thumbColor = primaryColor
                )
            )
        }
    }
}
