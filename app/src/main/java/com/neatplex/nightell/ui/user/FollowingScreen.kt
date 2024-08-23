package com.neatplex.nightell.ui.user

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.UserCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson

@OptIn(ExperimentalMaterial3Api::class)
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, clip = false) // Add shadow to the Box
                        .background(Color.White)
                ) {
                    TopAppBar(
                        title = { Text(text = "Followings") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Transparent to use the Box's background
                        )
                    )
                }
            },
            content = { space ->
                Box(
                    modifier = Modifier.padding(space)
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
                                }
                                if (index == users.size - 1 && !isLoading && userViewModel.canLoadMoreFollowings) {
                                    userViewModel.fetchUserFollowings(
                                        userId,
                                        user.id,
                                        count
                                    )
                                }
                            }
                            if (isLoading) {
                                item { CustomCircularProgressIndicator() }
                            }
                        }
                    )
                }
            }
        )
    }
}