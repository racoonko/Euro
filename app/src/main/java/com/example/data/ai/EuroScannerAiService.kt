package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "EuroScannerAiService"

data class EuroScanResult(
  val itemType: String, // "coin" or "banknote"
  val denomination: String,
  val country: String,
  val year: Long,
  val seriesOrMint: String,
  val estimatedGrade: String,
  val estimatedMarketValue: Double,
  val authenticityAssessment: String,
  val securityFeaturesObserved: List<String>,
  val keyCollectorNotes: String
)

class EuroScannerAiService {

  private val scanResultSchema by lazy {
    Schema.obj(
      properties = mapOf(
        "itemType" to Schema.string(description = "Either 'coin' or 'banknote'"),
        "denomination" to Schema.string(description = "Exact denomination label, e.g. '€2.00', '€0.50', '€50', etc."),
        "country" to Schema.string(description = "Country of issue, e.g. Germany, Monaco, France, Vatican City, Italy, Spain"),
        "year" to Schema.integer(description = "Mint or issue year, e.g. 2007"),
        "seriesOrMint" to Schema.string(description = "Mint mark, commemorative subject, or series (e.g. Mint A - Berlin, Grace Kelly, Europa Series)"),
        "estimatedGrade" to Schema.string(description = "Estimated condition grade: UNC, AU, XF, VF, F, VG, or PROOF"),
        "estimatedMarketValue" to Schema.double(description = "Estimated numismatic collector market value in Euros"),
        "authenticityAssessment" to Schema.string(description = "Authenticity check summary (Genuine, Suspicious, High-Quality Strike, etc.)"),
        "securityFeaturesObserved" to Schema.array(
          items = Schema.string(),
          description = "Key visible security features or mint markers observed (e.g. edge lettering, watermark, hologram, luster)"
        ),
        "keyCollectorNotes" to Schema.string(description = "Detailed numismatic notes, variety details, mintage context, and grading rationale")
      ),
      optionalProperties = listOf("seriesOrMint", "authenticityAssessment")
    )
  }

  private val generativeModel by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = scanResultSchema
      }
    )
  }

  suspend fun analyzeImage(bitmap: Bitmap): Result<EuroScanResult> {
    return try {
      val prompt = """
        You are a world-class professional numismatist and expert in Euro coins and banknotes.
        Examine this high-resolution image of a Euro coin or banknote.
        Analyze:
        1. Whether it is a coin or banknote.
        2. Exact denomination (e.g., €2.00, €0.50, €50, €100).
        3. Country of origin (from national side design, stars, heraldry, or banknote designators).
        4. Year of issue / mint mark (e.g., Germany A/D/F/G/J, France, Spain, Monaco, Vatican, etc.).
        5. Series / variety (e.g., 2002 1st series, Europa series, commemorative subject like Treaty of Rome, Grace Kelly, etc.).
        6. Wear and surface condition to estimate Sheldon grading category (UNC, AU, XF, VF, F, VG, PROOF).
        7. Realistic collector market value in Euros (€) based on rarity, mintage, and condition.
        8. Visible security elements (luster, edge lettering, intaglio relief, watermark, hologram, microprint).
        Provide the complete analysis in the requested JSON structure.
      """.trimIndent()

      val response = withTimeout(60_000L) {
        generativeModel.generateContent(
          content {
            image(bitmap)
            text(prompt)
          }
        )
      }

      val jsonString = response.text ?: throw IllegalStateException("Model returned empty text.")
      val json = JSONObject(jsonString)

      val itemType = json.optString("itemType", "coin")
      val denomination = json.optString("denomination", "€2.00")
      val country = json.optString("country", "European Union")
      val year = json.optLong("year", 2002L)
      val seriesOrMint = json.optString("seriesOrMint", "")
      val estimatedGrade = json.optString("estimatedGrade", "UNC")
      val estimatedMarketValue = json.optDouble("estimatedMarketValue", 10.0)
      val authenticityAssessment = json.optString("authenticityAssessment", "Genuine Strike")
      val secArray = json.optJSONArray("securityFeaturesObserved")
      val securityFeatures = if (secArray != null) {
        (0 until secArray.length()).map { secArray.getString(it) }
      } else {
        emptyList()
      }
      val notes = json.optString("keyCollectorNotes", "High-detail numismatic inspection.")

      Result.success(
        EuroScanResult(
          itemType = itemType,
          denomination = denomination,
          country = country,
          year = year,
          seriesOrMint = seriesOrMint,
          estimatedGrade = estimatedGrade,
          estimatedMarketValue = estimatedMarketValue,
          authenticityAssessment = authenticityAssessment,
          securityFeaturesObserved = securityFeatures,
          keyCollectorNotes = notes
        )
      )
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "AI scan timed out", e)
      Result.failure(Exception("AI scan timed out after 60 seconds. Please try again."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "AI scan analysis failed", e)
      Result.failure(e)
    }
  }
}
