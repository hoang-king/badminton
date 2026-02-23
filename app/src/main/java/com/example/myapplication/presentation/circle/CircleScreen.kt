package com.example.myapplication.presentation.circle

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Match
import com.example.myapplication.presentation.game.GameViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleScreen(
    navController: NavController,
    gameViewModel: GameViewModel = viewModel(),
    circleViewModel: CircleViewModel = viewModel()
) {
    val teams by gameViewModel.teams.collectAsState()
    val matches by circleViewModel.matches.collectAsState()
    val showSaveDialog by circleViewModel.showSaveDialog.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Pass teams from GameViewModel to CircleViewModel
    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            circleViewModel.setTeams(teams)
        }
    }

    // Dialog save history
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { circleViewModel.closeSaveDialog() },
            title = { Text("Save Result") },
            text = { Text("Do you want to save the result to history?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        circleViewModel.saveToHistory(context, matches, teams)
                        circleViewModel.closeSaveDialog()
                        // Navigate back to match management screen
                        navController.navigate("game") {
                            popUpTo("game") { inclusive = true }
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { circleViewModel.closeSaveDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    fun shareSchedule() {
        val scheduleText = buildScheduleText(matches, teams)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, scheduleText)
        }
        context.startActivity(Intent.createChooser(intent, "Share tournament schedule"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Round Robin ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (teams.isNotEmpty()) {
                        IconButton(onClick = { shareSchedule() }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Share tournament schedule"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (teams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.5f), // Shrink card to 80% width and center
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "⚠️",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Teams",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Please go back to the main screen and create teams first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Overview Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(
                            value = "${circleViewModel.getTotalTeams()}",
                            label = "Teams",
                            icon = "👥"
                        )

                        VerticalDivider(
                            modifier = Modifier.height(60.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                        )

                        StatCard(
                            value = "${circleViewModel.getTotalMatches()}",
                            label = "Matches",
                            icon = "🏸"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Match list title
                Text(
                    "📋 Match list:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Match list
                matches.forEach { match ->
                    MatchCard(
                        match = match,
                        onWinnerSelected = { winnerIndex ->
                            circleViewModel.setMatchWinner(match.matchNumber, winnerIndex)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MatchCard(match: Match, onWinnerSelected: (Int?) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Match number badge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    "Trận ${match.matchNumber}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Đội 1
                TeamColumn(
                    teamNumber = match.team1Index + 1,
                    players = match.team1,
                    isWinner = match.winnerIndex == 0,
                    isLoser = match.winnerIndex == 1,
                    onClick = { 
                        if (match.winnerIndex == 0) onWinnerSelected(null) else onWinnerSelected(0)
                    },
                    modifier = Modifier.weight(1f)
                )

                // VS divider
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "VS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                // Đội 2
                TeamColumn(
                    teamNumber = match.team2Index + 1,
                    players = match.team2,
                    isWinner = match.winnerIndex == 1,
                    isLoser = match.winnerIndex == 0,
                    onClick = { 
                        if (match.winnerIndex == 1) onWinnerSelected(null) else onWinnerSelected(1)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Hiển thị thông báo kết quả
            if (match.winnerIndex != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "✓ Đội ${if (match.winnerIndex == 0) match.team1Index + 1 else match.team2Index + 1} thắng",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamColumn(
    teamNumber: Int,
    players: List<String>,
    isWinner: Boolean = false,
    isLoser: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .alpha(if (isLoser) 0.4f else 1f)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Team $teamNumber",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        players.forEach { player ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "•",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    player,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun buildScheduleText(matches: List<Match>, teams: List<List<String>>): String {
    val builder = StringBuilder()
    builder.append("📋 ROUND ROBIN TOURNAMENT SCHEDULE\n")
    builder.append("=" .repeat(50)).append("\n\n")
    builder.append("👥 Teams: ${teams.size}\n")
    builder.append("🏸 Total Matches: ${matches.size}\n\n")
    
    matches.forEach { match ->
        builder.append("Match ${match.matchNumber}:\n")
        builder.append("  Team ${match.team1Index + 1}: ${match.team1.joinToString(", ")}\n")
        builder.append("  VS\n")
        builder.append("  Team ${match.team2Index + 1}: ${match.team2.joinToString(", ")}\n")
        builder.append("\n")
    }
    
    return builder.toString()
}
