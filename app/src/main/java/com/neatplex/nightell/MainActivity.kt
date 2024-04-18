package com.neatplex.nightell

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Text
import com.neatplex.nightell.navigation.Navigation
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            window.statusBarColor = getColor(R.color.black)
            MyApp()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MyApp() {

        AppTheme {
            val rootNavController = rememberNavController()
            val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute !in listOf("signIn", "signUp", "splash")

            Scaffold(
                bottomBar = {
                    if (showBottomBar){
                        NavigationBar {
                            items.forEach { item ->
                                val isSelected = item.route == navBackStackEntry?.destination?.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = if (isSelected) rememberVectorPainter(image = item.selectedIcon) else rememberVectorPainter(
                                                image = item.unselectedIcon
                                            ),
                                            contentDescription = null
                                        )
                                    },
                                    alwaysShowLabel = false,
                                    selected = isSelected,
                                    onClick = {
                                        rootNavController.navigate(item.route) {
                                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }else{
                        Text(text = "Powered by Neatplex", modifier = Modifier.padding(30.dp))
                    }
                }
            ) {
                Navigation(navController = rootNavController, tokenManager = tokenManager)
            }
        }
    }

}

data class BottomNavigationItems(
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

val items = listOf(
    BottomNavigationItems("home", Icons.Outlined.Home, Icons.Filled.Home),
    BottomNavigationItems("addPost", Icons.Outlined.Add, Icons.Filled.Add),
    BottomNavigationItems("profile", Icons.Outlined.Person, Icons.Filled.Person),
)
