package com.klavs.wordleonline.uix.view

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.VideogameAsset
import androidx.compose.material.icons.rounded.VideogameAssetOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.klavs.wordleonline.R
import com.klavs.wordleonline.data.entity.Lobby
import com.klavs.wordleonline.data.entity.LobbyStatus
import com.klavs.wordleonline.domain.model.GameResource
import com.klavs.wordleonline.ui.theme.WordleOnlineTheme
import com.klavs.wordleonline.uix.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun Game(viewModel: GameViewModel, navController: NavHostController) {
    val lobbyResource by viewModel.lobbyResultFlow.collectAsStateWithLifecycle()

    GameContent(
        lobbyResource = lobbyResource,
        goBackToMain = { navController.popBackStack() },
        guess = viewModel::guess
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun GameContent(
    lobbyResource: GameResource<Lobby>,
    guess: (String) -> Unit = {},
    goBackToMain: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = goBackToMain
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = "leave",
                            modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    CircularWavyProgressIndicator()
                    Text(
                        stringResource(R.string.searching_players) + "...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            when (lobbyResource) {
                is GameResource.Error -> {
                    goBackToMain()
                }

                is GameResource.Success -> {
                    when (lobbyResource.data!!.status) {
                        LobbyStatus.PLAYING -> {
                            val myTurn = lobbyResource.data.playerID == lobbyResource.data.turn
                            val missingChars = remember { mutableStateListOf<Char>() }
                            val word = lobbyResource.data.word!!
                            val guessList = lobbyResource.data.guesses
                            var input by remember { mutableStateOf("") }
                            val currentIndex =
                                (guessList.size * 6 + if (input.length != 6) input.length else 5)
                            var timer by rememberSaveable { mutableIntStateOf(40) }
                            var winner by remember { mutableStateOf<Int?>(null) }
                            LaunchedEffect(lobbyResource.data.turn) {
                                Log.d("Game", "turn LaunchedEffect called, timer: $timer")
                                timer = 40
                                while (timer > 0 && winner == null) {
                                    delay(1000)
                                    timer--
                                }
                                if (myTurn && winner == null) {
                                    guess("")
                                }
                            }
                            LaunchedEffect(Unit) {
                                isLoading = false
                            }
                            LaunchedEffect(lobbyResource.data.winner) {
                                winner = lobbyResource.data.winner
                            }
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(
                                        count = 6
                                    )
                                ) {
                                    items(36) { index ->
                                        val row = index / 6
                                        val char = if (row < guessList.size) {
                                            if (index == 0) guessList.first()
                                                .first() else {
                                                guessList[index / 6]
                                                    .toCharArray()[index % 6]
                                            }
                                        } else if (row == guessList.size) {
                                            try {
                                                input[index % 6]
                                            } catch (e: Exception) {
                                                ' '
                                            }
                                        } else {
                                            ' '
                                        }
                                        val currentBox = index == currentIndex
                                        var modifier = Modifier
                                            .padding(5.dp)
                                            .size(50.dp)
                                        if (currentBox) {
                                            modifier = modifier.border(
                                                2.dp,
                                                Color.Black,
                                                shape = CardDefaults.shape
                                            )
                                        }
                                        var color: Color = Color.Unspecified
                                        if (row < guessList.size) {
                                            if (guessList[row][index % 6] == word[index % 6]) {
                                                color = if (isSystemInDarkTheme()) {
                                                    Color.Green.copy(green = 0.5f)
                                                } else {
                                                    Color.Green
                                                }
                                            } else if (word.contains(guessList[row][index % 6])) {
                                                color = if (isSystemInDarkTheme()) {
                                                    Color.Yellow.copy(
                                                        red = 0.6f,
                                                        green = 0.6f,
                                                        blue = 0f
                                                    )
                                                } else {
                                                    Color.Yellow
                                                }
                                            } else {
                                                if (!missingChars.contains(char)) {
                                                    missingChars.add(char)
                                                }

                                                color = Color.Gray
                                            }
                                        }
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = color
                                            ),
                                            modifier = modifier
                                        ) {
                                            Box(
                                                Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = char.toString().uppercase(),
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            }

                                        }
                                    }
                                }


                                winner?.let { winnerId ->
                                    when (winnerId) {
                                        0 -> {
                                            AlertDialog(
                                                properties = DialogProperties(
                                                    dismissOnBackPress = false,
                                                    dismissOnClickOutside = false
                                                ),
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Rounded.VideogameAsset,
                                                        contentDescription = "game over"
                                                    )
                                                },
                                                title = {
                                                    Text(stringResource(R.string.game_over))
                                                },
                                                text = {
                                                    Text(
                                                        stringResource(R.string.answer) + ": " + word.uppercase(),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = goBackToMain
                                                    ) {
                                                        Text(stringResource(R.string.main_page))
                                                    }
                                                },
                                                onDismissRequest = {}
                                            )
                                        }

                                        lobbyResource.data.playerID -> {
                                            Text(
                                                text = stringResource(R.string.you_won),
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .background(
                                                        Color.Green,
                                                        RoundedCornerShape(15.dp)
                                                    )
                                                    .padding(8.dp),
                                                color = Color.DarkGray,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        else -> {
                                            Text(
                                                text = stringResource(R.string.your_opponent_won),
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .background(
                                                        MaterialTheme.colorScheme.errorContainer,
                                                        RoundedCornerShape(15.dp)
                                                    )
                                                    .padding(8.dp),
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Keyboard(
                                    input = input,
                                    guess = {
                                        guess(it)
                                        input = ""
                                    },
                                    myTurn = myTurn,
                                    timer = timer,
                                    winner = winner,
                                    missingChars = missingChars,
                                    onCharClick = {
                                        input = replaceCharAtIndex(
                                            str = input,
                                            index = currentIndex % 6,
                                            newChar = it
                                        )
                                    },
                                    onBackSpaceClick = {
                                        input = if (input.length != 6) {
                                            replaceCharAtIndex(
                                                str = input,
                                                index = input.length - 1,
                                                newChar = null
                                            )
                                        } else {
                                            replaceCharAtIndex(
                                                str = input,
                                                index = 5,
                                                newChar = null
                                            )
                                        }
                                    }
                                )
                            }

                        }

                        LobbyStatus.ENDED -> {
                            AlertDialog(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.VideogameAssetOff,
                                        contentDescription = ""
                                    )
                                },
                                properties = DialogProperties(
                                    dismissOnBackPress = false,
                                    dismissOnClickOutside = false
                                ),
                                title = {
                                    Text(stringResource(R.string.opponent_left))
                                },
                                confirmButton = {
                                    Button(
                                        onClick = goBackToMain
                                    ) {
                                        Text(stringResource(R.string.ok))
                                    }
                                },
                                onDismissRequest = {}
                            )
                        }

                        else -> {}
                    }
                }

                is GameResource.LobbyTimeOut -> {
                    LaunchedEffect(Unit) {
                        isLoading = false
                    }
                    AlertDialog(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "timeout"
                            )
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        ),
                        text = {
                            Text(
                                stringResource(R.string.cannot_be_found_active_players),
                                textAlign = TextAlign.Center
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = goBackToMain
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        onDismissRequest = {}
                    )
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Keyboard(
    input: String,
    guess: (String) -> Unit,
    myTurn: Boolean = true,
    timer: Int,
    onCharClick: (Char) -> Unit,
    onBackSpaceClick: () -> Unit,
    winner: Int?,
    missingChars: List<Char>
) {
    val screenWith = LocalConfiguration.current.screenWidthDp.dp
    val words = listOf(
        "qwertyuıopğü".toCharArray(),
        "asdfghjklşi".toCharArray(),
        "zxcvbnmöç".toCharArray()
    )
    Column(
        modifier = Modifier.padding(bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "$timer seconds left"
                )
                Text(
                    timer.toString(),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            if (myTurn) {
                Text(
                    text = stringResource(R.string.your_turn)
                )
            } else {
                Text(
                    text = stringResource(R.string.opponent_turn)
                )
            }
            val enable =
                myTurn && winner == null && input.length == 6 && timer != -1
            FilledIconButton(
                enabled = enable,
                modifier = Modifier
                    .padding(5.dp),
                onClick = {
                    guess(input)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "send"
                )
            }
        }
        words.forEachIndexed { index, charArray ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    screenWith / 180f,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                charArray.forEach { char ->
                    val containerColor =
                        if (missingChars.contains(char)) {
                            Color.Gray
                        } else Color.Unspecified
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor
                        ),
                        modifier = Modifier
                            .size(
                                width = screenWith / 13f,
                                height = 55.dp
                            )
                            .clip(CardDefaults.shape)
                            .clickable {
                                onCharClick(char)
                            }) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                char.toString().uppercase(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                    }

                }
                if (index == 2) {
                    Card(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clip(CardDefaults.shape)
                            .clickable {
                                onBackSpaceClick()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Backspace,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(9.dp)
                                .size(IconButtonDefaults.xSmallIconSize),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

fun replaceCharAtIndex(str: String, index: Int, newChar: Char?): String {
    if (newChar != null) {
        if (index < 0) {
            throw IndexOutOfBoundsException("Index is out of bounds")
        } else {
            if (index < str.length) {
                val charArray = str.toCharArray()
                charArray[index] = newChar
                return String(charArray)
            } else if (index == str.length) {
                val charArray = str.toCharArray().toMutableList()
                charArray.add(newChar)
                return String(charArray.toCharArray())
            } else {
                throw IndexOutOfBoundsException("Index is out of bounds")
            }
        }
    } else {
        return if (str.isEmpty()) {
            str
        } else {
            val charArray = str.toCharArray().toMutableList()
            charArray.removeAt(charArray.size - 1)
            String(charArray.toCharArray())
        }

    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun GamePreview() {
    WordleOnlineTheme(darkTheme = true) {
        val lobby = Lobby(
            winner = 2,
            playerID = 1,
            word = "Karpuz",
            guesses = listOf("pardon", "gırgır"),
            status = LobbyStatus.PLAYING
        )
        GameContent(lobbyResource = GameResource.Success(data = lobby))
    }
}