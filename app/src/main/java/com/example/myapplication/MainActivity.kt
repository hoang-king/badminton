package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.bracket.BracketScreen
import com.example.myapplication.presentation.bracket.BracketViewModel
import com.example.myapplication.presentation.circle.CircleScreen
import com.example.myapplication.presentation.circle.CircleViewModel
import com.example.myapplication.presentation.history.HistoryScreen
import com.example.myapplication.presentation.game.GameScreen
import com.example.myapplication.presentation.game.GameViewModel
import com.example.myapplication.presentation.score.BadmintonScoreScreen
import com.example.myapplication.presentation.theme.MyApplicationTheme

import dagger.hilt.android.AndroidEntryPoint

import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                // Tạo shared ViewModels ở đây
                val gameViewModel: GameViewModel = hiltViewModel()
                val circleViewModel: CircleViewModel = hiltViewModel()
                val bracketViewModel: BracketViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = "game") {
                    composable("game") {
                        GameScreen(
                            gameViewModel = gameViewModel,
                            navController = navController
                        )
                    }
                    composable("bracket") {
                        BracketScreen(
                            navController = navController,
                            gameViewModel = gameViewModel,
                            bracketViewModel = bracketViewModel
                        )
                    }
                    composable("circle") {
                        CircleScreen(
                            navController = navController,
                            gameViewModel = gameViewModel,
                            circleViewModel = circleViewModel
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            navController = navController
                        )
                    }
                    composable("score") {
                        BadmintonScoreScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
