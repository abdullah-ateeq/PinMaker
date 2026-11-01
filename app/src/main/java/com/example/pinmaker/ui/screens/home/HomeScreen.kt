package com.example.pinmaker.ui.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pinmaker.model.PinUiState
import com.example.pinmaker.ui.components.HapticHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: PinUiState,
    hapticEnabled: Boolean,
    onLengthChanged: (Int) -> Unit,
    onToggleRepeatedDigits: (Boolean) -> Unit,
    onToggleConsecutiveDigits: (Boolean) -> Unit,
    onPrefixChanged: (String) -> Unit,
    onSuffixChanged: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onRegenerate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val copyToClipboard: () -> Unit = {
        if (uiState.pin.isNotEmpty()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("PinMaker PIN", uiState.pin)
            clipboard.setPrimaryClip(clip)

            if (hapticEnabled) {
                HapticHelper.performSuccessFeedback(context)
            }

            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "PIN copied to clipboard",
                    withDismissAction = true
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "PinMaker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // PIN Display Card
            PinDisplayCard(
                uiState = uiState,
                onToggleVisibility = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onToggleVisibility()
                },
                onCopyPin = copyToClipboard,
                onRegenerate = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onRegenerate()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Configuration Controls
            PinControlsSection(
                uiState = uiState,
                onLengthChanged = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onLengthChanged(it)
                },
                onToggleRepeatedDigits = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onToggleRepeatedDigits(it)
                },
                onToggleConsecutiveDigits = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onToggleConsecutiveDigits(it)
                },
                onPrefixChanged = onPrefixChanged,
                onSuffixChanged = onSuffixChanged,
                onGenerateClicked = {
                    if (hapticEnabled) HapticHelper.performClickFeedback(view)
                    onRegenerate()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
