package com.neatplex.nightell.ui.screens.user

import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.common.EmptyState
import androidx.compose.material3.Scaffold
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.user.UserCard
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowerScreen(
    navController: NavController,
    userId: Int,
    sessionViewModel: SessionViewModel,
    followListViewModel: FollowListViewModel = hiltViewModel(),
) {
    val usersState by followListViewModel.users.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        followListViewModel.start(userId, FollowListType.FOLLOWERS)
    }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.followers), onBack = { navController.popBackStack() }) },
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
                emptyText = "No followers yet",
            )
        }
    }
}

/** Kept for screens that still use it; prefer [AppTopBar]. */
@Composable
fun AppBarWithTitle(navController: NavController, title: String) {
    AppTopBar(title = title, onBack = { navController.popBackStack() })
}

@Composable
fun UsersList(
    users: List<User>,
    isLoading: Boolean,
    canLoadMore: Boolean,
    currentUserId: Int?,
    navController: NavController,
    onLoadMore: () -> Unit,
    emptyText: String = "Nobody here yet",
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        if (users.isEmpty() && !isLoading) {
            item { EmptyState(icon = Icons.Rounded.Person, title = emptyText) }
        }
        itemsIndexed(users, key = { _, user -> user.id }) { index, user ->
            UserCard(user = user) { selectedUser ->
                if (currentUserId != selectedUser.id) {
                    navController.navigate(Routes.user(selectedUser.id))
                }
            }
            if (index == users.lastIndex && !isLoading && canLoadMore) {
                LaunchedEffect(user.id) { onLoadMore() }
            }
        }
        if (isLoading) {
            item { CustomCircularProgressIndicator() }
        }
    }
}
