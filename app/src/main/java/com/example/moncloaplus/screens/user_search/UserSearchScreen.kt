package com.example.moncloaplus.screens.user_search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moncloaplus.R
import com.example.moncloaplus.model.User
import com.example.moncloaplus.model.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    viewModel: UserViewModel = hiltViewModel()
) {
    val userList by viewModel.users.collectAsState()
    var query by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val queryWords = query.split(" ").filter { it.isNotEmpty() }

    val filteredUsers = userList.filter { user ->
        queryWords.all { word ->
            listOf(user.firstName, user.firstSurname, user.secondSurname)
                .any { it.contains(word, ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { isActive = filteredUsers.isNotEmpty() },
            active = isActive,
            onActiveChange = { isActive =
                if (isActive) true else {
                    selectedUser = null
                    true
                } },
            placeholder = { Text("Buscar usuario...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        query = ""
                        selectedUser = null
                    }) {
                        Icon(painter = painterResource(R.drawable.cancel_24px),
                            contentDescription = "Borrar búsqueda",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
//            colors = SearchBarDefaults.colors(
//                containerColor = MaterialTheme.colorScheme.secondary,
//                dividerColor = MaterialTheme.colorScheme.tertiary
//            )
        ) {
            if (selectedUser == null && isActive) {
                filteredUsers.forEach { user ->
                    ListItem(
                        headlineContent = { Text("${user.firstName} ${user.firstSurname} ${user.secondSurname}") },
                        overlineContent = { Text(user.city) },
                        supportingContent = {
                            Column {
                                Text(user.degree)
                                Text(user.university)
                            }
                        },
                        leadingContent = {
                            Icon(Icons.Filled.AccountBox, contentDescription = "Usuario")
                        },
                        trailingContent = { Text(user.role.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = "${user.firstName} ${user.firstSurname} ${user.secondSurname}"
                                selectedUser = user
                                isActive = false
                            }
                    )
                    HorizontalDivider()
                }
            }
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))

        selectedUser?.let { user ->
            UserDetailCard(user)
        }
    }
}

@Composable
fun UserDetailCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = user.role.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            }

            Spacer(modifier = Modifier.fillMaxWidth().padding(6.dp))

            Text(
                text = "${user.firstName} ${user.firstSurname} ${user.secondSurname}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.city,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.fillMaxWidth().padding(10.dp))

            Text(
                text = user.degree,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.university,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.fillMaxWidth().padding(10.dp))

            Text(
                text = "Habitación: ${user.roomNumber}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Iniciales: ${user.initials}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

