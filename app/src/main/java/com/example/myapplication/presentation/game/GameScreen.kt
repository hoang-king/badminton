package com.example.myapplication.presentation.game

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.usecase.RandomTeamsUseCase
import com.example.myapplication.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    navController: NavController
) {
    val playerInput by gameViewModel.playerInput.collectAsState()
    val femaleInput by gameViewModel.femaleInput.collectAsState()
    val gameMode by gameViewModel.gameMode.collectAsState()
    val teams by gameViewModel.teams.collectAsState()

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    var showTopMenu by remember { mutableStateOf(false) }
    var showTeamsMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Neon badge icon
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
                            "MATCH",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = LightText,
                            letterSpacing = 2.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showTopMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Main menu", tint = LightTextSecondary)
                    }
                    DropdownMenu(
                        expanded = showTopMenu,
                        onDismissRequest = { showTopMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Score") },
                            leadingIcon = { Icon(Icons.Default.Star, null, tint = SportAmber) },
                            onClick = {
                                showTopMenu = false
                                navController.navigate("score")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("History") },
                            leadingIcon = { Icon(Icons.Default.History, null, tint = Cyan) },
                            onClick = {
                                showTopMenu = false
                                navController.navigate("history")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = LightText,
                    actionIconContentColor = LightText
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ====== Card nhập danh sách người chơi ======
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = CardGlowBorderSubtle,
                        shape = RoundedCornerShape(18.dp)
                    ),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Gradient icon badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            "PLAYER LIST",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = LightText,
                            letterSpacing = 1.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Reset Button với viền đỏ
                        OutlinedButton(
                            onClick = { gameViewModel.resetAll() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = Brush.horizontalGradient(listOf(ErrorRed.copy(alpha = 0.5f), ErrorRed.copy(alpha = 0.3f)))
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                        Text(
                            "GAME MODE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = LightText,
                            letterSpacing = 1.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RandomTeamsUseCase.GameMode.entries.forEach { mode ->
                                val isSelected = gameMode == mode
                                val label = when (mode) {
                                    RandomTeamsUseCase.GameMode.SINGLES -> "Đơn"
                                    RandomTeamsUseCase.GameMode.DOUBLES -> "Đôi"
                                    RandomTeamsUseCase.GameMode.MIXED_DOUBLES -> "Nam/Nữ"
                                }
                                
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { gameViewModel.setGameMode(mode) },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonGreenContainer,
                                        selectedLabelColor = NeonGreen,
                                        containerColor = DarkSurfaceVariant.copy(alpha = 0.3f),
                                        labelColor = LightTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = DarkOutline,
                                        selectedBorderColor = NeonGreen
                                    )
                                )
                            }
                        }

                        val primaryPlaceholder = when (gameMode) {
                            RandomTeamsUseCase.GameMode.SINGLES -> "Danh sách người chơi"
                            RandomTeamsUseCase.GameMode.DOUBLES -> "Danh sách người chơi"
                            RandomTeamsUseCase.GameMode.MIXED_DOUBLES -> "Danh sách Nam"
                        }

                        OutlinedTextField(
                            value = playerInput,
                            onValueChange = { gameViewModel.setPlayerInput(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            label = { Text(primaryPlaceholder, color = LightTextSecondary) },
                            placeholder = {
                                Text(
                                    "Ví dụ:\n- Nguyễn Văn A\n- Trần Văn B",
                                    color = LightTextSecondary.copy(alpha = 0.6f)
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = LightText,
                                unfocusedTextColor = LightText,
                                cursorColor = NeonGreen,
                                focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.3f)
                            )
                        )

                        if (gameMode == RandomTeamsUseCase.GameMode.MIXED_DOUBLES) {
                            OutlinedTextField(
                                value = femaleInput,
                                onValueChange = { gameViewModel.setFemaleInput(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                label = { Text("Danh sách Nữ", color = LightTextSecondary) },
                                placeholder = {
                                    Text(
                                        "Ví dụ:\n- Trần Thị B\n- Lê Thị C",
                                        color = LightTextSecondary.copy(alpha = 0.6f)
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan,
                                    unfocusedBorderColor = DarkOutline,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    cursorColor = Cyan,
                                    focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                                    unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.3f)
                                )
                            )
                        }

                    // ===== RANDOM TEAM Button - Gradient Neon =====
                    Button(
                        onClick = {
                            gameViewModel.randomTeams()
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = DarkOnPrimary
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(NeonGreen, NeonGreenDark, Cyan)
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Shuffle, null, Modifier.size(22.dp), tint = DarkOnPrimary)
                                Text(
                                    "RANDOM TEAM",
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 15.sp,
                                    color = DarkOnPrimary
                                )
                            }
                        }
                    }
                }
            }

            // ====== Danh sách team ======
            if (teams.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = CardGlowBorderSubtle,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Group, null, tint = Cyan, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                "TEAMS (${teams.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = LightText,
                                letterSpacing = 1.sp,
                                modifier = Modifier.weight(1f)
                            )

                            // Share Button
                            IconButton(onClick = {
                                val message = gameViewModel.getShareMessage()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share with")
                                context.startActivity(shareIntent)
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonGreen)
                            }

                            // Sort/Menu Button
                            Box {
                                IconButton(onClick = { showTeamsMenu = true }) {
                                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort", tint = LightTextSecondary)
                                }
                                DropdownMenu(
                                    expanded = showTeamsMenu,
                                    onDismissRequest = { showTeamsMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Sort by size") },
                                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null, tint = NeonGreen) },
                                        onClick = {
                                            showTeamsMenu = false
                                            gameViewModel.sortTeams()
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Bracket") },
                                        leadingIcon = { Icon(Icons.Default.AccountTree, null, tint = ElectricBlue) },
                                        onClick = {
                                            showTeamsMenu = false
                                            navController.navigate("bracket")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Circle") },
                                        leadingIcon = { Icon(Icons.Default.Circle, null, tint = Cyan) },
                                        onClick = {
                                            showTeamsMenu = false
                                            navController.navigate("circle")
                                        }
                                    )
                                }
                            }
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
    val teamColors = listOf(
        listOf(NeonGreen, Cyan),
        listOf(Cyan, ElectricBlue),
        listOf(ElectricBlue, NeonGreen),
        listOf(SportAmber, NeonGreen)
    )
    val colorPair = teamColors[(teamNumber - 1) % teamColors.size]

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Gradient left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(colors = colorPair),
                        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    )
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Team number badge - gradient
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(colors = colorPair)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$teamNumber",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = DarkOnPrimary
                        )
                    }
                    Column {
                        Text(
                            "TEAM $teamNumber",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = colorPair[0],
                            letterSpacing = 1.sp
                        )
                        Text(
                            "${players.size} players",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    players.forEach { player ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colorPair[1].copy(alpha = 0.7f))
                            )
                            Text(
                                player,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightText
                            )
                        }
                    }
                }
            }
        }
    }
}
