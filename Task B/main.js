// Simple Hedera HCS Messaging Demo (Encrypted)
// --------------------------------------------

const {
  Client,
  PrivateKey,
  AccountId,
  TopicCreateTransaction,
  TopicMessageSubmitTransaction,
  TopicMessageQuery
} = require("@hashgraph/sdk");
const crypto = require("crypto");
require("dotenv").config();

// AES-256 encryption helpers
function getKey() {
  return crypto.createHash("sha256")
               .update(process.env.ENCRYPTION_SECRET)
               .digest();
}

function encrypt(text) {
  const iv = crypto.randomBytes(16);
  const cipher = crypto.createCipheriv("aes-256-cbc", getKey(), iv);
  const encrypted = Buffer.concat([cipher.update(text), cipher.final()]);
  return iv.toString("hex") + ":" + encrypted.toString("hex");
}

function decrypt(data) {
  try {
    const [ivHex, encHex] = data.split(":");
    const iv = Buffer.from(ivHex, "hex");
    const encrypted = Buffer.from(encHex, "hex");

    const decipher = crypto.createDecipheriv("aes-256-cbc", getKey(), iv);
    const decrypted = Buffer.concat([decipher.update(encrypted), decipher.final()]);
    return decrypted.toString();
  } catch {
    return data; // Not encrypted or wrong format
  }
}

async function main() {
  // Connect to Hedera Testnet
  const client = Client.forTestnet()
    .setOperator(process.env.OPERATOR_ID, PrivateKey.fromString(process.env.OPERATOR_KEY));

  console.log("Connected as:", process.env.OPERATOR_ID);

  // 1️⃣ Create Topic
  const tx = await new TopicCreateTransaction().execute(client);
  const receipt = await tx.getReceipt(client);
  const topicId = receipt.topicId;

  console.log("\n📌 Topic Created:", topicId.toString());

  // 2️⃣ Send encrypted messages
  const messages = [
    "Hello, Hedera!",
    "Learning HCS",
    "Message 3"
  ];

  console.log("\n📤 Sending encrypted messages...");

  for (let msg of messages) {
    const encryptedMsg = encrypt(msg);
    await new TopicMessageSubmitTransaction({
      topicId,
      message: encryptedMsg
    }).execute(client);

    console.log(`   Sent: "${msg}" 🔐`);
  }

  // Wait a few seconds so mirror node catches up
  console.log("\n⏳ Waiting for consensus...");
  await new Promise(r => setTimeout(r, 5000));

  // 3️⃣ Retrieve + Decrypt messages
  console.log("\n📥 Retrieving + decrypting messages:\n");

  await new Promise((resolve) => {
    new TopicMessageQuery()
      .setTopicId(topicId)
      .setStartTime(0) // read all messages
      .subscribe(client, null, (msg) => {
        const raw = msg.contents.toString();
        const decrypted = decrypt(raw);
        const timestamp = msg.consensusTimestamp.toDate().toISOString();

        console.log(` • "${decrypted}" at ${timestamp}`);
      });

    setTimeout(resolve, 6000);
  });

  console.log("\n✔️ Finished.");
}

main();
