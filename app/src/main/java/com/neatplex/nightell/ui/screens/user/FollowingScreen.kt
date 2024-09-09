package com.neatplex.nightell.ui.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowingScreen(navController: NavController,
                    userId: Int,
                    sharedViewModel : SharedViewModel) {

    val userViewModel : UserViewModel = hiltViewModel()
    val users by userViewModel.usersList.observeAsState(emptyList())
    val count = 20
    val isLoading by userViewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        userViewModel.fetchUserFollowings(userId, null , count)
    }

    AppTheme {
        Scaffold(
            topBar = {
                AppBarWithTitle(navController = navController, title = stringResource(R.string.followings))
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UsersList(
                        users = users,
                        isLoading = isLoading,
                        sharedViewModel = sharedViewModel,
                        navController = navController,
                        userViewModel = userViewModel,
                        userId = userId,
                        count = count
                    )
                }
            }
        )
    }
}