package com.example.myapplication

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quản lý trận đấu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
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

            // Card nhập danh sách người chơi
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Danh sách người chơi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = playerInput,
                        onValueChange = gameViewModel::setPlayerInput,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("Nhập tên người chơi (mỗi dòng một tên)") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // RANDOM ĐỘI
                        Button(
                            onClick = { gameViewModel.randomTeams() },
                            modifier = Modifier.weight(1f),
                            enabled = playerInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Icon(Icons.Default.Shuffle, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Random đội")
                        }

                        // SHARE SHEET
                        Button(
                            onClick = {
                                val message = gameViewModel.getShareMessage()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ với")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = teams.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Icon(Icons.Default.Share, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }
                    }
                }
            }

            // Card chức năng
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Chức năng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // BRACKET BUTTON
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("bracket") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bracket")
                        }

                        // SCORE BUTTON
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("Score") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Score")
                        }
                    }

                    // CIRCLE BUTTON
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("circle") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Circle")
                    }
                }
            }

            // Hiển thị danh sách đội
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Group, null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                "Các đội đã tạo (${teams.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                            )
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
                    "Đội $teamNumber",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "(${players.size} người)",
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
