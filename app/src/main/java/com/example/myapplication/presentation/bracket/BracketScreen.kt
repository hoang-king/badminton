package com.example.myapplication.presentation.bracket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplication.R
import com.example.myapplication.domain.model.BracketMatch
import com.example.myapplication.presentation.game.GameViewModel

/**
 * Main Bracket Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BracketScreen(
    navController: NavController,
    gameViewModel: GameViewModel = viewModel(),
    bracketViewModel: BracketViewModel = viewModel()
) {
    val teams by gameViewModel.teams.collectAsState()
    val totalRounds by bracketViewModel.totalRounds.collectAsState()
    val showSaveDialog by bracketViewModel.showSaveDialog.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            bracketViewModel.setTeams(teams)
        }
    }

    // Dialog save history
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { bracketViewModel.closeSaveDialog() },
            title = { Text("Congratulations Champion!") },
            text = { 
                val champion = bracketViewModel.getChampion()
                Column {
                    Text("Do you want to save this tournament result to history?")
                    if (champion != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Winning Team: ${champion.joinToString(", ")}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    bracketViewModel.saveToHistory(context)
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { bracketViewModel.closeSaveDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BracketTopBar(
                hasTeams = teams.isNotEmpty(),
                onBackClick = { navController.navigateUp() },
                onResetClick = { bracketViewModel.resetBracket() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (teams.isEmpty()) {
                EmptyState()
            } else {
                BracketContent(
                    totalRounds = totalRounds,
                    bracketViewModel = bracketViewModel
                )
            }
        }
    }
}

/**
 * Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BracketTopBar(
    hasTeams: Boolean,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Knockout Bracket",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (hasTeams) {
                IconButton(onClick = onResetClick) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset"
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

/**
 * Empty State
 */
@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f), // Thu nhỏ card còn 80% chiều rộng và vẫn căn giữa
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
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "No teams",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(

                    "No teams",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please create teams before starting",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Bracket Content - Scrollable grid
 */
@Composable
private fun BracketContent(
    totalRounds: Int,
    bracketViewModel: BracketViewModel
) {
    // ✅ Collect matches state
    val matches by bracketViewModel.matches.collectAsState()
    val isFinalWon by bracketViewModel.isFinalWon.collectAsState()

    Box(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            for (round in 0 until totalRounds) {
                BracketRoundColumn(
                    round = round,
                    totalRounds = totalRounds,
                    matches = matches.filter { it.roundIndex == round }
                        .sortedBy { it.matchIndex },
                    isFinalWon = isFinalWon,
                    onTeamClick = { matchIndex, winner ->
                        bracketViewModel.selectWinner(round, matchIndex, winner)
                    }
                )
            }
        }
    }

}
/**
 * Bracket Round Column - Vertical list of matches
 */
@Composable
private fun BracketRoundColumn(
    round: Int,
    totalRounds: Int,
    matches: List<BracketMatch>,
    isFinalWon: Boolean,
    onTeamClick: (Int, Int) -> Unit
) {
    // Spacing theo cấp số 2: 16, 32, 64, 128...
    val baseSpacing = 16
    val multiplier = 1 shl round // 2^round
    val spacing = (baseSpacing * multiplier).dp

    Column(
        modifier = Modifier.width(200.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Round header
        RoundHeader(round = round, totalRounds = totalRounds)

        Spacer(modifier = Modifier.height(12.dp))

        // Matches with spacing to align according to binary tree
        matches.forEach { match ->
            if (round > 0) {
                Spacer(modifier = Modifier.height((spacing.value / 2).dp))
            }

            MatchCard(
                match = match,
                isFinal = round == totalRounds - 1,
                isFinalWon = isFinalWon,
                onTeamClick = { winner ->
                    onTeamClick(match.matchIndex, winner)
                }
            )

            if (round > 0) {
                Spacer(modifier = Modifier.height((spacing.value / 2).dp))
            }
        }
    }
}

/**
 * Round Header
 */
@Composable
private fun RoundHeader(round: Int, totalRounds: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = when {
                round == totalRounds - 1 -> "Final"
                round == totalRounds - 2 -> "Semi-Final"
                round == totalRounds - 3 -> "Quarter-Final"
                else -> "Round ${round + 1}"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun MatchCard(
    match: BracketMatch,
    isFinal: Boolean = false,
    isFinalWon: Boolean = false,
    onTeamClick: (Int) -> Unit
) {
    // ✅ Hide card if both teams are null
    if (match.team1 == null && match.team2 == null) {
        Spacer(modifier = Modifier.height(100.dp))
        return
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Team 1
                if (match.team1 != null) {
                    TeamBox(
                        team = match.team1,
                        teamIndex = match.team1Index,
                        isWinner = match.winner == 1,
                        isClickable = match.winner == null,
                        onClick = { onTeamClick(1) }
                    )
                }

                // Divider
                if (match.team1 != null && match.team2 != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Team 2
                if (match.team2 != null) {
                    TeamBox(
                        team = match.team2,
                        teamIndex = match.team2Index,
                        isWinner = match.winner == 2,
                        isClickable = match.winner == null,
                        onClick = { onTeamClick(2) }
                    )
                }
            }
        }

        // Fire animation for final match winner
        if (isFinal && isFinalWon) {
            FireAnimation(modifier = Modifier.align(Alignment.Center))
        }
    }
}
/**
 * Team Box - Clickable team display
 */
@Composable
private fun TeamBox(
    team: List<String>,
    teamIndex: Int?,
    isWinner: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isWinner -> MaterialTheme.colorScheme.primaryContainer
        isClickable -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isWinner -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                width = if (isWinner) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = isClickable) { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Team ${(teamIndex ?: 0) + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isWinner) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                team.forEach { player ->
                    Text(
                        text = player,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (isWinner) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Fire Animation for final match
 */
@Composable
private fun FireAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire))
    
    LottieAnimation(
        composition = composition,
        modifier = modifier
            .size(200.dp),
        iterations = Int.MAX_VALUE
    )
}
