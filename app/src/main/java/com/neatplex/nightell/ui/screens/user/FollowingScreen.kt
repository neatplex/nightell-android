package com.neatplex.nightell.ui.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowingScreen(navController: NavController,
                    userId: Int,
                    sessionViewModel: SessionViewModel,
                    followListViewModel: FollowListViewModel = hiltViewModel()) {

    val usersState by followListViewModel.users.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        followListViewModel.start(userId, FollowListType.FOLLOWINGS)
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
                        users = usersState.items,
                        isLoading = usersState.isLoading,
                        canLoadMore = usersState.canLoadMore,
                        currentUserId = sessionViewModel.currentUser.value?.id,
                        navController = navController,
                        onLoadMore = followListViewModel::loadMore,
                    )
                }
            }
        )
    }
}