package com.example.myapplication.presentation.game

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    navController: NavController
) {
    val playerInput by gameViewModel.playerInput.collectAsState()
    val teams by gameViewModel.teams.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    var showTopMenu by remember { mutableStateOf(false) }
    var showTeamsMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Match Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showTopMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Main menu")
                    }
                    DropdownMenu(
                        expanded = showTopMenu,
                        onDismissRequest = { showTopMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Score") },
                            leadingIcon = { Icon(Icons.Default.Star, null) },
                            onClick = {
                                showTopMenu = false
                                navController.navigate("score")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("History") },
                            leadingIcon = { Icon(Icons.Default.History, null) },
                            onClick = {
                                showTopMenu = false
                                navController.navigate("history")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Card for entering player list
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Player List",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Reset Button
                        TextButton(
                            onClick = { gameViewModel.resetAll() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset")
                        }
                    }

                    OutlinedTextField(
                        value = playerInput,
                        onValueChange = { gameViewModel.setPlayerInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("Enter player names (Example:\n- Nguyen Van A\n- Tran Thi B)") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // RANDOM TEAM - Full Width
                    Button(
                        onClick = { 
                            gameViewModel.randomTeams()
                            keyboardController?.hide() // Hide keyboard
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Random Team")
                    }
                }
            }

            // Display team list
            if (teams.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Group, null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                "Created Teams (${teams.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Menu Button moved here
                            Box {
                                IconButton(onClick = { showTeamsMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Team menu")
                                }
                                DropdownMenu(
                                    expanded = showTeamsMenu,
                                    onDismissRequest = { showTeamsMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Bracket") },
                                        leadingIcon = { Icon(Icons.Default.AccountTree, null) },
                                        onClick = {
                                            showTeamsMenu = false
                                            navController.navigate("bracket")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Circle") },
                                        leadingIcon = { Icon(Icons.Default.Circle, null) },
                                        onClick = {
                                            showTeamsMenu = false
                                            navController.navigate("circle")
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Share") },
                                        leadingIcon = { Icon(Icons.Default.Share, null) },
                                        onClick = {
                                            showTeamsMenu = false
                                            val message = gameViewModel.getShareMessage()
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, message)
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "Share with")
                                            context.startActivity(shareIntent)
                                        }
                                    )
                                }
                            }
                        }

                        teams.forEachIndexed { index, team ->
                            TeamCard(teamNumber = index + 1, players = team)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TeamCard(
    teamNumber: Int,
    players: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "$teamNumber",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Text(
                    "Team $teamNumber",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "(${players.size} players)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                players.forEach { player ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            player,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
