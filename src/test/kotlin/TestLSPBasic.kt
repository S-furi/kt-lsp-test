@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Test
import java.net.Socket
import kotlin.test.assertTrue

class TestLSPBasic {

    @Test
    fun testInitialisation() {
        val initializeMessage = JsonRpcUtils.buildJSONRPCMessage(InitializeMessage())
        Socket("127.0.0.1", 9999).use {client ->
            client.outputStream.write(initializeMessage)
            val response = JsonRpcUtils.readRPCResponse(client.inputStream)
            assertTrue { response.isNotEmpty() }
            println(response)
        }
    }
}


