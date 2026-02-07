package com.example.myapplication

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadmintonScoreScreen(navController: NavController) {
    // Sử dụng các biến trạng thái hiện có của bạn
    var scoreA by rememberSaveable { mutableStateOf(0) }
    var scoreB by rememberSaveable { mutableStateOf(0) }
//    // Giữ lại logic của bạn cho trường hợp cần dùng sau này
//    var setA by remember { mutableStateOf(0) }
//    var setB by remember { mutableStateOf(0) }
    var winner by rememberSaveable { mutableStateOf<String?>(null) }

    fun resetGame() {
        scoreA = 0
        scoreB = 0
        // setA = 0
        // setB = 0
        winner = null
    }
    fun checkWinner() {
        if (scoreA >= 21 && scoreA - scoreB >= 2) {
            winner = "Player 1"
        } else if (scoreB >= 21 && scoreB - scoreA >= 2) {
            winner = "Player 2"
        }
    }

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
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF90CAF9)), // Thanh topbar màu đen
                // Navigation icon được loại bỏ để đơn giản hóa giao diện theo hình
                // Nhưng có thể giữ lại nếu bạn muốn:
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
                .background( Color(0xFF90CAF9)) // Nền đen theo hình ảnh bao quanh
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    onClick = { if (scoreA > 0) scoreA-- },
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
                        .clickable { resetGame() },
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
                    onClick = { if (scoreB > 0) scoreB-- },
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
                    .weight(3f) // Cung cấp không gian lớn hơn cho phần điểm số
            ) {
                // Ô TĂNG ĐIỂM BÊN TRÁI
                ScoreButton(
                    symbol = "+",
                    onClick = {
                        if (winner == null) {
                            scoreA++
                            checkWinner()
                        }
                    },
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
                    onClick = {
                        if (winner == null) {
                            scoreB++
                            checkWinner()
                        }
                    },
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
    color: Color, // Màu chữ/biểu tượng
    bgColor: Color // Màu nền
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
            fontSize = 64.sp, // Kích thước lớn cho các nút + và -
            fontWeight = FontWeight.Black
        )
    }
}