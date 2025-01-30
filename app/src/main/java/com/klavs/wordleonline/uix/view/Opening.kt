package com.klavs.wordleonline.uix.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.klavs.wordleonline.R
import com.klavs.wordleonline.routes.Game

@Composable
fun Opening(navController: NavHostController) {
    OpeningContent(onPlayRandom = { navController.navigate(Game) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningContent(onPlayRandom: () -> Unit = {}) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(R.string.app_name))
        })
    }) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                FilledTonalButton(
                    onClick = { onPlayRandom() }
                ) {
                    Text(stringResource(R.string.play))
                }
            }
        }
    }
}

@Preview
@Composable
private fun OpeningPreview() {
    OpeningContent()
}