package com.neatplex.nightell.ui.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.user.UserCard
import com.neatplex.nightell.ui.theme.AppTheme
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

    AppTheme {
        Scaffold(
            topBar = {
                AppBarWithTitle(navController = navController, title = stringResource(R.string.followers))
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithTitle(navController: NavController, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, clip = false)
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            )
        )
    }
}

@Composable
fun UsersList(
    users: List<User>,
    isLoading: Boolean,
    canLoadMore: Boolean,
    currentUserId: Int?,
    navController: NavController,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 56.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
            itemsIndexed(users) { index, user ->
                UserCard(user = user) { selectedUser ->
                    if (currentUserId != selectedUser.id) {
                        navController.navigate(Routes.user(selectedUser.id))
                    }
                }
                if (index == users.size - 1 && !isLoading && canLoadMore) {
                    onLoadMore()
                }
            }
            if (isLoading) {
                item { CustomCircularProgressIndicator() }
            }
        }
    )
}
