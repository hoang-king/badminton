package com.example.myapplication.presentation.score

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // ===== MAIN SCORE AREA: 2 halves =====
        Row(modifier = Modifier.fillMaxSize()) {

            // --- TEAM A (Left) ---
            TeamScorePanel(
                teamName = "TEAM A",
                score = scoreA,
                accentColor = NeonGreen,
                accentGlow = NeonGreenGlow,
                containerColor = NeonGreenContainer,
                isLandscape = isLandscape,
                onTapScore = { scoreViewModel.incrementA() },
                onDecrease = { scoreViewModel.decrementA() },
                modifier = Modifier.weight(1f)
            )

            // --- Center divider ---
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                NeonGreen.copy(alpha = 0.4f),
                                Cyan.copy(alpha = 0.5f),
                                NeonGreen.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // --- TEAM B (Right) ---
            TeamScorePanel(
                teamName = "TEAM B",
                score = scoreB,
                accentColor = Cyan,
                accentGlow = CyanGlow,
                containerColor = CyanContainer,
                isLandscape = isLandscape,
                onTapScore = { scoreViewModel.incrementB() },
                onDecrease = { scoreViewModel.decrementB() },
                modifier = Modifier.weight(1f)
            )
        }

        // ===== TOP BAR overlay =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = { navController.navigateUp() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = DarkSurface.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = LightText
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Reset button (center)
            FilledTonalButton(
                onClick = { scoreViewModel.resetGame() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = DarkSurfaceVariant.copy(alpha = 0.85f),
                    contentColor = LightText
                )
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "RESET",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Placeholder to balance layout
            Spacer(modifier = Modifier.size(48.dp))
        }

        // ===== WINNER OVERLAY =====
        AnimatedVisibility(
            visible = winner != null,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(400)
            ),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground.copy(alpha = 0.75f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* consume clicks */ },
                contentAlignment = Alignment.Center
            ) {
                val winnerColor = if (winner == "TEAM A") NeonGreen else Cyan

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Trophy icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        winnerColor.copy(alpha = 0.3f),
                                        winnerColor.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = winnerColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Text(
                        text = winner ?: "",
                        color = winnerColor,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )

                    Text(
                        text = "WINS!",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play again button
                    FilledTonalButton(
                        onClick = { scoreViewModel.resetGame() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = winnerColor.copy(alpha = 0.2f),
                            contentColor = winnerColor
                        )
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "PLAY AGAIN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

// ===== TEAM SCORE PANEL =====
@Composable
fun TeamScorePanel(
    teamName: String,
    score: Int,
    accentColor: Color,
    accentGlow: Color,
    containerColor: Color,
    isLandscape: Boolean,
    onTapScore: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSurface,
                        containerColor.copy(alpha = 0.3f),
                        DarkSurface
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTapScore() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Team name badge
            Surface(
                color = accentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = teamName,
                    color = accentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 8.dp))

            // Large score number
            Text(
                text = "$score",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = if (isLandscape) 220.sp else 120.sp,
                textAlign = TextAlign.Center,
                lineHeight = if (isLandscape) 220.sp else 120.sp
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 8.dp))

            // Tap hint
            Text(
                text = "TAP TO SCORE",
                color = LightTextSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
        }

        // Decrease button (bottom-center)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            FilledIconButton(
                onClick = onDecrease,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DarkSurfaceVariant.copy(alpha = 0.8f),
                    contentColor = accentColor.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
