package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CollectorItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CollectorRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(expectedUserId: String? = null): String {
    val uid = auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    if (expectedUserId != null && uid != expectedUserId) {
      throw SecurityException("Authenticated UID ($uid) does not match expected UID ($expectedUserId)")
    }
    return uid
  }

  fun observeItems(userId: String): Flow<List<CollectorItem>> = flow {
    val uid = requireUserId(userId)
    val path = "users/$uid/items"
    emitAll(
      db.collection("users").document(uid).collection("items")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(CollectorItem::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun getItemById(userId: String, itemId: String): Result<CollectorItem?> {
    val uid = requireUserId(userId)
    val docRef = db.collection("users").document(uid).collection("items").document(itemId)
    return try {
      val snapshot = docRef.get().await()
      Result.success(snapshot.toObject(CollectorItem::class.java))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun addItem(userId: String, item: CollectorItem): Result<String> {
    val uid = requireUserId(userId)
    val docRef = db.collection("users").document(uid).collection("items").document()
    val payload = mutableMapOf<String, Any>(
      "userId" to uid,
      "itemType" to item.itemType,
      "denomination" to item.denomination,
      "country" to item.country,
      "year" to item.year,
      "conditionGrade" to item.conditionGrade,
      "purchasePrice" to item.purchasePrice,
      "currentMarketValue" to item.currentMarketValue,
      "hasCertification" to item.hasCertification,
      "isFavorite" to item.isFavorite,
      "createdAt" to FieldValue.serverTimestamp()
    )
    if (item.seriesOrMint.isNotBlank()) payload["seriesOrMint"] = item.seriesOrMint
    if (item.notes.isNotBlank()) payload["notes"] = item.notes
    if (item.highResPhotoUri.isNotBlank()) payload["highResPhotoUri"] = item.highResPhotoUri
    if (item.certCompany.isNotBlank()) payload["certCompany"] = item.certCompany
    if (item.certNumber.isNotBlank()) payload["certNumber"] = item.certNumber

    return try {
      docRef.set(payload).await()
      Result.success(docRef.id)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun updateItem(userId: String, item: CollectorItem): Result<Unit> {
    val uid = requireUserId(userId)
    require(item.id.isNotBlank()) { "Item ID must not be blank for update" }
    requireNotNull(item.createdAt) { "Item createdAt must not be null for update" }
    val docRef = db.collection("users").document(uid).collection("items").document(item.id)

    val payload = mutableMapOf<String, Any>(
      "userId" to uid,
      "itemType" to item.itemType,
      "denomination" to item.denomination,
      "country" to item.country,
      "year" to item.year,
      "conditionGrade" to item.conditionGrade,
      "purchasePrice" to item.purchasePrice,
      "currentMarketValue" to item.currentMarketValue,
      "hasCertification" to item.hasCertification,
      "isFavorite" to item.isFavorite,
      "createdAt" to item.createdAt,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    if (item.seriesOrMint.isNotBlank()) payload["seriesOrMint"] = item.seriesOrMint
    if (item.notes.isNotBlank()) payload["notes"] = item.notes
    if (item.highResPhotoUri.isNotBlank()) payload["highResPhotoUri"] = item.highResPhotoUri
    if (item.certCompany.isNotBlank()) payload["certCompany"] = item.certCompany
    if (item.certNumber.isNotBlank()) payload["certNumber"] = item.certNumber

    return try {
      docRef.set(payload).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun toggleFavorite(userId: String, itemId: String, isFavorite: Boolean): Result<Unit> {
    val uid = requireUserId(userId)
    val docRef = db.collection("users").document(uid).collection("items").document(itemId)
    return try {
      docRef.update(
        mapOf(
          "isFavorite" to isFavorite,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun deleteItem(userId: String, itemId: String): Result<Unit> {
    val uid = requireUserId(userId)
    val docRef = db.collection("users").document(uid).collection("items").document(itemId)
    return try {
      docRef.delete().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      Result.failure(e)
    }
  }
}
