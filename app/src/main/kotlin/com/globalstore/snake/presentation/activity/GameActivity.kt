package com.globalstore.snake.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.globalstore.snake.R
import com.globalstore.snake.data.cache.GameCache
import com.globalstore.snake.data.model.HighScore
import com.globalstore.snake.domain.base.BaseActivity
import com.globalstore.snake.domain.base.TOP_10
import com.globalstore.snake.domain.game.GameEngine
import com.globalstore.snake.extension.Direction
import com.globalstore.snake.extension.dragDetector
import com.globalstore.snake.extension.rememberDragOffset
import com.globalstore.snake.extension.toDirection
import com.globalstore.snake.presentation.screen.EndScreen
import com.globalstore.snake.presentation.screen.GameScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GameActivity : BaseActivity() {
    private lateinit var dataStore: GameCache
    private val isPlaying = mutableStateOf(true)
    private var score = mutableStateOf(0)
    private var newDirection = mutableStateOf(Direction.Up)
    private var currentDirection = mutableStateOf(Direction.Up)
    private lateinit var scope: CoroutineScope
    private lateinit var playerName: String
    private lateinit var highScores: List<HighScore>
    private var gameEngine = GameEngine(
        scope = lifecycleScope,
        onGameEnded = {
            if (isPlaying.value) {
                isPlaying.value = false
                scope.launch { dataStore.saveHighScore(highScores) }
            }
        },
        onFoodEaten = {
            score.value++
        }
    )

    @Composable
    private fun HandleLifeCycle() {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    gameEngine.needToPause(false)
                } else if (event == Lifecycle.Event.ON_RESUME) {

                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    @Composable
    override fun Content() {
        HandleLifeCycle()
        scope = rememberCoroutineScope()
        dataStore = GameCache(applicationContext)
        playerName =
            dataStore.getPlayerName.collectAsState(initial = stringResource(id = R.string.default_player_name)).value
        highScores = dataStore.getHighScores.collectAsState(initial = listOf()).value.plus(
            HighScore(playerName, score.value)
        ).sortedByDescending { it.score }.take(TOP_10)
        Column(
            modifier = Modifier
                .dragDetector(
                    enabled = true,
                    dragOffset = rememberDragOffset(),
                    onDragFinished = { dragOffset ->
                        currentDirection.value = newDirection.value
                        newDirection.value = dragOffset.toDirection()
                    }
                )

                .background(Color.Blue)
        ) {
            if (isPlaying.value) {
                GameScreen(
                    gameEngine = gameEngine,
                    score = score.value,
                    newDirection = newDirection.value,
                    currentDirection = currentDirection.value
                )
            } else {
                EndScreen(score.value) {
                    score.value = 0
                    gameEngine.reset()
                    isPlaying.value = true
                }
            }
        }
    }
}