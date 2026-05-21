package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TestScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Modern Tahlil Architecture Test") 
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = "✅ P2.A Modern Architecture Implementation\n\n" +
                          "• Single Activity + Navigation Component\n" +
                          "• Jetpack Compose + Material Design 3\n" +
                          "• Modular project structure\n" +
                          "• Hilt dependency injection\n" +
                          "• Islamic design theme\n\n" +
                          "Ready for development!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}