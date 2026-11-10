package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSlateBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EuroBronze
import com.example.ui.theme.EuroGoldDark
import com.example.ui.theme.EuroGoldLight
import com.example.ui.theme.EuroGoldPrimary
import com.example.ui.theme.EuroSilver
import com.example.ui.theme.EuroSilverLight
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EuroCoinVisual(
  denomination: String,
  country: String,
  year: Long,
  modifier: Modifier = Modifier,
  sizeDp: Dp = 90.dp,
  isProof: Boolean = false
) {
  val isBiMetallic = denomination.contains("1.00") || denomination.contains("2.00") || denomination == "€1" || denomination == "€2"
  val isNordicGold = denomination.contains("0.10") || denomination.contains("0.20") || denomination.contains("0.50")
  val isCopper = denomination.contains("0.01") || denomination.contains("0.02") || denomination.contains("0.05")

  val outerRingBrush = when {
    isBiMetallic && (denomination.contains("2") || denomination == "€2.00") -> Brush.sweepGradient(
      listOf(EuroSilverLight, EuroSilver, Color(0xFF64748B), EuroSilverLight)
    )
    isBiMetallic -> Brush.sweepGradient(
      listOf(EuroGoldLight, EuroGoldPrimary, EuroGoldDark, EuroGoldLight)
    )
    isNordicGold -> Brush.sweepGradient(
      listOf(Color(0xFFFDE047), Color(0xFFCA8A04), Color(0xFF854D0E), Color(0xFFFDE047))
    )
    isCopper -> Brush.sweepGradient(
      listOf(Color(0xFFFCA5A5), EuroBronze, Color(0xFF7F1D1D), Color(0xFFFCA5A5))
    )
    else -> Brush.sweepGradient(
      listOf(EuroGoldLight, EuroGoldPrimary, EuroGoldDark, EuroGoldLight)
    )
  }

  val innerCoreBrush = when {
    isBiMetallic && (denomination.contains("2") || denomination == "€2.00") -> Brush.radialGradient(
      listOf(EuroGoldLight, EuroGoldPrimary, EuroGoldDark)
    )
    isBiMetallic -> Brush.radialGradient(
      listOf(EuroSilverLight, EuroSilver, Color(0xFF475569))
    )
    isNordicGold -> Brush.radialGradient(
      listOf(Color(0xFFFEF08A), Color(0xFFEAB308), Color(0xFFA16207))
    )
    isCopper -> Brush.radialGradient(
      listOf(Color(0xFFFECACA), Color(0xFFEA580C), Color(0xFF9A3412))
    )
    else -> Brush.radialGradient(
      listOf(EuroGoldLight, EuroGoldPrimary, EuroGoldDark)
    )
  }

  Box(
    modifier = modifier
      .size(sizeDp)
      .clip(CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = size.minDimension / 2f

      // Reeded edge outer rim
      drawCircle(
        brush = outerRingBrush,
        radius = radius,
        center = center
      )

      // Outer ring reeded notches simulation
      val notchCount = 36
      for (i in 0 until notchCount) {
        val angle = (i.toDouble() * (2.0 * Math.PI / notchCount))
        val startX = center.x + (radius - 4f) * cos(angle).toFloat()
        val startY = center.y + (radius - 4f) * sin(angle).toFloat()
        val endX = center.x + radius * cos(angle).toFloat()
        val endY = center.y + radius * sin(angle).toFloat()
        drawLine(
          color = Color(0x33000000),
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 2f,
          cap = StrokeCap.Round
        )
      }

      // Outer ring border
      drawCircle(
        color = Color(0x40000000),
        radius = radius - 1.5f,
        center = center,
        style = Stroke(width = 2f)
      )

      // Inner core
      val coreRadius = if (isBiMetallic) radius * 0.70f else radius * 0.88f
      drawCircle(
        brush = innerCoreBrush,
        radius = coreRadius,
        center = center
      )

      // Core border groove
      drawCircle(
        color = Color(0x55000000),
        radius = coreRadius,
        center = center,
        style = Stroke(width = 2f)
      )

      // Stars on outer ring (for bimetallic €1 and €2)
      if (isBiMetallic) {
        val starOrbitRadius = (radius + coreRadius) / 2f
        for (i in 0 until 12) {
          val starAngle = (i.toDouble() * (2.0 * Math.PI / 12.0)) - (Math.PI / 2.0)
          val starCenter = Offset(
            center.x + starOrbitRadius * cos(starAngle).toFloat(),
            center.y + starOrbitRadius * sin(starAngle).toFloat()
          )
          drawCircle(
            color = if (denomination.contains("2")) EuroGoldLight else EuroSilverLight,
            radius = 2.5f,
            center = starCenter
          )
        }
      }

      // Proof mirror reflection line
      if (isProof) {
        drawLine(
          brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color(0x80FFFFFF), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
          ),
          start = Offset(0f, size.height * 0.2f),
          end = Offset(size.width, size.height * 0.8f),
          strokeWidth = 8f
        )
      }
    }

    // Center relief denomination and details
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = denomination,
        fontSize = (sizeDp.value * 0.24f).sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF1E1705),
        lineHeight = (sizeDp.value * 0.24f).sp
      )
      if (sizeDp >= 70.dp) {
        Text(
          text = country.take(8).uppercase(),
          fontSize = (sizeDp.value * 0.10f).sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF452B00),
          letterSpacing = 0.5.sp
        )
        Text(
          text = year.toString(),
          fontSize = (sizeDp.value * 0.09f).sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF452B00)
        )
      }
    }
  }
}

@Composable
fun EuroBanknoteVisual(
  denomination: String,
  modifier: Modifier = Modifier,
  widthDp: Dp = 150.dp,
  heightDp: Dp = 85.dp
) {
  val noteColor = when (denomination) {
    "€5" -> Color(0xFF6B7280) // Grey
    "€10" -> Color(0xFFEF4444) // Red
    "€20" -> Color(0xFF3B82F6) // Blue
    "€50" -> Color(0xFFF97316) // Orange
    "€100" -> Color(0xFF10B981) // Green
    "€200" -> Color(0xFFEAB308) // Yellow-Brown
    "€500" -> Color(0xFF8B5CF6) // Purple
    else -> Color(0xFFF97316)
  }

  Box(
    modifier = modifier
      .width(widthDp)
      .height(heightDp)
      .clip(RoundedCornerShape(6.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(
            noteColor.copy(alpha = 0.25f),
            noteColor.copy(alpha = 0.50f),
            noteColor.copy(alpha = 0.35f)
          )
        )
      )
      .border(1.5.dp, noteColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
      .padding(8.dp)
  ) {
    // Watermark window area
    Box(
      modifier = Modifier
        .size(widthDp * 0.22f, heightDp * 0.70f)
        .align(Alignment.CenterStart)
        .clip(RoundedCornerShape(4.dp))
        .background(Color(0x33FFFFFF))
        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(4.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "★",
        color = Color(0x66FFFFFF),
        fontSize = 12.sp
      )
    }

    // Security thread stripe
    Canvas(
      modifier = Modifier
        .width(6.dp)
        .fillMaxSize()
        .align(Alignment.Center)
    ) {
      drawLine(
        brush = Brush.verticalGradient(
          listOf(Color(0x88000000), Color(0x33FFFFFF), Color(0x88000000))
        ),
        start = Offset(size.width / 2f, 0f),
        end = Offset(size.width / 2f, size.height),
        strokeWidth = 4f
      )
    }

    // Hologram silver patch on right
    Box(
      modifier = Modifier
        .width(widthDp * 0.18f)
        .height(heightDp * 0.85f)
        .align(Alignment.CenterEnd)
        .clip(RoundedCornerShape(4.dp))
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(0xFFCBD5E1),
              Color(0xFFE2E8F0),
              Color(0xFF94A3B8),
              Color(0xFFF1F5F9)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = denomination.replace("€", ""),
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF1E293B)
      )
    }

    // Denomination text on bottom left
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = widthDp * 0.26f)
    ) {
      Text(
        text = denomination,
        fontSize = (heightDp.value * 0.32f).sp,
        fontWeight = FontWeight.Black,
        color = noteColor
      )
      Text(
        text = "EURO / ΕΥΡΩ",
        fontSize = (heightDp.value * 0.12f).sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFE2E8F0)
      )
    }

    // EU flag stars top center
    Text(
      text = "★★★★★★",
      color = Color(0xAAFFFFFF),
      fontSize = 8.sp,
      modifier = Modifier.align(Alignment.TopCenter)
    )
  }
}

@Composable
fun SheldonGradeBadge(
  grade: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (grade) {
    "PROOF" -> Pair(Color(0x33F59E0B), EuroGoldPrimary)
    "UNC" -> Pair(Color(0x3310B981), EmeraldSuccess)
    "AU" -> Pair(Color(0x333B82F6), Color(0xFF60A5FA))
    "XF" -> Pair(Color(0x3306B6D4), Color(0xFF22D3EE))
    "VF" -> Pair(Color(0x33F59E0B), Color(0xFFFBBF24))
    "F" -> Pair(Color(0x33F97316), Color(0xFFFB923C))
    "VG" -> Pair(Color(0x33EF4444), Color(0xFFF87171))
    else -> Pair(DarkSlateSurfaceVariant, EuroSilver)
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(6.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.6f)),
    modifier = modifier
  ) {
    Text(
      text = grade,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.ExtraBold,
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun RarityBadge(
  tier: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (tier) {
    "Ultra Rare / Key Date" -> Pair(Color(0x338B5CF6), Color(0xFFA78BFA))
    "Rare" -> Pair(Color(0x33F59E0B), EuroGoldPrimary)
    "Scarce" -> Pair(Color(0x333B82F6), Color(0xFF60A5FA))
    else -> Pair(Color(0x2264748B), Color(0xFF94A3B8))
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Text(
      text = tier,
      color = textColor,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
    )
  }
}

@Composable
fun MarketChangePill(
  profitEuro: Double,
  profitPercent: Double,
  modifier: Modifier = Modifier
) {
  val isPositive = profitEuro >= 0
  val color = if (isPositive) EmeraldSuccess else Color(0xFFEF4444)
  val icon = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(12.dp)
    )
    Text(
      text = "${if (isPositive) "+" else ""}€${"%.2f".format(profitEuro)} (${if (isPositive) "+" else ""}${"%.1f".format(profitPercent)}%)",
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
