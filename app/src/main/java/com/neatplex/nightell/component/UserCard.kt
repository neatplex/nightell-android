package com.neatplex.nightell.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@Composable
fun ShowUsers(users: List<User?>?, navController: NavController, viewModel: SharedViewModel) {



    users?.forEach { user ->
        if (user != null) {
            UserCard(user = user){
                if(viewModel.user.value?.id == user.id){
                    navController.navigate("profile")
                }else {
                    navController.navigate("userScreen/${user.id}")
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onUserClicked: (User) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onUserClicked(user)
            },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(user!!.username)
        }
    }
}