package com.example.infinityprogressbar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var showCometHead by remember { mutableStateOf(true) }
    var cometCount by remember { mutableFloatStateOf(1f) }
    var isDeterminateMode by remember { mutableStateOf(false) }
    var determinateProgress by remember { mutableFloatStateOf(0.5f) }
    
    val colorPresets = remember {
        listOf(
            Color(0xFF00F2FE) to "Cyan",
            Color(0xFF39FF14) to "Green",
            Color(0xFFFF007F) to "Pink",
            Color(0xFFFFD700) to "Gold",
            Color(0xFFBD00FF) to "Purple"
        )
    }
    var selectedColorIndex by remember { mutableStateOf(0) }
    val primaryColor = colorPresets[selectedColorIndex].first
    val trackColor = primaryColor.copy(alpha = 0.15f)

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
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
                    activeSegmentLength = activeSegmentLength,
                    showCometHead = showCometHead,
                    cometCount = cometCount.toInt(),
                    progress = if (isDeterminateMode) determinateProgress else null
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
            // Mode Selector (Determinate vs Indeterminate)
            ToggleRow(
                label = "Determinate Progress Mode",
                checked = isDeterminateMode,
                onCheckedChange = { isDeterminateMode = it },
                activeColor = primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Glow Color Preset Picker
            Text(
                text = "Glow Color: ${colorPresets[selectedColorIndex].second}",
                color = Color.White,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorPresets.forEachIndexed { index, (color, _) ->
                    val isSelected = selectedColorIndex == index
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { selectedColorIndex = index }
                    )
                }
            }

            // Stroke Width Control
            ControlSlider(
                label = "Stroke Width: ${String.format("%.1f", strokeWidth)} dp",
                value = strokeWidth,
                onValueChange = { strokeWidth = it },
                valueRange = 2f..16f,
                activeColor = primaryColor
            )

            if (isDeterminateMode) {
                Spacer(modifier = Modifier.height(16.dp))

                // Determinate Progress Slider Control
                ControlSlider(
                    label = "Progress: ${(determinateProgress * 100).toInt()}%",
                    value = determinateProgress,
                    onValueChange = { determinateProgress = it },
                    valueRange = 0f..1f,
                    activeColor = primaryColor
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // Duration (Speed) Control
                ControlSlider(
                    label = "Animation Duration: ${durationMillis.toInt()} ms",
                    value = durationMillis,
                    onValueChange = { durationMillis = it },
                    valueRange = 500f..5000f,
                    activeColor = primaryColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Segment Length Control
                ControlSlider(
                    label = "Active Length: ${(activeSegmentLength * 100).toInt()}%",
                    value = activeSegmentLength,
                    onValueChange = { activeSegmentLength = it },
                    valueRange = 0.05f..0.8f,
                    activeColor = primaryColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Comet Count Control
                ControlSlider(
                    label = "Comet Count: ${cometCount.toInt()}",
                    value = cometCount,
                    onValueChange = { cometCount = it },
                    valueRange = 1f..4f,
                    steps = 2,
                    activeColor = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comet Head Toggle Control
            ToggleRow(
                label = "Show Comet Head (Dot)",
                checked = showCometHead,
                onCheckedChange = { showCometHead = it },
                activeColor = primaryColor
            )
        }
    }
}

// --- Reusable Control Components ---

@Composable
fun ControlSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    activeColor: Color,
    steps: Int = 0
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                activeTrackColor = activeColor,
                thumbColor = activeColor
            )
        )
    }
}

@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = activeColor,
                checkedTrackColor = activeColor.copy(alpha = 0.5f)
            )
        )
    }
}
