package com.klavs.wordleonline.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.klavs.wordleonline.R
import com.klavs.wordleonline.routes.MenuScreen
import com.klavs.wordleonline.routes.Opening

sealed class BottomBarItem(
    val route: Any,
    val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Main : BottomBarItem(
        route = Opening,
        label = R.string.play,
        selectedIcon = Icons.Outlined.PlayArrow,
        unselectedIcon = Icons.Rounded.PlayArrow
    )
    data object Menu : BottomBarItem(
        route = MenuScreen,
        label = R.string.menu,
        selectedIcon = Icons.Rounded.Menu,
        unselectedIcon = Icons.Rounded.Menu
    )

    companion object{
        val items by lazy { listOf(Main, Menu) }
    }
}