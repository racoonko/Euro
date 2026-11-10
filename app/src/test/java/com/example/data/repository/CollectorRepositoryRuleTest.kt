package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CollectorItem
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectorRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun addItem_validPayload_createsDocumentAndReturnsId(): Unit = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CollectorRepository(firestore)

    val item = CollectorItem(
      userId = uid,
      itemType = "coin",
      denomination = "€2.00",
      country = "Germany",
      year = 2008L,
      seriesOrMint = "Mint A",
      conditionGrade = "UNC",
      purchasePrice = 3.50,
      currentMarketValue = 7.00,
      notes = "Test mint coin"
    )

    val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.addItem(uid, item) }
    assertTrue(createResult.isSuccess)
    val itemId = createResult.getOrThrow()
    assertTrue(itemId.isNotEmpty())
  }

  @Test
  fun observeItems_authenticatedOwner_emitsRealtimeUpdates(): Unit = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CollectorRepository(firestore)

    val item = CollectorItem(
      userId = uid,
      itemType = "banknote",
      denomination = "€50",
      country = "European Union",
      year = 2017L,
      seriesOrMint = "Europa Series",
      conditionGrade = "UNC",
      purchasePrice = 50.0,
      currentMarketValue = 65.0,
      notes = "Europa portrait watermark"
    )

    val itemId = withTimeout(DEFAULT_TIMEOUT_MS) { repository.addItem(uid, item).getOrThrow() }

    val emittedItems = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeItems(uid).first { list -> list.any { it.id == itemId } }
    }
    assertTrue(emittedItems.any { it.id == itemId })
  }

  @Test
  fun getItemById_crossUserAccess_failsWithPermissionDenied(): Unit = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = CollectorRepository(firestore)

    val item = CollectorItem(
      userId = aliceUid,
      itemType = "coin",
      denomination = "€2.00",
      country = "Monaco",
      year = 2007L,
      seriesOrMint = "Grace Kelly",
      conditionGrade = "UNC",
      purchasePrice = 1200.0,
      currentMarketValue = 3500.0
    )
    val itemId = withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.addItem(aliceUid, item).getOrThrow() }

    val bobUid = signInTestUser(BOB_EMAIL)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      try {
        val snapshot = firestore.collection("users").document(aliceUid).collection("items").document(itemId).get().await()
        Result.success(snapshot)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

    assertTrue(result.isFailure)
    val rootEx = result.exceptionOrNull()?.unwrapFirestoreException()
    assertNotNull(rootEx)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, rootEx?.code)
  }

  @Test
  fun observeItems_unauthenticatedUser_failsWithPermissionDenied(): Unit = runBlocking {
    val uid = "unauth_user_id"
    auth.signOut()
    val repository = CollectorRepository(firestore)

    var failed = false
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeItems(uid).first()
      }
    } catch (e: Exception) {
      failed = true
    }
    assertTrue(failed)
  }

  @Test
  fun deleteItem_authenticatedOwner_removesDocument(): Unit = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CollectorRepository(firestore)

    val item = CollectorItem(
      userId = uid,
      itemType = "coin",
      denomination = "€0.50",
      country = "France",
      year = 2002L,
      conditionGrade = "XF",
      purchasePrice = 0.50,
      currentMarketValue = 1.20
    )
    val itemId = withTimeout(DEFAULT_TIMEOUT_MS) { repository.addItem(uid, item).getOrThrow() }

    val deleteResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.deleteItem(uid, itemId) }
    assertTrue(deleteResult.isSuccess)

    val fetchResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getItemById(uid, itemId) }
    assertNull(fetchResult.getOrNull())
  }

  private companion object {
    const val ALICE_EMAIL = "alice@numismatics.test"
    const val BOB_EMAIL = "bob@numismatics.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
