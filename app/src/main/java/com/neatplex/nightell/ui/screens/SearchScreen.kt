package com.neatplex.nightell.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.ShowPosts
import com.neatplex.nightell.ui.viewmodel.SearchViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@Composable
fun SearchScreen(navController: NavController, sharedViewModel: SharedViewModel, searchViewModel: SearchViewModel = hiltViewModel()) {

    var query by remember { mutableStateOf("") }
    val searchResult by searchViewModel.searchResult.observeAsState()
    val isLoading by searchViewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Search Box
        Row {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it
                    searchViewModel.search(query)},
                label = { Text("Search") },
                trailingIcon = {
                    IconButton(onClick = { searchViewModel.search(query) }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = null
                    )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Trigger search when user hits enter
                        searchViewModel.search(query)
                    }
                )
            )
        }

        // ShowPosts
        ShowPosts(searchResult, navController, sharedViewModel)

        // Show loading indicator if loading
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}