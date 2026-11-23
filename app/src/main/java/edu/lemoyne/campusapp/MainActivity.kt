package edu.lemoyne.campusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.lemoyne.campusapp.ui.theme.CampusAppTheme

class MainActivity : ComponentActivity() {
    private val showcaseViewModel: ShowcaseViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val showcaseState by showcaseViewModel.state.collectAsStateWithLifecycle()
            val loginState by loginViewModel.state.collectAsStateWithLifecycle()

            CampusAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // --- Class 9 Step 2: the app starts at the owner
                    CampusAppScreen(
                        showcaseState = showcaseState,
                        loginState = loginState,
                        onRefreshShowcase = showcaseViewModel::refresh,
                        onLogin = { email, password, navigate ->
                            loginViewModel.login(email, password) { token ->
                                showcaseViewModel.setAccessToken(token)
                                showcaseViewModel.refresh()
                                navigate()
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}