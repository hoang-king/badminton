package com.example.myapplication.presentation.score

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadmintonScoreScreen(
    navController: NavController,
    scoreViewModel: ScoreViewModel = viewModel()
) {
    val scoreA by scoreViewModel.scoreA.collectAsState()
    val scoreB by scoreViewModel.scoreB.collectAsState()
    val winner by scoreViewModel.winner.collectAsState()

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
                                        colors = listOf(SportAmber, NeonGreen)
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
                            "SCOREBOARD",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = LightText,
                            letterSpacing = 2.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LightText
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ==== TOP ROW: RESET BUTTON AND DECREMENT ====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DECREASE LEFT
                ScoreButton(
                    isAdd = false,
                    onClick = { scoreViewModel.decrementA() },
                    modifier = Modifier.weight(1f)
                )

                // RESET GAME
                Box(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DarkSurfaceVariant,
                                    DarkSurfaceHigh,
                                    DarkSurfaceVariant
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    NeonGreen.copy(alpha = 0.2f),
                                    Cyan.copy(alpha = 0.3f),
                                    NeonGreen.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .clickable { scoreViewModel.resetGame() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RESET GAME",
                        color = NeonGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        letterSpacing = 3.sp
                    )
                }

                // DECREASE RIGHT
                ScoreButton(
                    isAdd = false,
                    onClick = { scoreViewModel.decrementB() },
                    modifier = Modifier.weight(1f)
                )
            }

            // WINNER BANNER
            if (winner != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonGreen, Cyan, ElectricBlue)
                            )
                        )
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = DarkOnPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "$winner WINNER!",
                            color = DarkOnPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        )
                    }
                }
            }

            // ==== BOTTOM ROW: SCORES + INCREMENT ====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {
                // INCREASE LEFT
                ScoreButton(
                    isAdd = true,
                    onClick = { scoreViewModel.incrementA() },
                    modifier = Modifier.weight(1f)
                )

                // PLAYER 1 SCORE
                PlayerScoreCard(
                    playerName = "PLAYER 1",
                    score = scoreA,
                    accentColor = NeonGreen,
                    modifier = Modifier.weight(2f)
                )

                // Divider giữa 2 player
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DarkOutline.copy(alpha = 0f),
                                    NeonGreen.copy(alpha = 0.3f),
                                    Cyan.copy(alpha = 0.3f),
                                    DarkOutline.copy(alpha = 0f)
                                )
                            )
                        )
                )

                // PLAYER 2 SCORE
                PlayerScoreCard(
                    playerName = "PLAYER 2",
                    score = scoreB,
                    accentColor = Cyan,
                    modifier = Modifier.weight(2f)
                )

                // INCREASE RIGHT
                ScoreButton(
                    isAdd = true,
                    onClick = { scoreViewModel.incrementB() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(DarkSurface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Player name badge
        Surface(
            color = accentColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = playerName,
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Large score - Neon
        Text(
            text = "$score",
            color = accentColor,
            fontWeight = FontWeight.Black,
            fontSize = if (isLandscape) 200.sp else 100.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ScoreButton(
    isAdd: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isAdd) NeonGreenContainer else DarkSurfaceVariant
    val iconColor = if (isAdd) NeonGreen else ErrorRed

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isAdd) Icons.Default.Add else Icons.Default.Remove,
            contentDescription = if (isAdd) "+" else "-",
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
    }
}
