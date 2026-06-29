package com.example.infinityprogressbar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
 * @param showCometHead Whether to show the glowing circle head.
 * @param cometHeadRadius Radius of the glowing circle head.
 * @param cometCount Number of comets orbiting simultaneously (Indeterminate mode only).
 * @param progress Null for Indeterminate mode, or 0.0f..1.0f for Determinate mode.
 */
@Composable
fun InfinityProgressBar(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6200EE),
    trackColor: Color = color.copy(alpha = 0.2f),
    strokeWidth: Dp = 4.dp,
    durationMillis: Int = 2000,
    activeSegmentLength: Float = 0.25f,
    showCometHead: Boolean = true,
    cometHeadRadius: Dp = strokeWidth * 1.5f,
    cometCount: Int = 1,
    progress: Float? = null
) {
    val isDeterminate = progress != null
    val animatedProgress by animateFloatAsState(
        targetValue = progress ?: 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "DeterminateProgress"
    )

    var progressPhase by remember { mutableFloatStateOf(0f) }

    if (!isDeterminate) {
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
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Calculate scaling factor 'a' to fit the lemniscate within the canvas.
        val strokeWidthPx = strokeWidth.toPx()
        val scale = calculateScale(width, height, strokeWidthPx)
        val center = Offset(width / 2f, height / 2f)

        // Build the base path by scaling the pre-calculated normalized points
        val basePath = buildLemniscatePath(center, scale)

        // Draw the background track
        drawPath(
            path = basePath,
            color = trackColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Measure the path to extract the animated active segments
        val pathMeasure = PathMeasure().apply { setPath(basePath, forceClosed = true) }
        val pathLength = pathMeasure.length
        val cometHeadRadiusPx = cometHeadRadius.toPx()

        if (isDeterminate) {
            drawDeterminateProgress(
                pathMeasure = pathMeasure,
                pathLength = pathLength,
                progress = animatedProgress,
                strokeWidthPx = strokeWidthPx,
                color = color,
                showCometHead = showCometHead,
                cometHeadRadiusPx = cometHeadRadiusPx
            )
        } else {
            drawIndeterminateProgress(
                pathMeasure = pathMeasure,
                pathLength = pathLength,
                progressPhase = progressPhase,
                activeSegmentLength = activeSegmentLength,
                cometCount = cometCount,
                strokeWidthPx = strokeWidthPx,
                color = color,
                showCometHead = showCometHead,
                cometHeadRadiusPx = cometHeadRadiusPx
            )
        }
    }
}

// --- Helper Extensions & Functions ---

private fun calculateScale(width: Float, height: Float, strokeWidthPx: Float): Float {
    val usableWidth = width - strokeWidthPx * 2
    val usableHeight = height - strokeWidthPx * 2
    val scaleX = usableWidth / 2f
    // Theoretical max y for a=1 is 1/sqrt(8) ≈ 0.3535. Max height is 2 * 0.3535 ≈ 0.707.
    val scaleY = usableHeight / 0.707f
    return minOf(scaleX, scaleY)
}

private fun buildLemniscatePath(center: Offset, scale: Float): Path {
    return Path().apply {
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
}

private fun DrawScope.drawGlowPath(
    path: Path,
    color: Color,
    strokeWidthPx: Float,
    alphaMultiplier: Float = 1f
) {
    // Pass 1: Outer wide, faint blur
    drawPath(
        path = path,
        color = color.copy(alpha = alphaMultiplier * 0.15f),
        style = Stroke(width = strokeWidthPx * 3f, cap = StrokeCap.Round)
    )
    // Pass 2: Inner medium glow
    drawPath(
        path = path,
        color = color.copy(alpha = alphaMultiplier * 0.35f),
        style = Stroke(width = strokeWidthPx * 1.8f, cap = StrokeCap.Round)
    )
    // Pass 3: Solid core
    drawPath(
        path = path,
        color = color.copy(alpha = alphaMultiplier),
        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawGlowCircle(
    center: Offset,
    radius: Float,
    color: Color
) {
    // Pass 1: Outer wide, faint glow
    drawCircle(
        color = color.copy(alpha = 0.15f),
        radius = radius * 2.2f,
        center = center
    )
    // Pass 2: Inner medium glow
    drawCircle(
        color = color.copy(alpha = 0.35f),
        radius = radius * 1.5f,
        center = center
    )
    // Pass 3: Core circle
    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawDeterminateProgress(
    pathMeasure: PathMeasure,
    pathLength: Float,
    progress: Float,
    strokeWidthPx: Float,
    color: Color,
    showCometHead: Boolean,
    cometHeadRadiusPx: Float
) {
    val endDistance = progress.coerceIn(0f, 1f) * pathLength
    if (endDistance > 0f) {
        val activePath = Path()
        pathMeasure.getSegment(0f, endDistance, activePath, startWithMoveTo = true)
        
        drawGlowPath(activePath, color, strokeWidthPx)

        if (showCometHead) {
            val headOffset = pathMeasure.getPosition(endDistance)
            drawGlowCircle(headOffset, cometHeadRadiusPx, color)
        }
    }
}

private fun DrawScope.drawIndeterminateProgress(
    pathMeasure: PathMeasure,
    pathLength: Float,
    progressPhase: Float,
    activeSegmentLength: Float,
    cometCount: Int,
    strokeWidthPx: Float,
    color: Color,
    showCometHead: Boolean,
    cometHeadRadiusPx: Float
) {
    for (c in 0 until cometCount) {
        val phaseOffset = c.toFloat() / cometCount
        val cometPhase = (progressPhase + phaseOffset) % 1f
        
        val startDistance = cometPhase * pathLength
        val endDistance = startDistance + (activeSegmentLength * pathLength)

        // Draw the moving active segment as a series of smaller segments with increasing opacity
        val steps = 20
        val segmentSpan = (activeSegmentLength * pathLength) / steps
        for (i in 0 until steps) {
            val subStart = startDistance + i * segmentSpan
            val subEnd = subStart + segmentSpan
            val stepAlpha = (i + 1).toFloat() / steps
            
            val subPath = Path()
            val wrappedStart = subStart % pathLength
            val wrappedEnd = subEnd % pathLength
            
            if (wrappedEnd > wrappedStart) {
                pathMeasure.getSegment(wrappedStart, wrappedEnd, subPath, startWithMoveTo = true)
            } else {
                pathMeasure.getSegment(wrappedStart, pathLength, subPath, startWithMoveTo = true)
                pathMeasure.getSegment(0f, wrappedEnd, subPath, startWithMoveTo = true)
            }
            
            drawGlowPath(subPath, color, strokeWidthPx, stepAlpha)
        }

        // Draw the comet head
        if (showCometHead) {
            val targetDistance = if (endDistance >= pathLength) endDistance - pathLength else endDistance
            val headOffset = pathMeasure.getPosition(targetDistance)
            drawGlowCircle(headOffset, cometHeadRadiusPx, color)
        }
    }
}
