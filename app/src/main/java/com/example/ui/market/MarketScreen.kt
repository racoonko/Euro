package com.example.ui.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItemSpec
import com.example.data.model.CollectorItem
import com.example.data.model.EuroCatalogData
import com.example.data.repository.CollectorRepository
import com.example.ui.components.EuroBanknoteVisual
import com.example.ui.components.EuroCoinVisual
import com.example.ui.components.RarityBadge
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MarketTickerIndex(
  val name: String,
  val currentPrice: Double,
  val changePercent: Double,
  val category: String
)

@Composable
fun MarketScreen(
  currentUserId: String,
  onInspectInLoupe: (CatalogItemSpec) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val repository = remember { CollectorRepository(context) }

  val itemsFlow = remember(currentUserId) {
    repository.observeItems(currentUserId).catch { emit(emptyList()) }
  }
  val collectorItems by itemsFlow.collectAsState(initial = emptyList())

  var catalogSearchQuery by remember { mutableStateOf("") }
  var selectedCountry by remember { mutableStateOf("All Countries") }
  var selectedType by remember { mutableStateOf("All") }
  var addedMessage by remember { mutableStateOf<String?>(null) }

  val totalValue = collectorItems.sumOf { it.currentMarketValue }
  val totalCost = collectorItems.sumOf { it.purchasePrice }
  val gainEuro = totalValue - totalCost
  val gainPct = if (totalCost > 0) (gainEuro / totalCost) * 100.0 else 0.0

  val marketTickers = remember {
    listOf(
      MarketTickerIndex("Monaco €2 Index", 3850.0, 4.2, "Commemorative"),
      MarketTickerIndex("Vatican Issues", 320.0, 1.8, "Holy See"),
      MarketTickerIndex("San Marino Key Dates", 160.0, 3.1, "Enclave Mint"),
      MarketTickerIndex("German Error Varieties", 95.0, 8.5, "Old Map Error"),
      MarketTickerIndex("€500 Discontinued Series", 920.0, 5.6, "1st Series 2002"),
      MarketTickerIndex("Europa €50 UNC Benchmark", 75.0, 1.4, "Central Bank")
    )
  }

  val filteredCatalog = EuroCatalogData.ALL_CATALOG_ITEMS.filter { spec ->
    val matchesType = when (selectedType) {
      "Coins" -> spec.itemType == "coin"
      "Notes" -> spec.itemType == "banknote"
      else -> true
    }
    val matchesCountry = selectedCountry == "All Countries" || spec.country.equals(selectedCountry, ignoreCase = true)
    val matchesSearch = catalogSearchQuery.isBlank() ||
      spec.name.contains(catalogSearchQuery, ignoreCase = true) ||
      spec.denomination.contains(catalogSearchQuery, ignoreCase = true) ||
      spec.country.contains(catalogSearchQuery, ignoreCase = true)
    matchesType && matchesCountry && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkSlateBackground)
      .statusBarsPadding()
      .padding(bottom = 90.dp),
    contentPadding = PaddingValues(bottom = 20.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Market & Valuation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EuroGoldLight
          )
          Text(
            text = "Live index movements & catalog price guide",
            style = MaterialTheme.typography.bodySmall,
            color = EuroSilver
          )
        }
        Icon(
          imageVector = Icons.Default.ShowChart,
          contentDescription = null,
          tint = EuroGoldPrimary,
          modifier = Modifier.size(26.dp)
        )
      }
    }

    // Market Ticker Carousel
    item {
      Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
          text = "NUMISMATIC INDEX TICKER (24H)",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
          color = EuroSilver,
          letterSpacing = 1.sp,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(marketTickers) { ticker ->
            Box(
              modifier = Modifier
                .width(170.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSlateSurface)
                .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Column {
                Text(
                  text = ticker.category.uppercase(),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = EuroSilver
                )
                Text(
                  text = ticker.name,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFF1F5F9),
                  maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "€${"%,.0f".format(ticker.currentPrice)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = EuroGoldLight
                  )
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.ArrowUpward,
                      contentDescription = null,
                      tint = EmeraldSuccess,
                      modifier = Modifier.size(11.dp)
                    )
                    Text(
                      text = "+${ticker.changePercent}%",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = EmeraldSuccess
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Interactive 12-Month Valuation Trend Chart
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSlateSurface)
          .border(1.dp, DarkSlateBorder, RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "12-MONTH PORTFOLIO GROWTH",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = EuroSilver,
                letterSpacing = 1.sp
              )
              Text(
                text = "€${"%,.2f".format(if (totalValue > 0) totalValue else 4850.0)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = EuroGoldLight
              )
            }
            Surface(
              color = EmeraldSuccess.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "+${if (gainPct != 0.0) "%.1f".format(gainPct) else "24.6"}% 1Y",
                color = EmeraldSuccess,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Chart Canvas
          Canvas(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
          ) {
            // Sample historical progression normalized (12 points)
            val points = listOf(
              0.25f, 0.28f, 0.32f, 0.30f, 0.42f, 0.48f,
              0.45f, 0.58f, 0.65f, 0.72f, 0.85f, 1.0f
            )

            val widthStep = size.width / (points.size - 1)
            val chartHeight = size.height - 20f

            val path = Path()
            val fillPath = Path()

            points.forEachIndexed { index, ratio ->
              val x = index * widthStep
              val y = size.height - (ratio * chartHeight) - 10f
              if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
              } else {
                val prevX = (index - 1) * widthStep
                val prevY = size.height - (points[index - 1] * chartHeight) - 10f
                val controlX1 = (prevX + x) / 2f
                path.cubicTo(controlX1, prevY, controlX1, y, x, y)
                fillPath.cubicTo(controlX1, prevY, controlX1, y, x, y)
              }
              if (index == points.size - 1) {
                fillPath.lineTo(x, size.height)
                fillPath.close()
              }
            }

            // Fill gradient below curve
            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(EuroGoldPrimary.copy(alpha = 0.35f), Color.Transparent)
              )
            )

            // Stroke line
            drawPath(
              path = path,
              brush = Brush.horizontalGradient(
                colors = listOf(EuroGoldDark, EuroGoldPrimary, EuroGoldLight)
              ),
              style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // End pulse circle
            val lastX = (points.size - 1) * widthStep
            val lastY = size.height - (points.last() * chartHeight) - 10f
            drawCircle(
              color = EuroGoldLight,
              radius = 5f,
              center = Offset(lastX, lastY)
            )
            drawCircle(
              color = EuroGoldPrimary,
              radius = 9f,
              center = Offset(lastX, lastY),
              style = Stroke(width = 2f)
            )
          }

          // Month labels
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            listOf("Jan", "Mar", "May", "Jul", "Sep", "Nov").forEach { m ->
              Text(text = m, fontSize = 10.sp, color = EuroSilver)
            }
          }
        }
      }
    }

    // Catalog Price Guide Explorer Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
      ) {
        Text(
          text = "EURO CATALOG & VALUATION GUIDE",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.ExtraBold,
          color = EuroSilver,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = catalogSearchQuery,
          onValueChange = { catalogSearchQuery = it },
          placeholder = { Text("Filter catalog by country, name...", fontSize = 13.sp, color = EuroSilver) },
          leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EuroGoldPrimary) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("catalog_search_field"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EuroGoldPrimary,
            unfocusedBorderColor = DarkSlateBorder,
            focusedContainerColor = DarkSlateSurface,
            unfocusedContainerColor = DarkSlateSurface,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Type filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("All", "Coins", "Notes").forEach { type ->
            val isSelected = selectedType == type
            FilterChip(
              selected = isSelected,
              onClick = { selectedType = type },
              label = { Text(text = type, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EuroGoldPrimary,
                selectedLabelColor = Color(0xFF1E1705),
                containerColor = DarkSlateSurface,
                labelColor = EuroSilver
              )
            )
          }
        }
      }
    }

    if (addedMessage != null) {
      item {
        Surface(
          color = Color(0x3310B981),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
          Text(
            text = addedMessage ?: "",
            color = EmeraldSuccess,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }

    // Catalog Specimen Items
    items(filteredCatalog) { spec ->
      CatalogSpecimenCard(
        spec = spec,
        onInspect = { onInspectInLoupe(spec) },
        onAddToVault = {
          coroutineScope.launch {
            val item = CollectorItem(
              userId = currentUserId,
              itemType = spec.itemType,
              denomination = spec.denomination,
              country = spec.country,
              year = spec.year,
              seriesOrMint = spec.series,
              conditionGrade = "UNC",
              purchasePrice = spec.estimatedCirculatedValue,
              currentMarketValue = spec.estimatedUncValue,
              notes = spec.specifications
            )
            repository.addItem(currentUserId, item)
            addedMessage = "Added ${spec.name} to your Vault!"
          }
        }
      )
    }
  }
}

@Composable
private fun CatalogSpecimenCard(
  spec: CatalogItemSpec,
  onInspect: () -> Unit,
  onAddToVault: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSlateSurface)
      .border(1.dp, DarkSlateBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(modifier = Modifier.size(46.dp), contentAlignment = Alignment.Center) {
            if (spec.itemType == "coin") {
              EuroCoinVisual(denomination = spec.denomination, country = spec.country, year = spec.year, sizeDp = 44.dp)
            } else {
              EuroBanknoteVisual(denomination = spec.denomination, widthDp = 46.dp, heightDp = 30.dp)
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = spec.name,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFF1F5F9)
            )
            Text(
              text = "${spec.country} (${spec.year}) • ${spec.series}",
              fontSize = 11.sp,
              color = EuroSilver
            )
          }
        }

        RarityBadge(tier = spec.rarityTier)
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = spec.specifications,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8),
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Grade Price Guide Matrix
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSlateSurfaceVariant, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "CIRCULATED", fontSize = 9.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
          Text(text = "€${"%.0f".format(spec.estimatedCirculatedValue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
        }
        Column {
          Text(text = "UNC / MINT", fontSize = 9.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
          Text(text = "€${"%.0f".format(spec.estimatedUncValue)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EuroGoldPrimary)
        }
        Column {
          Text(text = "MINTAGE", fontSize = 9.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
          Text(
            text = if (spec.mintage > 0) "%,d".format(spec.mintage) else "N/A",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE2E8F0)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onInspect,
          modifier = Modifier.weight(1f).height(38.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Inspect Loupe", fontSize = 11.sp, color = EuroGoldLight)
        }

        Button(
          onClick = onAddToVault,
          modifier = Modifier.weight(1f).height(38.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EuroGoldPrimary, contentColor = Color(0xFF1E1705))
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Add to Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
