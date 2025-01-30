package com.klavs.wordleonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.klavs.wordleonline.ui.theme.WordleOnlineTheme
import com.klavs.wordleonline.uix.view.BottomNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordleOnlineTheme {
                BottomNavigation()
            }
        }
    }
}