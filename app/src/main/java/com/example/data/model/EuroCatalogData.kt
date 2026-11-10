package com.example.data.model

data class CatalogItemSpec(
  val id: String,
  val name: String,
  val itemType: String, // "coin" or "banknote"
  val denomination: String,
  val country: String,
  val year: Long,
  val series: String,
  val estimatedUncValue: Double,
  val estimatedCirculatedValue: Double,
  val mintage: Long,
  val specifications: String,
  val securityFeatures: List<String>,
  val rarityTier: String // "Common", "Scarce", "Rare", "Ultra Rare / Key Date"
)

object EuroCatalogData {

  val ALL_CATALOG_ITEMS = listOf(
    // Rare & Commemorative 2-Euro Coins
    CatalogItemSpec(
      id = "coin_mc_2007_grace",
      name = "Monaco 2007 Princess Grace (Grace Kelly)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Monaco",
      year = 2007L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 3850.0,
      estimatedCirculatedValue = 2400.0,
      mintage = 20001L,
      specifications = "Bi-metallic: Nickel-brass center in Cupronickel ring. Weight: 8.50g, Diameter: 25.75mm. Edge lettering: 2 ★ repeated 6 times.",
      securityFeatures = listOf("Micro-engraved artist signature", "Specific reeded edge pattern", "Flawless mirror prooflike fields on genuine strikes"),
      rarityTier = "Ultra Rare / Key Date"
    ),
    CatalogItemSpec(
      id = "coin_va_2005_sedevacante",
      name = "Vatican City 2005 Sede Vacante",
      itemType = "coin",
      denomination = "€2.00",
      country = "Vatican City",
      year = 2005L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 320.0,
      estimatedCirculatedValue = 210.0,
      mintage = 60000L,
      specifications = "Bi-metallic. Weight: 8.50g, Diameter: 25.75mm. Cardinal Camerlengo coat of arms.",
      securityFeatures = listOf("Papal Chamber keys security engraving", "Edge inscription with Roman numeral details"),
      rarityTier = "Rare"
    ),
    CatalogItemSpec(
      id = "coin_sm_2004_borghesi",
      name = "San Marino 2004 Bartolomeo Borghesi",
      itemType = "coin",
      denomination = "€2.00",
      country = "San Marino",
      year = 2004L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 160.0,
      estimatedCirculatedValue = 95.0,
      mintage = 110000L,
      specifications = "First commemorative €2 coin of San Marino. Portrait of numismatist Bartolomeo Borghesi.",
      securityFeatures = listOf("Fine reeding", "Relief depth in classical bust profile"),
      rarityTier = "Scarce"
    ),
    CatalogItemSpec(
      id = "coin_fi_2004_enlargement",
      name = "Finland 2004 EU Enlargement (10 New States)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Finland",
      year = 2004L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 75.0,
      estimatedCirculatedValue = 38.0,
      mintage = 1000000L,
      specifications = "Stylized pillar with 10 shoots representing expanding Europe. Edge: SUOMI FINLAND ★★★.",
      securityFeatures = listOf("Pillar shoot micro-texture", "Distinct Finnish edge typography"),
      rarityTier = "Scarce"
    ),
    CatalogItemSpec(
      id = "coin_de_2008_st_michael_error",
      name = "Germany 2008 Hamburg St. Michael (Old Map Error)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Germany",
      year = 2008L,
      series = "Bundesländer Series (Mint F - Stuttgart)",
      estimatedUncValue = 95.0,
      estimatedCirculatedValue = 45.0,
      mintage = 75000L,
      specifications = "Rare variety struck with obsolete pre-2007 common European map rather than new expanded map.",
      securityFeatures = listOf("Obsolete 15-nation map reverse", "EINIGKEIT UND RECHT UND FREIHEIT edge"),
      rarityTier = "Rare"
    ),
    CatalogItemSpec(
      id = "coin_eu_2007_rome",
      name = "50th Anniversary Treaty of Rome (Joint Issue)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Germany",
      year = 2007L,
      series = "European Joint Commemorative",
      estimatedUncValue = 18.0,
      estimatedCirculatedValue = 5.0,
      mintage = 30000000L,
      specifications = "Treaty signed by the six founding member states on geometric pavement of Piazza del Campidoglio.",
      securityFeatures = listOf("Pavement micro-geometric lines", "Country-specific treaty inscription language"),
      rarityTier = "Common"
    ),
    CatalogItemSpec(
      id = "coin_fr_2019_asterix",
      name = "France 2019 60th Anniversary of Asterix",
      itemType = "coin",
      denomination = "€2.00",
      country = "France",
      year = 2019L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 42.0,
      estimatedCirculatedValue = 22.0,
      mintage = 310000L,
      specifications = "Asterix in profile with his winged helmet. RF (République Française) monogram.",
      securityFeatures = listOf("Intaglio die relief", "Winged helmet feather separation"),
      rarityTier = "Scarce"
    ),
    CatalogItemSpec(
      id = "coin_de_2002_a_2euro",
      name = "Germany 2002 2-Euro (Mint A - Berlin)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Germany",
      year = 2002L,
      series = "Standard Issue",
      estimatedUncValue = 14.0,
      estimatedCirculatedValue = 2.0,
      mintage = 211000000L,
      specifications = "German Federal Eagle symbol of sovereignty. Designed by Heinz and Sneschana Russewa-Hoyer.",
      securityFeatures = listOf("Eagle feather detail", "Reeded edge with EINIGKEIT UND RECHT UND FREIHEIT"),
      rarityTier = "Common"
    ),
    // Euro Banknotes
    CatalogItemSpec(
      id = "note_eu_europa_50",
      name = "€50 Europa Series Banknote",
      itemType = "banknote",
      denomination = "€50",
      country = "European Union",
      year = 2017L,
      series = "Europa Series",
      estimatedUncValue = 75.0,
      estimatedCirculatedValue = 50.0,
      mintage = 10000000000L,
      specifications = "140 x 77 mm, Orange color scheme. Renaissance architecture style.",
      securityFeatures = listOf(
        "Portrait window in hologram band: transparent portrait of Europa",
        "Emerald number: shiny 50 creates an effect of the light moving up and down",
        "Portrait watermark visible against the light",
        "Raised intaglio print on left and right edges",
        "UV-A / UV-C fluorescent fibers glowing yellow and green"
      ),
      rarityTier = "Common"
    ),
    CatalogItemSpec(
      id = "note_eu_europa_100",
      name = "€100 Europa Series Banknote",
      itemType = "banknote",
      denomination = "€100",
      country = "European Union",
      year = 2019L,
      series = "Europa Series",
      estimatedUncValue = 145.0,
      estimatedCirculatedValue = 100.0,
      mintage = 3500000000L,
      specifications = "147 x 77 mm, Green color scheme. Baroque and Rococo architectural style.",
      securityFeatures = listOf(
        "Satellite hologram at top of silvery stripe showing small € symbols orbiting",
        "Large emerald number with € symbols inside",
        "Europa portrait window hologram",
        "Microprinting under 10x magnification",
        "Dual-wavelength UV security reaction"
      ),
      rarityTier = "Scarce"
    ),
    CatalogItemSpec(
      id = "note_eu_2002_500",
      name = "€500 2002 First Series Banknote (Discontinued)",
      itemType = "banknote",
      denomination = "€500",
      country = "European Union",
      year = 2002L,
      series = "First Series 2002 (Issuance Stopped)",
      estimatedUncValue = 920.0,
      estimatedCirculatedValue = 540.0,
      mintage = 613000000L,
      specifications = "160 x 82 mm, Purple color scheme. Modern 20th century architecture. Highly sought after by collectors following ECB discontinuation.",
      securityFeatures = listOf(
        "Colour-shifting ink on reverse: numeral changes from purple to olive green/brown",
        "Hologram patch showing architectural motif and denomination",
        "Security thread with '500 EURO' microtext",
        "Complex multi-tone watermark"
      ),
      rarityTier = "Rare"
    ),
    CatalogItemSpec(
      id = "note_eu_europa_20",
      name = "€20 Europa Series Banknote",
      itemType = "banknote",
      denomination = "€20",
      country = "European Union",
      year = 2015L,
      series = "Europa Series",
      estimatedUncValue = 35.0,
      estimatedCirculatedValue = 20.0,
      mintage = 8000000000L,
      specifications = "133 x 72 mm, Blue color scheme. Gothic architectural style.",
      securityFeatures = listOf(
        "Portrait window in upper hologram",
        "Emerald number 20",
        "Gothic window watermark",
        "Tactile lines on sides"
      ),
      rarityTier = "Common"
    ),
    CatalogItemSpec(
      id = "coin_it_2002_dante",
      name = "Italy 2002 2-Euro (Dante Alighieri)",
      itemType = "coin",
      denomination = "€2.00",
      country = "Italy",
      year = 2002L,
      series = "Standard Issue",
      estimatedUncValue = 12.0,
      estimatedCirculatedValue = 2.0,
      mintage = 463402000L,
      specifications = "Portrait of Dante Alighieri drawn by Raphael in the Stanza della Segnatura. Weight: 8.5g.",
      securityFeatures = listOf("Intaglio laurel wreath details", "Italian edge lettering: 2 ★ repeated"),
      rarityTier = "Common"
    ),
    CatalogItemSpec(
      id = "coin_es_2014_felipe",
      name = "Spain 2014 Proclamation of King Felipe VI",
      itemType = "coin",
      denomination = "€2.00",
      country = "Spain",
      year = 2014L,
      series = "Commemorative 2-Euro",
      estimatedUncValue = 16.0,
      estimatedCirculatedValue = 4.5,
      mintage = 12000000L,
      specifications = "Dual portrait of King Juan Carlos I and King Felipe VI in overlapping profile.",
      securityFeatures = listOf("Overlapping profile relief transition", "Crowned M mintmark of Madrid"),
      rarityTier = "Common"
    )
  )

  val COUNTRIES = listOf(
    "All Countries",
    "Germany",
    "France",
    "Italy",
    "Spain",
    "Monaco",
    "Vatican City",
    "San Marino",
    "Finland",
    "Austria",
    "Netherlands",
    "Belgium",
    "European Union"
  )

  val DENOMINATIONS = listOf(
    "All",
    "€0.01", "€0.02", "€0.05", "€0.10", "€0.20", "€0.50", "€1.00", "€2.00",
    "€5", "€10", "€20", "€50", "€100", "€200", "€500"
  )

  val CONDITION_GRADES = listOf(
    "UNC" to "Uncirculated / Mint State (Flawless luster, no wear)",
    "AU" to "About Uncirculated (Traces of friction on highest points)",
    "XF" to "Extremely Fine (Light wear on sharp details)",
    "VF" to "Very Fine (Moderate wear, all major lettering clear)",
    "F" to "Fine (Noticeable wear, design still clearly defined)",
    "VG" to "Very Good (Considerable wear, rims full)",
    "PROOF" to "Proof (Mirror fields, frosted cameo relief)"
  )

  fun getGradeMultiplier(grade: String): Double {
    return when (grade) {
      "PROOF" -> 1.50
      "UNC" -> 1.00
      "AU" -> 0.75
      "XF" -> 0.55
      "VF" -> 0.38
      "F" -> 0.25
      "VG" -> 0.15
      else -> 0.30
    }
  }
}
