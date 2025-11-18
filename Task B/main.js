// Simple Hedera HCS Messaging Demo (Custom Messages + Verification)

const {
  Client,
  PrivateKey,
  TopicCreateTransaction,
  TopicMessageSubmitTransaction,
  TopicMessageQuery
} = require("@hashgraph/sdk");

const crypto = require("crypto");
const readline = require("readline-sync");
require("dotenv").config();

// -----------------------------
// AES-256 Encryption
// -----------------------------
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
    return Buffer.concat([decipher.update(encrypted), decipher.final()]).toString();
  } catch {
    return data;
  }
}

async function main() {
  // Connect to Hedera Testnet
  const client = Client.forTestnet();
  client.setOperator(process.env.OPERATOR_ID, PrivateKey.fromString(process.env.OPERATOR_KEY));

  console.log("Connected to Hedera as:", process.env.OPERATOR_ID);

  // Create Topic
  const tx = await new TopicCreateTransaction().execute(client);
  const receipt = await tx.getReceipt(client);
  const topicId = receipt.topicId;
  console.log("\n📌 Topic Created:", topicId.toString());

  // Ask user for custom messages
  console.log("\nType messages to send. Type 'exit' to stop.\n");

  while (true) {
    const msg = readline.question("Your message: ");

    if (msg.toLowerCase() === "exit") break;

    const encrypted = encrypt(msg);

    await new TopicMessageSubmitTransaction({
      topicId,
      message: encrypted
    }).execute(client);

    console.log(`   📤 Sent encrypted: "${msg}"`);
  }

  // Wait for consensus in mirror node
  console.log("\n⏳ Waiting for consensus...");
  await new Promise(r => setTimeout(r, 4000));

  // Retrieve messages
  console.log("\n📥 Retrieving + decrypting messages:");
  console.log("-----------------------------------------\n");

  await new Promise(resolve => {
    new TopicMessageQuery()
      .setTopicId(topicId)
      .setStartTime(0)
      .subscribe(client, null, (msg) => {
        const raw = msg.contents.toString();
        const decrypted = decrypt(raw);

        console.log(`Message #${msg.sequenceNumber}`);
        console.log(`   Content: "${decrypted}"`);
        console.log(`   Raw Encrypted: ${raw}`);
        console.log(`   Timestamp: ${msg.consensusTimestamp.toDate().toISOString()}`);
        console.log("-----------------------------------------");
      });

    setTimeout(resolve, 6000);
  });

  console.log("\n✔️ Done.");
}

main();
