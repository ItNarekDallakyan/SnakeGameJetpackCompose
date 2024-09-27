package com.globalstore.snake.extension

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

fun Modifier.dragDetector(
    enabled: Boolean,
    dragOffset: MutableState<Offset>,
    onDragFinished: (Offset) -> Unit,
) = pointerInput(Unit) {
    if (enabled) {
        detectDragGestures(
            onDragStart = { dragOffset.value = Offset(0f, 0f) },
            onDragEnd = { onDragFinished(dragOffset.value) }
        ) { change, dragAmount ->
            change.consume()
            dragOffset.value += Offset(dragAmount.x, dragAmount.y)
        }
    }
}

@Composable
fun rememberDragOffset() = remember { mutableStateOf(Offset(0f, 0f)) }


fun Offset.toDirection() = if (abs(this.x) > abs(this.y)) {
    if (this.x > 0) {
        Direction.Right
    } else {
        Direction.Left
    }
} else {
    if (this.y > 0) {
        Direction.Down
    } else {
        Direction.Up
    }
}

enum class Direction {
    Left, Right, Up, Down;
}