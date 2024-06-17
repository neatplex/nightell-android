package com.neatplex.nightell.ui.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FollowerScreen(navController: NavController, userId: Int, sharedViewModel : SharedViewModel) {

    val userViewModel : UserViewModel = hiltViewModel()
    val usersObserve by userViewModel.usersList.observeAsState()
    LaunchedEffect(Unit) {
        userViewModel.fetchUserFollowers(userId)
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


