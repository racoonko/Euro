package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CollectorItem(
  @DocumentId
  val id: String = "",
  val userId: String = "",
  val itemType: String = "coin", // "coin" or "banknote"
  val denomination: String = "€2.00",
  val country: String = "Germany",
  val year: Long = 2002L,
  val seriesOrMint: String = "",
  val conditionGrade: String = "UNC", // "UNC", "AU", "XF", "VF", "F", "VG", "G", "PROOF"
  val purchasePrice: Double = 0.0,
  val currentMarketValue: Double = 0.0,
  val notes: String = "",
  val highResPhotoUri: String = "",
  val hasCertification: Boolean = false,
  val certCompany: String = "",
  val certNumber: String = "",
  val isFavorite: Boolean = false,
  @ServerTimestamp
  val createdAt: Timestamp? = null,
  @ServerTimestamp
  val updatedAt: Timestamp? = null
)
