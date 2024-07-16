package com.neatplex.nightell.ui.user

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.ShowUsers
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowerScreen(navController: NavController, userId: Int, sharedViewModel : SharedViewModel) {

    val userViewModel : UserViewModel = hiltViewModel()
    val usersObserve by userViewModel.usersList.observeAsState()
    LaunchedEffect(Unit) {
        userViewModel.fetchUserFollowers(userId)
    }

    AppTheme {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, clip = false) // Add shadow to the Box
                        .background(Color.White)
                ) {
                    TopAppBar(
                        title = { Text(text = "Followers") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Transparent to use the Box's background
                        )
                    )
                }
            },
            content = { space ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(space),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    usersObserve.let { result ->
                        when (result) {
                            is Result.Success -> {
                                result.data?.users.let { users ->
                                    ShowUsers(users, navController, sharedViewModel)
                                }
                            }

                            is Result.Failure -> {
                                // Handle error
                            }

                            else -> {}
                        }
                    }
                }
            }
        )
    }
}


