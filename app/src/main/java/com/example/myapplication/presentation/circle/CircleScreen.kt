package com.example.myapplication.presentation.circle

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Match
import com.example.myapplication.presentation.game.GameViewModel
import com.example.myapplication.presentation.theme.*
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

    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            circleViewModel.setTeams(teams)
        }
    }

    // Dialog save history
    if (showSaveDialog) {
        AlertDialog(
            containerColor = DarkSurfaceVariant,
            titleContentColor = NeonGreen,
            textContentColor = LightText,
            onDismissRequest = { circleViewModel.closeSaveDialog() },
            title = {
                Text(
                    "SAVE RESULT",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            },
            text = { Text("Do you want to save the result to history?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            circleViewModel.saveToHistory(context, matches, teams)
                            circleViewModel.closeSaveDialog()
                            navController.navigate("game") {
                                popUpTo("game") { inclusive = true }
                            }
                        }
                    },
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
                TextButton(onClick = { circleViewModel.closeSaveDialog() }) {
                    Text("Cancel", color = LightTextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp)
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
        containerColor = DarkBackground,
        topBar = {
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
                                        colors = listOf(NeonGreen, Cyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SportsTennis,
                                contentDescription = null,
                                tint = DarkOnPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            "ROUND ROBIN",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = LightText,
                            letterSpacing = 2.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LightText
                        )
                    }
                },
                actions = {
                    if (teams.isNotEmpty()) {
                        IconButton(onClick = { shareSchedule() }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Share",
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
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Please go back and create teams first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightTextSecondary,
                            textAlign = TextAlign.Center
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
                // ===== Overview - Gradient Stats =====
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = CardGlowBorderSubtle,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatCard(
                            value = "${circleViewModel.getTotalTeams()}",
                            label = "TEAMS",
                            accentColor = Cyan
                        )

                        // Vertical divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(60.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            DarkOutline.copy(alpha = 0f),
                                            NeonGreen.copy(alpha = 0.4f),
                                            DarkOutline.copy(alpha = 0f)
                                        )
                                    )
                                )
                        )

                        StatCard(
                            value = "${circleViewModel.getTotalMatches()}",
                            label = "MATCHES",
                            accentColor = NeonGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Match list title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(NeonGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.SportsTennis,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        "MATCH LIST",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = LightText,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Match list
                matches.forEachIndexed { index, match ->
                    MatchCard(
                        match = match,
                        matchIndex = index,
                        onWinnerSelected = { winnerIndex ->
                            circleViewModel.setMatchWinner(match.matchNumber, winnerIndex)
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            value,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = LightTextSecondary,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MatchCard(match: Match, matchIndex: Int, onWinnerSelected: (Int?) -> Unit) {
    val matchColors = listOf(
        listOf(NeonGreen, Cyan),
        listOf(Cyan, ElectricBlue),
        listOf(ElectricBlue, NeonGreen),
        listOf(SportAmber, NeonGreen)
    )
    val colorPair = matchColors[matchIndex % matchColors.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = colorPair.map { it.copy(alpha = 0.2f) }
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Match number badge - gradient
            Surface(
                color = colorPair[0].copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "MATCH ${match.matchNumber}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = colorPair[0],
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                TeamColumn(
                    teamNumber = match.team1Index + 1,
                    players = match.team1,
                    isWinner = match.winnerIndex == 0,
                    isLoser = match.winnerIndex == 1,
                    accentColor = colorPair[0],
                    onClick = {
                        if (match.winnerIndex == 0) onWinnerSelected(null) else onWinnerSelected(0)
                    },
                    modifier = Modifier.weight(1f)
                )

                // VS badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ElectricBlueContainer,
                                    DarkSurfaceHigh
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "VS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue,
                        letterSpacing = 1.sp
                    )
                }

                // Team 2
                TeamColumn(
                    teamNumber = match.team2Index + 1,
                    players = match.team2,
                    isWinner = match.winnerIndex == 1,
                    isLoser = match.winnerIndex == 0,
                    accentColor = colorPair[1],
                    onClick = {
                        if (match.winnerIndex == 1) onWinnerSelected(null) else onWinnerSelected(1)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Winner result
            if (match.winnerIndex != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = NeonGreenContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "TEAM ${if (match.winnerIndex == 0) match.team1Index + 1 else match.team2Index + 1} WINS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen,
                            letterSpacing = 1.sp
                        )
                    }
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
    accentColor: androidx.compose.ui.graphics.Color = Cyan,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .alpha(if (isLoser) 0.35f else 1f)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Team badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isWinner)
                        Brush.linearGradient(colors = listOf(NeonGreen, Cyan))
                    else
                        Brush.linearGradient(colors = listOf(DarkSurfaceHigh, DarkSurfaceBright))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$teamNumber",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = if (isWinner) DarkOnPrimary else accentColor
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "TEAM $teamNumber",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = if (isWinner) NeonGreen else accentColor,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        players.forEach { player ->
            Text(
                player,
                style = MaterialTheme.typography.bodySmall,
                color = LightText,
                textAlign = TextAlign.Center
            )
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
