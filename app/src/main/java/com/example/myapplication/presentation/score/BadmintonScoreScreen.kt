package com.example.myapplication.presentation.score

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Badminton Score Counter",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF90CAF9)),
                 navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background( Color(0xFF90CAF9))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ==== HÀNG TRÊN: NÚT RESET VÀ NÚT TĂNG/GIẢM ĐIỂM NGANG ====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ô GIẢM ĐIỂM BÊN TRÁI
                ScoreButton(
                    symbol = "-",
                    onClick = { scoreViewModel.decrementA() },
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    bgColor = Color(0xFF90CAF9)
                )

                // NÚT RESET GAME LỚN
                Box(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxHeight()
                        .background(Color(0xFF90CAF9))
                        .clickable { scoreViewModel.resetGame() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RESET GAME",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                }

                // Ô GIẢM ĐIỂM BÊN PHẢI
                ScoreButton(
                    symbol = "-",
                    onClick = { scoreViewModel.decrementB() },
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    bgColor = Color(0xFF90CAF9)
                )
            }
            if (winner != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Green)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆 $winner WINNER!",
                        color = Color.Black,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ==== HÀNG DƯỚI: THẺ ĐIỂM VÀ NÚT TĂNG ĐIỂM NGANG ====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {
                // Ô TĂNG ĐIỂM BÊN TRÁI
                ScoreButton(
                    symbol = "+",
                    onClick = { scoreViewModel.incrementA() },
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    bgColor = Color(0xFF90CAF9)
                )

                // THẺ ĐIỂM PLAYER 1
                PlayerScoreCard(
                    playerName = "Player 1",
                    score = scoreA,
                    modifier = Modifier.weight(2f),
                    color = Color(0xFF90CAF9)
                )

                // THẺ ĐIỂM PLAYER 2
                PlayerScoreCard(
                    playerName = "Player 2",
                    score = scoreB,
                    modifier = Modifier.weight(2f),
                    color = Color(0xFF90CAF9)
                )

                // Ô TĂNG ĐIỂM BÊN PHẢI
                ScoreButton(
                    symbol = "+",
                    onClick = { scoreViewModel.incrementB() },
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    bgColor = Color(0xFF90CAF9)
                )
            }
        }
    }
}

// Composable để tạo các ô điểm lớn
@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    modifier: Modifier = Modifier,
    color: Color
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tên người chơi
        Text(
            text = playerName,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Điểm số lớn
        Text(
            text = "$score",
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = if (isLandscape) 200.sp else 100.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Composable cho các nút + và -
@Composable
fun ScoreButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = color,
            fontSize = 64.sp,
            fontWeight = FontWeight.Black
        )
    }
}
