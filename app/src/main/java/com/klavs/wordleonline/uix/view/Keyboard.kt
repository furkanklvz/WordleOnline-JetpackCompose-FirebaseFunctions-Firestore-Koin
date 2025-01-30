package com.klavs.wordleonline.uix.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.klavs.wordleonline.R


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Keyboard(
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