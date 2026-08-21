package com.abstergo.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.abstergo.model.WindowState
import com.abstergo.ui.theme.WindowClose
import com.abstergo.ui.theme.WindowMinimize
import com.abstergo.ui.theme.WindowTitleBar

@Composable
fun FloatingWindow(
    windowState: WindowState,
    onDrag: (Float, Float) -> Unit,
    onResize: (Float, Float) -> Unit,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .offset(
                x = with(density) { windowState.offset.x.toDp() },
                y = with(density) { windowState.offset.y.toDp() }
            )
            .size(windowState.size)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .pointerInput(Unit) { onFocus() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(WindowTitleBar)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(
                                windowState.offset.x + dragAmount.x,
                                (windowState.offset.y + dragAmount.y).coerceAtLeast(0f)
                            )
                        }
                    }
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Window controls (macOS style)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Close button
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(WindowClose)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClose
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(8.dp)
                        )
                    }

                    // Minimize button
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(WindowMinimize)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onMinimize
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Minimize,
                            contentDescription = "Minimize",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }

                // Title
                Text(
                    text = windowState.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )


            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
            ) {
                content()
            }
        }

        // Resize handle (bottom-right corner)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(20.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newWidth = windowState.size.width.value + (dragAmount.x / density.density)
                        val newHeight = windowState.size.height.value + (dragAmount.y / density.density)
                        onResize(
                            newWidth.coerceAtLeast(250f),
                            newHeight.coerceAtLeast(200f)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.OpenInFull,
                contentDescription = "Resize",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
