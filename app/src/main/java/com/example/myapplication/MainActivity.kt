package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.bracket.BracketScreen
import com.example.myapplication.presentation.bracket.BracketViewModel
import com.example.myapplication.presentation.circle.CircleScreen
import com.example.myapplication.presentation.circle.CircleViewModel
import com.example.myapplication.presentation.game.GameScreen
import com.example.myapplication.presentation.game.GameViewModel
import com.example.myapplication.presentation.score.BadmintonScoreScreen
import com.example.myapplication.presentation.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                // Tạo shared ViewModels ở đây
                val gameViewModel: GameViewModel = viewModel()
                val circleViewModel: CircleViewModel = viewModel()
                val bracketViewModel: BracketViewModel = viewModel()

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
                    composable("Score") {
                        BadmintonScoreScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
