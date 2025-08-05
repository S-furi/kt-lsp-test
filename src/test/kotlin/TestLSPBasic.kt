@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.net.Socket
import kotlinx.serialization.json.Json
import kotlin.collections.emptyMap
import kotlin.test.assertTrue

class TestLSPBasic {

    @Serializable
    data class InitializeMessage(
        @EncodeDefault val jsonrpc: String = "2.0",
        @EncodeDefault val id: Int = 1,
        @EncodeDefault val method: String = "initialize",
        @EncodeDefault val params: Map<String, Map<String, String?>?> = mapOf(
            "processId" to null,
            "workspaceFolders" to null,
            "capabilities" to emptyMap()
        )
    )

    fun buildJSONRPCMessage(msg: InitializeMessage): ByteArray {
        val body = Json.encodeToString(msg);
        return "Content-Length: ${body.length}\r\n\r\n$body".toByteArray(Charsets.UTF_8)
    }

    @Test
    fun testInitialisation() {
        val initializeMessage = buildJSONRPCMessage(InitializeMessage())
        Socket("127.0.0.1", 9999).use {client ->
            client.outputStream.write(initializeMessage)

            val buffer = ByteArray(4096)
            val bytesRead = client.getInputStream().read(buffer)
            val response = String(buffer, 0, bytesRead, Charsets.UTF_8)

            assertTrue { response.isNotEmpty() }
            println(response)
        }
    }
}


