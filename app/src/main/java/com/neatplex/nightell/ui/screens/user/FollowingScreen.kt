package com.neatplex.nightell.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import com.neatplex.nightell.ui.component.common.AppTopBar
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FollowingScreen(navController: NavController,
                    userId: Int,
                    sessionViewModel: SessionViewModel,
                    followListViewModel: FollowListViewModel = hiltViewModel()) {

    val usersState by followListViewModel.users.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        followListViewModel.start(userId, FollowListType.FOLLOWINGS)
    }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.followings), onBack = { navController.popBackStack() }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            UsersList(
                users = usersState.items,
                isLoading = usersState.isLoading,
                canLoadMore = usersState.canLoadMore,
                currentUserId = sessionViewModel.currentUser.value?.id,
                navController = navController,
                onLoadMore = followListViewModel::loadMore,
                emptyText = "Not following anyone yet",
            )
        }
    }
}
