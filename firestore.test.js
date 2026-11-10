const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function getValidItemPayload(userId) {
  return {
    userId,
    itemType: "coin",
    denomination: "€2.00",
    country: "Germany",
    year: 2008,
    seriesOrMint: "Mint A - Berlin",
    conditionGrade: "UNC",
    purchasePrice: 4.50,
    currentMarketValue: 8.00,
    notes: "Stuttgart Old Castle commemorative",
    highResPhotoUri: "content://media/euro_scan_1.jpg",
    hasCertification: false,
    isFavorite: true,
    createdAt: new Date(),
  };
}

test("Unauthenticated user: cannot read items", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("items").get());
});

test("Cross-user isolation: Bob cannot read Alice's items", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("items").doc("item_1").set(
      getValidItemPayload(ALICE_UID)
    );
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").get());
});

test("Creation: Alice can create a valid collector item", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_alice_1").set(
      getValidItemPayload(ALICE_UID)
    )
  );
});

test("Creation failure: Alice cannot create an item in Bob's collection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("items").doc("item_bob_1").set(
      getValidItemPayload(BOB_UID)
    )
  );
});

test("Creation failure: Alice cannot spoof userId in payload", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_alice_2").set(
      getValidItemPayload(BOB_UID)
    )
  );
});

test("Read: Alice can read and query her own items", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("items").doc("item_1").set(
      getValidItemPayload(ALICE_UID)
    );
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").get()
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").get()
  );
});

test("Update: Alice can update valid fields in her own item", async () => {
  const originalDate = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("items").doc("item_1").set({
      ...getValidItemPayload(ALICE_UID),
      createdAt: originalDate,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").update({
      currentMarketValue: 12.50,
      conditionGrade: "AU",
      notes: "Updated market value after auction",
      updatedAt: new Date(),
    })
  );
});

test("Update failure: Alice cannot alter immutable createdAt timestamp", async () => {
  const originalDate = new Date(Date.now() - 100000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("items").doc("item_1").set({
      ...getValidItemPayload(ALICE_UID),
      createdAt: originalDate,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").update({
      createdAt: new Date(),
    })
  );
});

test("Deletion: Alice can delete her own item, Bob cannot", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("items").doc("item_1").set(
      getValidItemPayload(ALICE_UID)
    );
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").delete()
  );

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("items").doc("item_1").delete()
  );
});
