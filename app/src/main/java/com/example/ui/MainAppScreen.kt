package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItemSpec
import com.example.ui.auth.performSignOut
import com.example.ui.collection.CollectionScreen
import com.example.ui.inspector.HighResLoupeScreen
import com.example.ui.market.MarketScreen
import com.example.ui.scanner.ScannerScreen
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.EuroGoldLight
import com.example.ui.theme.EuroGoldPrimary
import com.example.ui.theme.EuroSilver
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

enum class MainTab(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
  VAULT("My Vault", Icons.Filled.Collections, Icons.Outlined.Collections),
  SCANNER("Scanner", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
  MARKET("Market", Icons.Filled.ShowChart, Icons.Outlined.ShowChart),
  LOUPE("10x Loupe", Icons.Filled.Visibility, Icons.Outlined.Visibility)
}

@Composable
fun MainAppScreen(
  currentUserId: String,
  onSignOut: () -> Unit,
  isGuest: Boolean = false,
  onRequestSignIn: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var currentTab by remember { mutableStateOf(MainTab.VAULT) }
  var inspectorSpecimen by remember { mutableStateOf<CatalogItemSpec?>(null) }
  var showUserDialog by remember { mutableStateOf(false) }

  val authUser = remember { Firebase.auth.currentUser }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = DarkSlateBackground,
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .navigationBarsPadding()
          .height(68.dp)
          .border(1.dp, DarkSlateBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = DarkSlateSurface,
        tonalElevation = 8.dp
      ) {
        MainTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTab = tab },
            icon = {
              Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.label,
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color(0xFF1E1705),
              selectedTextColor = EuroGoldLight,
              indicatorColor = EuroGoldPrimary,
              unselectedIconColor = EuroSilver,
              unselectedTextColor = EuroSilver
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Content Screen
      Crossfade(targetState = currentTab, label = "ScreenTransition") { tab ->
        when (tab) {
          MainTab.VAULT -> {
            CollectionScreen(
              currentUserId = currentUserId,
              onInspectItemInLoupe = { spec ->
                inspectorSpecimen = spec
                currentTab = MainTab.LOUPE
              },
              isGuest = isGuest,
              onRequestSignIn = onRequestSignIn
            )
          }
          MainTab.SCANNER -> {
            ScannerScreen(
              currentUserId = currentUserId,
              onNavigateToLoupe = { spec ->
                inspectorSpecimen = spec
                currentTab = MainTab.LOUPE
              },
              onItemSavedToVault = {
                currentTab = MainTab.VAULT
              }
            )
          }
          MainTab.MARKET -> {
            MarketScreen(
              currentUserId = currentUserId,
              onInspectInLoupe = { spec ->
                inspectorSpecimen = spec
                currentTab = MainTab.LOUPE
              }
            )
          }
          MainTab.LOUPE -> {
            HighResLoupeScreen(
              initialItemSpec = inspectorSpecimen
            )
          }
        }
      }

      // Top floating profile icon button (top-right)
      IconButton(
        onClick = { showUserDialog = true },
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 10.dp, end = 16.dp)
          .size(40.dp)
          .clip(CircleShape)
          .background(Color(0x990F172A))
          .border(1.dp, EuroGoldPrimary.copy(alpha = 0.5f), CircleShape)
          .testTag("profile_button")
      ) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Profile",
          tint = EuroGoldLight,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }

  // Profile & Sign-Out Dialog
  if (showUserDialog) {
    AlertDialog(
      onDismissRequest = { showUserDialog = false },
      containerColor = DarkSlateSurface,
      title = {
        Text(
          text = if (isGuest) "Anonymous Guest Mode" else "Collector Account",
          color = EuroGoldLight,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (isGuest) {
            Text(
              text = "You are currently browsing in Guest Mode",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFF1F5F9)
            )
            Text(
              text = "Vault items are stored in temporary device memory. Sign in with Google to enable persistent Firebase Cloud sync across your devices.",
              style = MaterialTheme.typography.bodySmall,
              color = EuroSilver
            )
          } else {
            Text(
              text = "Signed in as: ${authUser?.displayName ?: "Euro Collector"}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFF1F5F9)
            )
            Text(
              text = authUser?.email ?: "Google Account",
              style = MaterialTheme.typography.bodySmall,
              color = EuroSilver
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Your catalog is securely synced with Firebase Cloud Firestore. High-resolution scans and market tracking are live.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF94A3B8)
            )
          }
        }
      },
      confirmButton = {
        if (isGuest) {
          Button(
            onClick = {
              showUserDialog = false
              onRequestSignIn()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = EuroGoldPrimary,
              contentColor = Color(0xFF1E1705)
            ),
            modifier = Modifier.testTag("guest_dialog_sign_in_button")
          ) {
            Text("Sign In with Google", fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = {
              showUserDialog = false
              performSignOut(
                context = context,
                scope = CoroutineScope(Dispatchers.Main),
                onSignOutComplete = onSignOut
              )
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFEF4444),
              contentColor = Color.White
            ),
            modifier = Modifier.testTag("sign_out_button")
          ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign Out")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { showUserDialog = false }) {
          Text("Close", color = EuroSilver)
        }
      }
    )
  }
}
