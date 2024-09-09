package com.neatplex.nightell.ui.screens.user

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.neatplex.nightell.ui.component.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.UserCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowerScreen(
    navController: NavController,
    userId: Int,
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel = hiltViewModel() // Inject the ViewModel
) {
    val users by userViewModel.usersList.observeAsState(emptyList())
    val isLoading by userViewModel.isLoading.observeAsState(false)
    val count = 20

    // Effect for fetching user followers when the screen is loaded
    LaunchedEffect(Unit) {
        userViewModel.fetchUserFollowers(userId, null, count)
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
    sharedViewModel: SharedViewModel,
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Int,
    count: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 56.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
            itemsIndexed(users) { index, user ->
                UserCard(user = user) { selectedUser ->
                    if (sharedViewModel.user.value?.id != selectedUser.id) {
                        val userJson = selectedUser.toJson()
                        navController.navigate("userScreen/${Uri.encode(userJson)}")
                    }
                    // Fetch more followers if at the end of the list
                    if (index == users.size - 1 && !isLoading && userViewModel.canLoadMoreFollowers) {
                        userViewModel.fetchUserFollowers(userId, selectedUser.id, count)
                    }
                }
            }
            if (isLoading) {
                item { CustomCircularProgressIndicator() }
            }
        }
    )
}
