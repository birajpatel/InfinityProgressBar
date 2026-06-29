package com.example.infinityprogressbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin


/**
 * Pre-calculated normalized points of the Lemniscate of Bernoulli.
 * Samples 360 points from t = 0 to 2*PI, with a = 1.0.
 * Normalized range: X is in [-1, 1], Y is in [-0.35, 0.35] approximately.
 */
private val LemniscatePoints: List<Offset> by lazy {
    val samples = 360
    List(samples) { index ->
        val t = (index * 2 * Math.PI / samples).toFloat()
        val sinT = sin(t)
        val cosT = cos(t)
        val denominator = 1f + sinT * sinT
        // Parametric equations for Lemniscate of Bernoulli
        val x = cosT / denominator
        val y = (sinT * cosT) / denominator
        Offset(x, y)
    }
}

/**
 * A beautiful, mathematically precise infinity-shaped progress bar (Lemniscate of Bernoulli).
 *
 * @param modifier Modifier for the layout.
 * @param color Color of the active progress indicator.
 * @param trackColor Color of the background infinity track.
 * @param strokeWidth Thickness of the paths.
 * @param durationMillis Duration of one full loop in milliseconds.
 * @param activeSegmentLength Fraction of the path length that is lit up (between 0.0f and 1.0f).
 */
@Composable
fun InfinityProgressBar(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6200EE),
    trackColor: Color = color.copy(alpha = 0.2f),
    strokeWidth: Dp = 4.dp,
    durationMillis: Int = 2000,
    activeSegmentLength: Float = 0.25f
) {
    var progressPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(durationMillis) {
        val startTime = withFrameMillis { it }
        val startPhase = progressPhase
        while (true) {
            withFrameMillis { frameTime ->
                val elapsed = frameTime - startTime
                val duration = maxOf(durationMillis.toFloat(), 1f)
                progressPhase = (startPhase + (elapsed.toFloat() / duration)) % 1f
            }
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Calculate scaling factor 'a' to fit the lemniscate within the canvas.
        // The lemniscate spans from x = -a to x = a (width = 2a), and y = -a/2 to y = a/2 approximately.
        // We leave a padding of strokeWidth to avoid clipping.
        val strokeWidthPx = strokeWidth.toPx()
        val usableWidth = width - strokeWidthPx * 2
        val usableHeight = height - strokeWidthPx * 2
        
        val scaleX = usableWidth / 2f
        // Theoretical max y for a=1 is 1/sqrt(8) ≈ 0.3535. Max height is 2 * 0.3535 ≈ 0.707.
        val scaleY = usableHeight / 0.707f
        val scale = minOf(scaleX, scaleY)

        val center = Offset(width / 2f, height / 2f)

        // Build the base path by scaling the pre-calculated normalized points
        val basePath = Path().apply {
            if (LemniscatePoints.isNotEmpty()) {
                val firstPoint = LemniscatePoints.first()
                moveTo(center.x + firstPoint.x * scale, center.y + firstPoint.y * scale)
                for (i in 1 until LemniscatePoints.size) {
                    val p = LemniscatePoints[i]
                    lineTo(center.x + p.x * scale, center.y + p.y * scale)
                }
                close()
            }
        }

        // Draw the background track
        drawPath(
            path = basePath,
            color = trackColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Measure the path to extract the animated active segment
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(basePath, forceClosed = true)
        val pathLength = pathMeasure.length

        val activePath = Path()
        val startDistance = progressPhase * pathLength
        val endDistance = startDistance + (activeSegmentLength * pathLength)

        if (endDistance <= pathLength) {
            // Segment is entirely within the path boundary
            pathMeasure.getSegment(startDistance, endDistance, activePath, startWithMoveTo = true)
        } else {
            // Segment wraps around the start of the path
            pathMeasure.getSegment(startDistance, pathLength, activePath, startWithMoveTo = true)
            pathMeasure.getSegment(0f, endDistance - pathLength, activePath, startWithMoveTo = true)
        }

        // Draw the moving active segment
        drawPath(
            path = activePath,
            color = color,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}
