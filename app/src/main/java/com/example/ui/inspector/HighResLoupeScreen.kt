package com.example.ui.inspector

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItemSpec
import com.example.data.model.EuroCatalogData
import com.example.ui.components.EuroBanknoteVisual
import com.example.ui.components.EuroCoinVisual
import com.example.ui.components.SheldonGradeBadge
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceElevated
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EuroGoldDark
import com.example.ui.theme.EuroGoldLight
import com.example.ui.theme.EuroGoldPrimary
import com.example.ui.theme.EuroSilver
import com.example.ui.theme.UvGlowNeonGreen
import com.example.ui.theme.UvGlowPurple
import kotlin.math.roundToInt

enum class LightingSpectrum(val label: String, val description: String) {
  NORMAL("Luster Light", "Natural true-color balanced lighting reveals mint luster & surface wear"),
  UV_BLACKLIGHT("UV 365nm", "Ultraviolet fluorescence reveals security fibers, glowing flag & luminescent dyes"),
  OBLIQUE_GRAZING("Oblique / Rake", "Low-angle side lighting casts relief shadows on edge lettering & die varieties"),
  WATERMARK_BACKLIGHT("Transmitted", "Backlight reveals embedded portrait watermark & security thread microprint")
}

@Composable
fun HighResLoupeScreen(
  initialItemSpec: CatalogItemSpec? = null,
  modifier: Modifier = Modifier
) {
  var selectedItem by remember {
    mutableStateOf(initialItemSpec ?: EuroCatalogData.ALL_CATALOG_ITEMS.first())
  }
  var magnification by remember { mutableFloatStateOf(5.0f) }
  var currentSpectrum by remember { mutableStateOf(LightingSpectrum.NORMAL) }
  var loupeOffsetX by remember { mutableFloatStateOf(0f) }
  var loupeOffsetY by remember { mutableFloatStateOf(0f) }
  var showGradingGuide by remember { mutableStateOf(false) }

  val animatedBgColor by animateColorAsState(
    targetValue = when (currentSpectrum) {
      LightingSpectrum.NORMAL -> DarkSlateSurface
      LightingSpectrum.UV_BLACKLIGHT -> Color(0xFF140D26)
      LightingSpectrum.OBLIQUE_GRAZING -> Color(0xFF0F172A)
      LightingSpectrum.WATERMARK_BACKLIGHT -> Color(0xFF1E293B)
    },
    label = "LoupeBgColor"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkSlateBackground)
      .statusBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(bottom = 90.dp)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = "High-Res Loupe Inspector",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = EuroGoldLight
        )
        Text(
          text = "Numismatic Optical & Security Analysis",
          style = MaterialTheme.typography.bodySmall,
          color = EuroSilver
        )
      }

      SheldonGradeBadge(grade = "10x OPTICAL")
    }

    // Quick Item Switcher Row
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
      Text(
        text = "Select Specimen to Inspect:",
        style = MaterialTheme.typography.labelMedium,
        color = EuroSilver,
        modifier = Modifier.padding(bottom = 6.dp)
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val sampleItems = EuroCatalogData.ALL_CATALOG_ITEMS.take(4)
        sampleItems.forEach { item ->
          val isSelected = item.id == selectedItem.id
          FilterChip(
            selected = isSelected,
            onClick = { selectedItem = item },
            label = {
              Text(
                text = item.denomination + " " + item.country.take(4),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EuroGoldPrimary,
              selectedLabelColor = Color(0xFF1E1705),
              containerColor = DarkSlateSurfaceVariant,
              labelColor = EuroSilver
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Interactive Magnifying Inspection Viewport
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(310.dp)
        .padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(animatedBgColor)
        .border(1.5.dp, DarkSlateBorder, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center
    ) {
      // Background specimen being inspected
      Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
      ) {
        if (selectedItem.itemType == "coin") {
          EuroCoinVisual(
            denomination = selectedItem.denomination,
            country = selectedItem.country,
            year = selectedItem.year,
            sizeDp = 180.dp,
            isProof = currentSpectrum == LightingSpectrum.OBLIQUE_GRAZING
          )
        } else {
          EuroBanknoteVisual(
            denomination = selectedItem.denomination,
            widthDp = 220.dp,
            heightDp = 125.dp
          )
        }

        // Overlay effect based on spectrum mode
        when (currentSpectrum) {
          LightingSpectrum.UV_BLACKLIGHT -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
              // Glowing green and yellow UV fibers
              val fiberPoints = listOf(
                Pair(Offset(size.width * 0.3f, size.height * 0.4f), Offset(size.width * 0.35f, size.height * 0.45f)),
                Pair(Offset(size.width * 0.6f, size.height * 0.3f), Offset(size.width * 0.68f, size.height * 0.34f)),
                Pair(Offset(size.width * 0.45f, size.height * 0.7f), Offset(size.width * 0.52f, size.height * 0.65f)),
                Pair(Offset(size.width * 0.75f, size.height * 0.55f), Offset(size.width * 0.8f, size.height * 0.62f))
              )
              fiberPoints.forEach { (start, end) ->
                drawLine(
                  color = UvGlowNeonGreen,
                  start = start,
                  end = end,
                  strokeWidth = 3f,
                  cap = StrokeCap.Round
                )
              }
              // Fluorescent glow wash
              drawCircle(
                brush = Brush.radialGradient(
                  listOf(UvGlowPurple.copy(alpha = 0.35f), Color.Transparent),
                  radius = size.width * 0.45f
                )
              )
            }
          }
          LightingSpectrum.OBLIQUE_GRAZING -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
              // Harsh angled grazing shadow line
              drawLine(
                brush = Brush.horizontalGradient(
                  listOf(Color(0x77000000), Color.Transparent, Color(0x55FFFFFF))
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 14f
              )
            }
          }
          LightingSpectrum.WATERMARK_BACKLIGHT -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
              // Transmitted light center beam
              drawCircle(
                brush = Brush.radialGradient(
                  listOf(Color(0x66FFFFFF), Color.Transparent),
                  radius = size.width * 0.38f
                )
              )
            }
          }
          LightingSpectrum.NORMAL -> {}
        }
      }

      // Interactive Jeweler's Loupe Lens (Draggable)
      Box(
        modifier = Modifier
          .offset { IntOffset(loupeOffsetX.roundToInt(), loupeOffsetY.roundToInt()) }
          .size(118.dp)
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              loupeOffsetX = (loupeOffsetX + dragAmount.x).coerceIn(-120f, 120f)
              loupeOffsetY = (loupeOffsetY + dragAmount.y).coerceIn(-85f, 85f)
            }
          }
          .clip(CircleShape)
          .background(Color(0x33000000))
          .border(4.dp, EuroGoldPrimary, CircleShape)
          .border(6.dp, Color(0x66B45309), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        // High-power loupe crosshair reticle and glass reflection
        Canvas(modifier = Modifier.fillMaxSize()) {
          val center = Offset(size.width / 2f, size.height / 2f)
          val radius = size.minDimension / 2f

          // Outer glass ring highlight
          drawCircle(
            color = Color(0x44FFFFFF),
            radius = radius - 4f,
            style = Stroke(width = 2f)
          )

          // 0.1mm measurement ticks
          drawLine(
            color = EuroGoldLight.copy(alpha = 0.7f),
            start = Offset(center.x - 20f, center.y),
            end = Offset(center.x + 20f, center.y),
            strokeWidth = 1.5f
          )
          drawLine(
            color = EuroGoldLight.copy(alpha = 0.7f),
            start = Offset(center.x, center.y - 20f),
            end = Offset(center.x, center.y + 20f),
            strokeWidth = 1.5f
          )

          // Reticle center circle
          drawCircle(
            color = EuroGoldPrimary,
            radius = 6f,
            center = center,
            style = Stroke(width = 1.2f)
          )
        }

        // Loupe magnification label
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .background(Color(0x99111827), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${magnification.toInt()}x MAG",
            color = EuroGoldLight,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      // Drag hint overlay
      Row(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 10.dp)
          .background(Color(0xBB0F172A), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Adjust,
          contentDescription = null,
          tint = EuroGoldPrimary,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Drag loupe over surface to inspect relief & fibers",
          color = EuroSilver,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Magnification Zoom Selector
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Optical Magnification:",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFF1F5F9)
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(1.0f, 2.5f, 5.0f, 10.0f).forEach { zoom ->
          val isSelected = magnification == zoom
          Surface(
            onClick = { magnification = zoom },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) EuroGoldPrimary else DarkSlateSurfaceVariant,
            modifier = Modifier.testTag("zoom_button_${zoom.toInt()}")
          ) {
            Text(
              text = "${if (zoom == 2.5f) "2.5" else zoom.toInt().toString()}x",
              color = if (isSelected) Color(0xFF1E1705) else EuroSilver,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Spectrum / Lighting Mode Switcher
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
    ) {
      Text(
        text = "Security & Lighting Spectrum:",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFF1F5F9),
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LightingSpectrum.values().forEach { spectrum ->
          val isSelected = currentSpectrum == spectrum
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) DarkSlateSurfaceElevated else DarkSlateSurface)
              .border(
                1.dp,
                if (isSelected) EuroGoldPrimary else DarkSlateBorder,
                RoundedCornerShape(12.dp)
              )
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              onClick = { currentSpectrum = spectrum },
              shape = CircleShape,
              color = if (isSelected) EuroGoldPrimary else DarkSlateSurfaceVariant,
              modifier = Modifier.size(24.dp)
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF1E1705),
                  modifier = Modifier.padding(2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = spectrum.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) EuroGoldLight else Color(0xFFF1F5F9)
              )
              Text(
                text = spectrum.description,
                style = MaterialTheme.typography.bodySmall,
                color = EuroSilver,
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Verified Security Features of Selected Item
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .background(DarkSlateSurface, RoundedCornerShape(16.dp))
        .border(1.dp, DarkSlateBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "Key Security & Mint Markers",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = EuroGoldLight
        )
        Text(
          text = selectedItem.denomination,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          color = EuroGoldPrimary
        )
      }

      Text(
        text = selectedItem.specifications,
        style = MaterialTheme.typography.bodySmall,
        color = EuroSilver,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 4.dp)
      ) {
        selectedItem.securityFeatures.forEach { feature ->
          Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = EmeraldSuccess,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = feature,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFE2E8F0)
            )
          }
        }
      }
    }
  }
}
