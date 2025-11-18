const { Client, AccountBalanceQuery, AccountId, PrivateKey } = require("@hashgraph/sdk");
require("dotenv").config();

async function testConnection() {
    console.log("🔍 Testing Hedera connection...\n");
    
    try {
        const client = Client.forTestnet();
        client.setOperator(
            AccountId.fromString(process.env.OPERATOR_ID),
            PrivateKey.fromString(process.env.OPERATOR_KEY)
        );
        
        // Increase timeout
        client.setRequestTimeout(60000);
        
        console.log("1️⃣ Checking account balance...");
        const balance = await new AccountBalanceQuery()
            .setAccountId(process.env.OPERATOR_ID)
            .execute(client);
        
        console.log(`✅ Balance: ${balance.hbars.toString()}`);
        
        if (balance.hbars.toBigNumber().isLessThan(1)) {
            console.log("⚠️  Low balance! Get more from: https://portal.hedera.com/faucet");
        }
        
        client.close();
        console.log("\n✅ Connection test successful!");
        
    } catch (error) {
        console.error("❌ Connection failed:", error.message);
    }
}

testConnection();