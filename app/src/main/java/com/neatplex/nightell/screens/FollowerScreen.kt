package com.neatplex.nightell.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.ShowUsers
import com.neatplex.nightell.model.User
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.viewmodels.SharedViewModel
import com.neatplex.nightell.viewmodels.UserProfileViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowerScreen(navController: NavController, userId: Int, sharedViewModel : SharedViewModel) {

    val userProfileViewModel : UserProfileViewModel = hiltViewModel()
    val usersObserve by userProfileViewModel.userData.observeAsState()
    LaunchedEffect(Unit) {
        userProfileViewModel.fetchUserFollowers(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Followers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                usersObserve.let { result ->
                    when (result) {
                        is Result.Success -> {
                            result.data?.users.let { users ->
                                ShowUsers(users, navController, sharedViewModel)
                            }
                        }
                        is Result.Error -> {
                            // Handle error
                        }
                        is Result.Loading -> {
                            // Show loading indicator
                        }

                        else -> {}
                    }
                }

            }
        }
    )

}


