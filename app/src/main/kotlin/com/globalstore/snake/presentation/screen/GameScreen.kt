package com.globalstore.snake.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.globalstore.snake.R
import com.globalstore.snake.domain.game.GameEngine
import com.globalstore.snake.extension.Direction
import com.globalstore.snake.presentation.activity.GameActivity
import com.globalstore.snake.presentation.component.AppBar
import com.globalstore.snake.presentation.component.Board

@Composable
fun GameScreen(
    gameEngine: GameEngine,
    score: Int,
    newDirection: Direction,
    currentDirection: Direction
) {
    val state = gameEngine.state.collectAsState(initial = null)
    val activity = LocalContext.current as GameActivity

    remember(key1 = newDirection) {
        when (newDirection) {
            Direction.Up -> {
                if (currentDirection != Direction.Down) {
                    gameEngine.move = Pair(0, -1)
                }
            }

            Direction.Left -> {
                if (currentDirection != Direction.Right) {
                    gameEngine.move = Pair(-1, 0)
                }
            }

            Direction.Right -> {
                if (currentDirection != Direction.Left) {
                    gameEngine.move = Pair(1, 0)
                }
            }

            Direction.Down -> {
                if (currentDirection != Direction.Up) {
                    gameEngine.move = Pair(0, 1)
                }
            }
        }
        mutableSetOf(Unit)
    }

    AppBar(
        title = stringResource(id = R.string.your_score, score),
        onBackClicked = { activity.finish() }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.control_message))
            state.value?.let { Board(it) }
        }
    }
}