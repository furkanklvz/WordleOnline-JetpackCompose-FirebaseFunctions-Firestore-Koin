package com.klavs.wordleonline.uix.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.klavs.wordleonline.domain.model.BottomBarItem
import com.klavs.wordleonline.routes.Game
import com.klavs.wordleonline.routes.MenuScreen
import com.klavs.wordleonline.routes.Opening
import com.klavs.wordleonline.uix.viewmodel.GameViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BottomNavigation() {
    val navController = rememberNavController()
    val bottomBarItems = BottomBarItem.items
    var bottomBarIsEnable by remember { mutableStateOf(true) }
    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = bottomBarIsEnable) {
                BottomBar(bottomBarItems, navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Opening,
            modifier = Modifier.padding(
                bottom = if (bottomBarIsEnable) innerPadding.calculateBottomPadding()
                else WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            composable<Opening> {
                LaunchedEffect(Unit) {
                    bottomBarIsEnable = true
                }
                Opening(navController = navController)
            }
            composable<Game> {
                LaunchedEffect(Unit) {
                    bottomBarIsEnable = false
                }
                val viewModel = koinViewModel<GameViewModel>(parameters = { parametersOf(it) })
                Game(viewModel = viewModel, navController = navController)
            }
            composable<MenuScreen> { Menu(navController = navController) }
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBar(
    items: List<BottomBarItem>,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    ShortNavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.route::class)
            } == true
            ShortNavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.label)
                    )
                },
                label = { Text(stringResource(item.label)) }
            )
        }
    }
}


@Preview
@Composable
private fun BottomNavigationPreview() {
    BottomNavigation()
}