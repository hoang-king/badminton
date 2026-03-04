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
    val selectedMatchForScore by circleViewModel.selectedMatchForScore.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(teams) {
        if (teams.isNotEmpty()) {
            circleViewModel.setTeams(teams)
        }
    }

    // Score Input Dialog
    selectedMatchForScore?.let { match ->
        ScoreInputDialog(
            team1Name = "Team ${match.team1Index + 1}",
            team2Name = "Team ${match.team2Index + 1}",
            initialIsBo3 = match.isBo3,
            initialSetScores1 = match.setScores1,
            initialSetScores2 = match.setScores2,
            onDismiss = { circleViewModel.closeScoreDialog() },
            onConfirm = { s1, s2, isBo3, sets1, sets2 ->
                circleViewModel.updateMatchResult(match.matchNumber, s1, s2, isBo3, sets1, sets2)
                circleViewModel.closeScoreDialog()
            }
        )
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
                        onClick = { circleViewModel.openScoreDialog(match) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreInputDialog(
    team1Name: String,
    team2Name: String,
    initialIsBo3: Boolean,
    initialSetScores1: List<Int?>,
    initialSetScores2: List<Int?>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Boolean, List<Int?>, List<Int?>) -> Unit
) {
    var isBo3 by remember { mutableStateOf(initialIsBo3) }
    var setScores1 by remember { mutableStateOf(initialSetScores1) }
    var setScores2 by remember { mutableStateOf(initialSetScores2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceVariant,
        title = {
            Text("ENTER SCORE", fontWeight = FontWeight.Black, color = NeonGreen)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Toggle Bo3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Match Format", color = LightText, fontWeight = FontWeight.Bold)
                    Row {
                        FilterChip(
                            selected = !isBo3,
                            onClick = { isBo3 = false },
                            label = { Text("1 Round") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cyan,
                                selectedLabelColor = DarkOnPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isBo3,
                            onClick = { isBo3 = true },
                            label = { Text("Bo3") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = DarkOnPrimary
                            )
                        )
                    }
                }

                // Set Inputs
                val setLimit = if (isBo3) 3 else 1
                for (i in 0 until setLimit) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("SET ${i + 1}", style = MaterialTheme.typography.labelMedium, color = LightTextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreField(
                                value = setScores1.getOrNull(i)?.toString() ?: "",
                                label = team1Name,
                                onValueChange = { val newList = setScores1.toMutableList(); newList[i] = it.toIntOrNull(); setScores1 = newList },
                                modifier = Modifier.weight(1f)
                            )
                            Text("VS", fontWeight = FontWeight.Black, color = ElectricBlue)
                            ScoreField(
                                value = setScores2.getOrNull(i)?.toString() ?: "",
                                label = team2Name,
                                onValueChange = { val newList = setScores2.toMutableList(); newList[i] = it.toIntOrNull(); setScores2 = newList },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s1 = if (isBo3) {
                        var wins = 0
                        for (i in 0 until 3) {
                            val sc1 = setScores1.getOrNull(i) ?: 0
                            val sc2 = setScores2.getOrNull(i) ?: 0
                            if (sc1 > sc2) wins++
                        }
                        wins
                    } else (setScores1.firstOrNull() ?: 0)
                    
                    val s2 = if (isBo3) {
                        var wins = 0
                        for (i in 0 until 3) {
                            val sc1 = setScores1.getOrNull(i) ?: 0
                            val sc2 = setScores2.getOrNull(i) ?: 0
                            if (sc2 > sc1) wins++
                        }
                        wins
                    } else (setScores2.firstOrNull() ?: 0)

                    onConfirm(s1, s2, isBo3, setScores1, setScores2)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DarkOnPrimary)
            ) {
                Text("CONFIRM", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = LightTextSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreField(value: String, label: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp) },
        modifier = modifier,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = DarkOutline,
            focusedContainerColor = DarkSurfaceHigh,
            unfocusedContainerColor = DarkSurfaceHigh
        ),
        singleLine = true
    )
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
private fun MatchCard(
    match: Match, 
    matchIndex: Int, 
    onClick: () -> Unit
) {
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
            .clickable(onClick = onClick)
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
            // Match number badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colorPair[0].copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "MATCH ${match.matchNumber}${if (match.isBo3) " (Bo3)" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = colorPair[0],
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
                
                if (match.winnerIndex != null) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                    modifier = Modifier.weight(1f)
                )

                // Score Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${match.score1 ?: 0} - ${match.score2 ?: 0}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = if (match.winnerIndex != null) NeonGreen else LightText
                    )
                    Text("VS", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                }

                // Team 2
                TeamColumn(
                    teamNumber = match.team2Index + 1,
                    players = match.team2,
                    isWinner = match.winnerIndex == 1,
                    isLoser = match.winnerIndex == 0,
                    accentColor = colorPair[1],
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamColumn(
    teamNumber: Int,
    players: List<String>,
    isWinner: Boolean = false,
    isLoser: Boolean = false,
    accentColor: androidx.compose.ui.graphics.Color = Cyan,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .alpha(if (isLoser) 0.5f else 1f)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isWinner) NeonGreen else DarkSurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$teamNumber",
                fontWeight = FontWeight.Black,
                color = if (isWinner) DarkOnPrimary else accentColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "TEAM $teamNumber",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = if (isWinner) NeonGreen else LightText,
        )
        players.forEach { player ->
            Text(
                player,
                style = MaterialTheme.typography.bodySmall,
                color = LightTextSecondary,
                maxLines = 1
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
