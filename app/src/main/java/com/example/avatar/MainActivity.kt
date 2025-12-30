
package com.example.avatar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.avatar.presentation.login.LoginViewModel
import com.example.avatar.presentation.login.google_auth.GoogleAuthUiClient
import com.example.avatar.presentation.login.google_auth.SignInResult
import com.example.avatar.presentation.navigation.NavGraph
import com.example.avatar.ui.theme.AvatarTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvatarTheme {
                val loginViewModel = hiltViewModel<LoginViewModel>()
                val state by loginViewModel.state.collectAsStateWithLifecycle()

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            lifecycleScope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                loginViewModel.onSignInResult(signInResult)
                            }
                        } else {
                            loginViewModel.onSignInResult(
                                SignInResult(
                                    data = null,
                                    errorMessage = "Sign in was not successful. Result code: ${result.resultCode}"
                                )
                            )
                        }
                    }
                )

                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if(state.isSignInSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                LaunchedEffect(key1 = state.signInError) {
                    state.signInError?.let { error ->
                        Toast.makeText(
                            applicationContext,
                            error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                NavGraph(
                    googleAuthUiClient = googleAuthUiClient,
                    launcher = launcher,
                    loginViewModel = loginViewModel
                )
            }
        }
    }
}
