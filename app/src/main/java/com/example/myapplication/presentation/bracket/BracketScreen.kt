package com.example.myapplication.presentation.bracket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplication.R
import com.example.myapplication.domain.model.BracketMatch
import com.example.myapplication.presentation.game.GameViewModel
import com.example.myapplication.presentation.theme.*

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
    val selectedMatchForScore by bracketViewModel.selectedMatchForScore.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            bracketViewModel.setTeams(teams)
        }
    }

    // Score Input Dialog
    selectedMatchForScore?.let { match ->
        com.example.myapplication.presentation.circle.ScoreInputDialog(
            team1Name = "T${(match.team1Index ?: 0) + 1}",
            team2Name = "T${(match.team2Index ?: 0) + 1}",
            initialIsBo3 = match.isBo3,
            initialSetScores1 = match.setScores1,
            initialSetScores2 = match.setScores2,
            onDismiss = { bracketViewModel.closeScoreDialog() },
            onConfirm = { s1, s2, isBo3, sets1, sets2 ->
                bracketViewModel.updateBracketResult(
                    match.roundIndex, 
                    match.matchIndex, 
                    s1, s2, isBo3, sets1, sets2
                )
                bracketViewModel.closeScoreDialog()
            }
        )
    }

    // Dialog save history
    if (showSaveDialog) {
        AlertDialog(
            containerColor = DarkSurfaceVariant,
            titleContentColor = NeonGreen,
            textContentColor = LightText,
            onDismissRequest = { bracketViewModel.closeSaveDialog() },
            title = {
                Text(
                    "CHAMPION!",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            },
            text = {
                val champion = bracketViewModel.getChampion()
                Column {
                    Text("Save this tournament result to history?")
                    if (champion != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = NeonGreenContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    champion.joinToString(", "),
                                    fontWeight = FontWeight.Black,
                                    color = NeonGreen
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { bracketViewModel.saveToHistory(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = DarkOnPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("SAVE", fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { bracketViewModel.closeSaveDialog() }) {
                    Text("Cancel", color = LightTextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = DarkBackground,
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
                .background(DarkBackground)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricBlue, Cyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = DarkOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    "KNOCKOUT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = LightText,
                    letterSpacing = 2.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = LightText
                )
            }
        },
        actions = {
            if (hasTeams) {
                IconButton(onClick = onResetClick) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Cyan
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface,
            titleContentColor = LightText,
            navigationIconContentColor = LightText,
            actionIconContentColor = Cyan
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
                .fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = LightTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "NO TEAMS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = LightText,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Please create teams before starting",
                    color = LightTextSecondary,
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
                    bracketViewModel = bracketViewModel
                )
            }
        }
    }
}

/**
 * Bracket Round Column
 */
@Composable
private fun BracketRoundColumn(
    round: Int,
    totalRounds: Int,
    matches: List<BracketMatch>,
    isFinalWon: Boolean,
    bracketViewModel: BracketViewModel
) {
    val baseSpacing = 16
    val multiplier = 1 shl round
    val spacing = (baseSpacing * multiplier).dp

    Column(
        modifier = Modifier.width(220.dp), // Increased width for score input
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        RoundHeader(round = round, totalRounds = totalRounds)
        Spacer(modifier = Modifier.height(12.dp))

        matches.forEach { match ->
            if (round > 0) {
                Spacer(modifier = Modifier.height((spacing.value / 2).dp))
            }

            MatchCard(
                match = match,
                isFinal = round == totalRounds - 1,
                isFinalWon = isFinalWon,
                onClick = { bracketViewModel.openScoreDialog(match) }
            )

            if (round > 0) {
                Spacer(modifier = Modifier.height((spacing.value / 2).dp))
            }
        }
    }
}

/**
 * Round Header - Gradient
 */
@Composable
private fun RoundHeader(round: Int, totalRounds: Int) {
    val headerColors = when {
        round == totalRounds - 1 -> listOf(SportAmber, NeonGreen)
        round == totalRounds - 2 -> listOf(NeonGreen, Cyan)
        else -> listOf(Cyan, ElectricBlue)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    colors = headerColors.map { it.copy(alpha = 0.2f) }
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = headerColors.map { it.copy(alpha = 0.3f) }
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                round == totalRounds - 1 -> "🏆 FINAL"
                round == totalRounds - 2 -> "SEMI-FINAL"
                round == totalRounds - 3 -> "QUARTER-FINAL"
                else -> "ROUND ${round + 1}"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = headerColors[0],
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun MatchCard(
    match: BracketMatch,
    isFinal: Boolean = false,
    isFinalWon: Boolean = false,
    onClick: () -> Unit
) {
    if (match.team1 == null && match.team2 == null) {
        Spacer(modifier = Modifier.height(100.dp))
        return
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .border(
                    width = 1.dp,
                    color = if (isFinal && isFinalWon) NeonGreen.copy(alpha = 0.3f) else DarkOutline,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(12.dp)
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
                        score = match.score1
                    )
                }

                // Divider
                if (match.team1 != null && match.team2 != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .height(1.dp)
                            .background(DarkOutline.copy(alpha = 0.2f))
                    )
                }

                // Team 2
                if (match.team2 != null) {
                    TeamBox(
                        team = match.team2,
                        teamIndex = match.team2Index,
                        isWinner = match.winner == 2,
                        score = match.score2
                    )
                }
            }
        }

        // Fire animation
        if (isFinal && isFinalWon) {
            FireAnimation(modifier = Modifier.align(Alignment.Center))
        }
    }
}

/**
 * Team Box
 */
@Composable
private fun TeamBox(
    team: List<String>,
    teamIndex: Int?,
    isWinner: Boolean,
    score: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isWinner) NeonGreen.copy(alpha = 0.1f) else Color.Transparent)
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isWinner) NeonGreen else DarkSurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(teamIndex ?: 0) + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = if (isWinner) DarkOnPrimary else LightTextSecondary,
                    fontSize = 9.sp
                )
            }
            Text(
                text = team.firstOrNull() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = if (isWinner) NeonGreen else LightText,
                maxLines = 1,
                fontSize = 10.sp
            )
        }

        Text(
            text = "${score ?: 0}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Black,
            color = if (isWinner) NeonGreen else Cyan,
            fontSize = 11.sp
        )
    }
}

/**
 * Fire Animation
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
