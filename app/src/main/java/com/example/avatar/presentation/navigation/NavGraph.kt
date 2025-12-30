package com.example.avatar.presentation.navigation

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.avatar.presentation.login.LoginScreen
import com.example.avatar.presentation.login.LoginViewModel
import com.example.avatar.presentation.login.google_auth.GoogleAuthUiClient
import com.example.avatar.ui.character_detail.CharacterDetailScreen
import com.example.avatar.ui.character_list.CharacterListScreen
import com.example.avatar.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object CharacterList : Screen("character_list_screen")
    object Profile : Screen("profile_screen")
    object CharacterDetail : Screen("character_detail_screen/{characterId}") {
        fun createRoute(characterId: String) = "character_detail_screen/$characterId"
    }
}

@Composable
fun NavGraph(
    googleAuthUiClient: GoogleAuthUiClient,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val state by loginViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                if(googleAuthUiClient.getSignedInUser() != null) {
                    navController.navigate(Screen.CharacterList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if(state.isSignInSuccessful) {
                    navController.navigate(Screen.CharacterList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onSignInClick = {
                    loginViewModel.viewModelScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }
        composable(Screen.CharacterList.route) {
            CharacterListScreen(
                onCharacterClick = { character ->
                    navController.navigate(Screen.CharacterDetail.createRoute(character.id))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    loginViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.CharacterList.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.CharacterDetail.route,
            arguments = listOf(
                navArgument("characterId") {
                    type = NavType.StringType
                }
            )
        ) {
            CharacterDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
