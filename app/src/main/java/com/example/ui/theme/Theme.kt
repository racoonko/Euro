package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EuroVaultDarkColorScheme = darkColorScheme(
  primary = EuroGoldPrimary,
  onPrimary = Color(0xFF1E1705),
  primaryContainer = EuroGoldDark,
  onPrimaryContainer = EuroGoldLight,
  secondary = EuroSilver,
  onSecondary = Color(0xFF0F172A),
  secondaryContainer = DarkSlateSurfaceElevated,
  onSecondaryContainer = EuroSilverLight,
  tertiary = EmeraldSuccess,
  onTertiary = Color.White,
  background = DarkSlateBackground,
  onBackground = TextPrimary,
  surface = DarkSlateSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSlateSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkSlateBorder,
  outlineVariant = Color(0xFF1E293B),
  error = CrimsonError,
  onError = Color.White
)

private val EuroVaultLightColorScheme = lightColorScheme(
  primary = EuroGoldDark,
  onPrimary = Color.White,
  primaryContainer = EuroGoldLight,
  onPrimaryContainer = Color(0xFF452B00),
  secondary = Color(0xFF475569),
  onSecondary = Color.White,
  tertiary = EmeraldDark,
  onTertiary = Color.White,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color.White,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1),
  error = CrimsonError,
  onError = Color.White
)

@Composable
fun EuroVaultTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  // Numismatic apps look best in dark theme or gold-accented light theme
  val colorScheme = if (darkTheme) EuroVaultDarkColorScheme else EuroVaultLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
