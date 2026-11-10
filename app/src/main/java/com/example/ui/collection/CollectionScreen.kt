package com.example.ui.collection

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItemSpec
import com.example.data.model.CollectorItem
import com.example.data.model.EuroCatalogData
import com.example.data.repository.CollectorRepository
import com.example.ui.components.EuroBanknoteVisual
import com.example.ui.components.EuroCoinVisual
import com.example.ui.components.MarketChangePill
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
  currentUserId: String,
  onInspectItemInLoupe: (CatalogItemSpec) -> Unit,
  isGuest: Boolean = false,
  onRequestSignIn: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val repository = remember { CollectorRepository(context) }

  val itemsFlow = remember(currentUserId, isGuest) {
    if (isGuest) {
      kotlinx.coroutines.flow.flowOf(emptyList())
    } else {
      repository.observeItems(currentUserId).catch { emit(emptyList()) }
    }
  }
  val firestoreItems by itemsFlow.collectAsState(initial = emptyList())

  var guestItems by remember {
    mutableStateOf(
      listOf(
        CollectorItem(
          id = "guest_item_1",
          userId = "guest_collector",
          itemType = "coin",
          denomination = "€2.00",
          country = "Monaco",
          year = 2007L,
          seriesOrMint = "Grace Kelly (Key Date)",
          conditionGrade = "UNC",
          purchasePrice = 2400.0,
          currentMarketValue = 3850.0,
          notes = "Rare 2007 commemorative coin celebrating Princess Grace of Monaco. Mintage: 20,001.",
          isFavorite = true
        ),
        CollectorItem(
          id = "guest_item_2",
          userId = "guest_collector",
          itemType = "banknote",
          denomination = "€50",
          country = "European Union",
          year = 2017L,
          seriesOrMint = "Europa Series",
          conditionGrade = "UNC",
          purchasePrice = 50.0,
          currentMarketValue = 75.0,
          notes = "Crisp mint banknote with portrait window hologram and emerald 50 number.",
          isFavorite = true
        ),
        CollectorItem(
          id = "guest_item_3",
          userId = "guest_collector",
          itemType = "coin",
          denomination = "€2.00",
          country = "Germany",
          year = 2008L,
          seriesOrMint = "Hamburg St. Michael (Old Map Error)",
          conditionGrade = "AU",
          purchasePrice = 45.0,
          currentMarketValue = 95.0,
          notes = "Rare variety error struck with obsolete pre-2007 map reverse."
        )
      )
    )
  }

  val collectorItems = if (isGuest) guestItems else firestoreItems

  var searchQuery by remember { mutableStateOf("") }
  var selectedTypeFilter by remember { mutableStateOf("All") } // "All", "Coins", "Notes", "Favorites"
  var selectedCountryFilter by remember { mutableStateOf("All Countries") }
  var isGridView by remember { mutableStateOf(false) }

  var selectedItemForDetail by remember { mutableStateOf<CollectorItem?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }
  var showQuickCatalogPicker by remember { mutableStateOf(false) }

  // Filtered items
  val filteredItems = collectorItems.filter { item ->
    val matchesType = when (selectedTypeFilter) {
      "Coins" -> item.itemType == "coin"
      "Notes" -> item.itemType == "banknote"
      "Favorites" -> item.isFavorite
      else -> true
    }
    val matchesCountry = selectedCountryFilter == "All Countries" || item.country.equals(selectedCountryFilter, ignoreCase = true)
    val matchesSearch = searchQuery.isBlank() ||
      item.denomination.contains(searchQuery, ignoreCase = true) ||
      item.country.contains(searchQuery, ignoreCase = true) ||
      item.seriesOrMint.contains(searchQuery, ignoreCase = true) ||
      item.notes.contains(searchQuery, ignoreCase = true)
    matchesType && matchesCountry && matchesSearch
  }

  // Portfolio calculation
  val totalMarketValue = collectorItems.sumOf { it.currentMarketValue }
  val totalCostBasis = collectorItems.sumOf { it.purchasePrice }
  val totalGainLoss = totalMarketValue - totalCostBasis
  val gainLossPercent = if (totalCostBasis > 0) (totalGainLoss / totalCostBasis) * 100.0 else 0.0

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkSlateBackground)
      .statusBarsPadding()
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 90.dp),
      contentPadding = PaddingValues(bottom = 16.dp)
    ) {
      // Header: Portfolio Vault Summary
      item {
        VaultSummaryHeader(
          totalValue = totalMarketValue,
          totalCost = totalCostBasis,
          profitEuro = totalGainLoss,
          profitPercent = gainLossPercent,
          totalCount = collectorItems.size,
          coinsCount = collectorItems.count { it.itemType == "coin" },
          notesCount = collectorItems.count { it.itemType == "banknote" }
        )
      }

      if (isGuest) {
        item {
          GuestVaultBanner(onRequestSignIn = onRequestSignIn)
        }
      }

      // Search & View Toggle
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search denomination, country...", fontSize = 13.sp, color = EuroSilver) },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EuroGoldPrimary)
              },
              modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("vault_search_field"),
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

            // Grid / List View Toggle
            IconButton(
              onClick = { isGridView = !isGridView },
              modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSlateSurface)
                .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp))
                .testTag("toggle_view_button")
            ) {
              Icon(
                imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                contentDescription = "Toggle view",
                tint = EuroGoldLight
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Filter Chips Row
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            val filters = listOf("All", "Coins", "Notes", "Favorites")
            items(filters) { filter ->
              val isSelected = selectedTypeFilter == filter
              FilterChip(
                selected = isSelected,
                onClick = { selectedTypeFilter = filter },
                label = { Text(text = filter, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = EuroGoldPrimary,
                  selectedLabelColor = Color(0xFF1E1705),
                  containerColor = DarkSlateSurface,
                  labelColor = EuroSilver
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) EuroGoldPrimary else DarkSlateBorder,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }
        }
      }

      // Empty State
      if (filteredItems.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp, start = 20.dp, end = 20.dp)
              .background(DarkSlateSurface, RoundedCornerShape(16.dp))
              .border(1.dp, DarkSlateBorder, RoundedCornerShape(16.dp))
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(DarkSlateSurfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "€", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = EuroGoldPrimary)
              }
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = if (collectorItems.isEmpty()) "Your Vault is Empty" else "No matching items found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EuroGoldLight
              )
              Text(
                text = if (collectorItems.isEmpty())
                  "Start cataloging your Euro coins and banknotes using high-res AI scanning or quick-add from the catalog."
                else "Try adjusting your search terms or filters.",
                style = MaterialTheme.typography.bodySmall,
                color = EuroSilver,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
              )

              if (collectorItems.isEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                  onClick = { showQuickCatalogPicker = true },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = EuroGoldPrimary,
                    contentColor = Color(0xFF1E1705)
                  ),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Add Starter Specimen", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      } else {
        // Items List
        items(filteredItems) { item ->
          CollectorItemRow(
            item = item,
            onClick = { selectedItemForDetail = item },
            onToggleFavorite = {
              if (isGuest) {
                guestItems = guestItems.map { if (it.id == item.id) it.copy(isFavorite = !it.isFavorite) else it }
              } else {
                coroutineScope.launch {
                  repository.toggleFavorite(currentUserId, item.id, !item.isFavorite)
                }
              }
            }
          )
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = EuroGoldPrimary,
      contentColor = Color(0xFF1E1705),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 100.dp, end = 20.dp)
        .testTag("add_item_fab")
    ) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
    }

    // Detail Bottom Sheet
    if (selectedItemForDetail != null) {
      val item = selectedItemForDetail!!
      ModalBottomSheet(
        onDismissRequest = { selectedItemForDetail = null },
        sheetState = rememberModalBottomSheetState(),
        containerColor = DarkSlateSurfaceElevated
      ) {
        ItemDetailSheetContent(
          item = item,
          onInspectInLoupe = {
            selectedItemForDetail = null
            val spec = EuroCatalogData.ALL_CATALOG_ITEMS.firstOrNull {
              it.denomination == item.denomination && it.country == item.country
            } ?: EuroCatalogData.ALL_CATALOG_ITEMS.first()
            onInspectItemInLoupe(spec)
          },
          onDeleteItem = {
            if (isGuest) {
              guestItems = guestItems.filter { it.id != item.id }
              selectedItemForDetail = null
            } else {
              coroutineScope.launch {
                repository.deleteItem(currentUserId, item.id)
                selectedItemForDetail = null
              }
            }
          }
        )
      }
    }

    // Add Item Dialog
    if (showAddDialog) {
      AddItemDialog(
        currentUserId = currentUserId,
        onDismiss = { showAddDialog = false },
        onSave = { newItem ->
          if (isGuest) {
            val guestItem = newItem.copy(id = "guest_${System.currentTimeMillis()}")
            guestItems = listOf(guestItem) + guestItems
            showAddDialog = false
          } else {
            coroutineScope.launch {
              repository.addItem(currentUserId, newItem)
              showAddDialog = false
            }
          }
        },
        onPickFromCatalog = {
          showAddDialog = false
          showQuickCatalogPicker = true
        }
      )
    }

    // Quick Catalog Picker Dialog
    if (showQuickCatalogPicker) {
      QuickCatalogPickerDialog(
        currentUserId = currentUserId,
        onDismiss = { showQuickCatalogPicker = false },
        onSelectSpecimen = { spec ->
          val newItem = CollectorItem(
            id = if (isGuest) "guest_${System.currentTimeMillis()}" else "",
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
          if (isGuest) {
            guestItems = listOf(newItem) + guestItems
            showQuickCatalogPicker = false
          } else {
            coroutineScope.launch {
              repository.addItem(currentUserId, newItem)
              showQuickCatalogPicker = false
            }
          }
        }
      )
    }
  }
}

@Composable
private fun GuestVaultBanner(
  onRequestSignIn: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
      .border(1.dp, EuroGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(DarkSlateSurfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.LockOpen,
          contentDescription = null,
          tint = EuroGoldLight,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Anonymous Guest Vault",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = EuroGoldLight
        )
        Text(
          text = "Items stored locally. Sign in with Google to cloud sync.",
          style = MaterialTheme.typography.bodySmall,
          color = EuroSilver,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onRequestSignIn,
        colors = ButtonDefaults.buttonColors(
          containerColor = EuroGoldPrimary,
          contentColor = Color(0xFF1E1705)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("guest_banner_sign_in_button")
      ) {
        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }
    }
  }
}

@Composable
private fun VaultSummaryHeader(
  totalValue: Double,
  totalCost: Double,
  profitEuro: Double,
  profitPercent: Double,
  totalCount: Int,
  coinsCount: Int,
  notesCount: Int
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(
            DarkSlateSurfaceElevated,
            DarkSlateSurface,
            DarkSlateSurfaceElevated
          )
        )
      )
      .border(1.5.dp, EuroGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
      .padding(20.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "TOTAL VAULT VALUE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = EuroSilver,
            letterSpacing = 1.sp
          )
          Text(
            text = "€${"%,.2f".format(totalValue)}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = EuroGoldLight
          )
        }

        MarketChangePill(
          profitEuro = profitEuro,
          profitPercent = profitPercent
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSlateSurfaceVariant, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "COST BASIS", fontSize = 10.sp, color = EuroSilver, fontWeight = FontWeight.SemiBold)
          Text(text = "€${"%,.2f".format(totalCost)}", fontSize = 13.sp, color = Color(0xFFF1F5F9), fontWeight = FontWeight.Bold)
        }
        Column {
          Text(text = "TOTAL ITEMS", fontSize = 10.sp, color = EuroSilver, fontWeight = FontWeight.SemiBold)
          Text(text = "$totalCount ($coinsCount coins, $notesCount notes)", fontSize = 13.sp, color = Color(0xFFF1F5F9), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun CollectorItemRow(
  item: CollectorItem,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSlateSurface)
      .border(1.dp, DarkSlateBorder, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Visual Thumbnail
    Box(
      modifier = Modifier.size(54.dp),
      contentAlignment = Alignment.Center
    ) {
      if (item.itemType == "coin") {
        EuroCoinVisual(
          denomination = item.denomination,
          country = item.country,
          year = item.year,
          sizeDp = 50.dp
        )
      } else {
        EuroBanknoteVisual(
          denomination = item.denomination,
          widthDp = 52.dp,
          heightDp = 34.dp
        )
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    // Description & specs
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${item.denomination} • ${item.country}",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
          color = Color(0xFFF1F5F9)
        )
        Spacer(modifier = Modifier.width(6.dp))
        SheldonGradeBadge(grade = item.conditionGrade)
      }

      Text(
        text = "${item.year} ${if (item.seriesOrMint.isNotBlank()) "• " + item.seriesOrMint else ""}",
        fontSize = 12.sp,
        color = EuroSilver
      )

      val profit = item.currentMarketValue - item.purchasePrice
      val profitPct = if (item.purchasePrice > 0) (profit / item.purchasePrice) * 100.0 else 0.0
      Text(
        text = "Cost: €${"%.2f".format(item.purchasePrice)}  ➔  ${if (profit >= 0) "+" else ""}€${"%.2f".format(profit)} (${if (profit >= 0) "+" else ""}${"%.1f".format(profitPct)}%)",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (profit >= 0) EmeraldSuccess else Color(0xFFEF4444)
      )
    }

    // Market Value & Favorite toggle
    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          contentDescription = "Favorite",
          tint = if (item.isFavorite) EuroGoldPrimary else Color(0xFF64748B),
          modifier = Modifier.size(18.dp)
        )
      }

      Text(
        text = "€${"%.2f".format(item.currentMarketValue)}",
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        color = EuroGoldLight
      )
    }
  }
}

@Composable
private fun ItemDetailSheetContent(
  item: CollectorItem,
  onInspectInLoupe: () -> Unit,
  onDeleteItem: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "${item.denomination} ${item.country}",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Black,
          color = EuroGoldLight
        )
        Text(
          text = "${item.year} ${item.seriesOrMint}",
          style = MaterialTheme.typography.bodyMedium,
          color = EuroSilver
        )
      }

      SheldonGradeBadge(grade = item.conditionGrade)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Value metrics
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(DarkSlateSurface, RoundedCornerShape(12.dp))
        .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp))
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(text = "CURRENT VALUE", fontSize = 10.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
        Text(text = "€${"%.2f".format(item.currentMarketValue)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = EuroGoldPrimary)
      }
      Column {
        Text(text = "PURCHASE COST", fontSize = 10.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
        Text(text = "€${"%.2f".format(item.purchasePrice)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
      }
      val netReturn = item.currentMarketValue - item.purchasePrice
      val returnPct = if (item.purchasePrice > 0) (netReturn / item.purchasePrice) * 100.0 else 0.0
      Column(horizontalAlignment = Alignment.End) {
        Text(text = "NET RETURN", fontSize = 10.sp, color = EuroSilver, fontWeight = FontWeight.Bold)
        Text(
          text = "${if (netReturn >= 0) "+" else ""}€${"%.2f".format(netReturn)} (${if (netReturn >= 0) "+" else ""}${"%.1f".format(returnPct)}%)",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (netReturn >= 0) EmeraldSuccess else Color(0xFFEF4444)
        )
      }
    }

    if (item.notes.isNotBlank()) {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Collector Notes:",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = EuroSilver
      )
      Text(
        text = item.notes,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFFCBD5E1),
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedButton(
        onClick = onDeleteItem,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
      ) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Delete", fontSize = 12.sp)
      }

      Button(
        onClick = onInspectInLoupe,
        modifier = Modifier.weight(1.5f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = EuroGoldPrimary,
          contentColor = Color(0xFF1E1705)
        )
      ) {
        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Inspect in 10x Loupe", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Composable
private fun AddItemDialog(
  currentUserId: String,
  onDismiss: () -> Unit,
  onSave: (CollectorItem) -> Unit,
  onPickFromCatalog: () -> Unit
) {
  var itemType by remember { mutableStateOf("coin") }
  var denomination by remember { mutableStateOf("€2.00") }
  var country by remember { mutableStateOf("Germany") }
  var yearText by remember { mutableStateOf("2020") }
  var seriesOrMint by remember { mutableStateOf("") }
  var conditionGrade by remember { mutableStateOf("UNC") }
  var purchasePriceText by remember { mutableStateOf("2.00") }
  var marketValueText by remember { mutableStateOf("5.00") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSlateSurface,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Add to Euro Vault", color = EuroGoldLight, fontWeight = FontWeight.Bold)
        TextButton(onClick = onPickFromCatalog) {
          Text("Pick from Catalog", color = EuroGoldPrimary, fontSize = 12.sp)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Coin vs Banknote
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = itemType == "coin",
            onClick = { itemType = "coin" },
            label = { Text("Coin") }
          )
          FilterChip(
            selected = itemType == "banknote",
            onClick = { itemType = "banknote" },
            label = { Text("Banknote") }
          )
        }

        OutlinedTextField(
          value = denomination,
          onValueChange = { denomination = it },
          label = { Text("Denomination (e.g. €2.00, €50)") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = country,
          onValueChange = { country = it },
          label = { Text("Country of Issue") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = yearText,
            onValueChange = { yearText = it },
            label = { Text("Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = conditionGrade,
            onValueChange = { conditionGrade = it },
            label = { Text("Grade (UNC, AU, etc.)") },
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = seriesOrMint,
          onValueChange = { seriesOrMint = it },
          label = { Text("Mint Mark / Variety (Optional)") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = purchasePriceText,
            onValueChange = { purchasePriceText = it },
            label = { Text("Cost (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = marketValueText,
            onValueChange = { marketValueText = it },
            label = { Text("Market Value (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Personal Notes / Slab Number") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val year = yearText.toLongOrNull() ?: 2002L
          val cost = purchasePriceText.toDoubleOrNull() ?: 0.0
          val value = marketValueText.toDoubleOrNull() ?: cost
          onSave(
            CollectorItem(
              userId = currentUserId,
              itemType = itemType,
              denomination = denomination,
              country = country,
              year = year,
              seriesOrMint = seriesOrMint,
              conditionGrade = conditionGrade,
              purchasePrice = cost,
              currentMarketValue = value,
              notes = notes
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = EuroGoldPrimary, contentColor = Color(0xFF1E1705))
      ) {
        Text("Save Item", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = EuroSilver)
      }
    }
  )
}

@Composable
private fun QuickCatalogPickerDialog(
  currentUserId: String,
  onDismiss: () -> Unit,
  onSelectSpecimen: (CatalogItemSpec) -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSlateSurface,
    title = {
      Text("Choose Reference Specimen", color = EuroGoldLight, fontWeight = FontWeight.Bold)
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(350.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(EuroCatalogData.ALL_CATALOG_ITEMS) { spec ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(DarkSlateSurfaceVariant)
              .clickable { onSelectSpecimen(spec) }
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${spec.denomination} ${spec.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9)
              )
              Text(
                text = "${spec.country} (${spec.year}) • ${spec.series}",
                fontSize = 11.sp,
                color = EuroSilver
              )
            }
            Text(
              text = "€${"%.0f".format(spec.estimatedUncValue)}",
              fontWeight = FontWeight.ExtraBold,
              color = EuroGoldPrimary
            )
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = EuroSilver)
      }
    }
  )
}
