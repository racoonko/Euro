package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.MainAppScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.theme.EuroVaultTheme
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Step 1: Configure App Check with launch intent before UI, ViewModels or Firebase operations
    configureAppCheck(this, intent)

    // Step 2: Edge to Edge
    enableEdgeToEdge()

    // Step 3: Initialize UI
    setContent {
      EuroVaultTheme {
        AppRoot()
      }
    }
  }
}

@Composable
fun AppRoot() {
  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
  var isGuestMode by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
      if (auth.currentUser != null) {
        isGuestMode = false
      }
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose {
      Firebase.auth.removeAuthStateListener(listener)
    }
  }

  if (currentUser == null && !isGuestMode) {
    AuthScreen(
      onAuthSuccess = {
        currentUser = Firebase.auth.currentUser
        isGuestMode = false
      },
      onContinueAsGuest = {
        isGuestMode = true
      }
    )
  } else {
    val isGuest = (currentUser == null)
    val effectiveUserId = currentUser?.uid ?: "guest_collector"
    key(effectiveUserId, isGuest) {
      MainAppScreen(
        currentUserId = effectiveUserId,
        onSignOut = {
          currentUser = null
          isGuestMode = false
        },
        isGuest = isGuest,
        onRequestSignIn = {
          isGuestMode = false
        }
      )
    }
  }
}
