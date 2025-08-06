@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
class InitializeMessage(
    @EncodeDefault val id: Int = 1,
    @EncodeDefault val params: InitParams = InitParams()
) : Message {
    @EncodeDefault override val jsonrpc: String  = "2.0"
    @EncodeDefault val method: String = "initialize"
}

@Serializable
data class InitParams(
    @EncodeDefault val processId: Int? = null,
    val clientInfo: ClientInfo? = null,
    val locale: String? = null,
    val initializationOptions: String? = null, // LSPAny, being any not serializable (at least with kotlinx.serialization), use it already json encoded
    @EncodeDefault val capabilities: ClientCapabilities = ClientCapabilities(),
    val workspaceFolders: List<WorkspaceFolder>? = null,
)

@Serializable
data class ClientInfo(val name: String, val version: String?)

@Serializable
class ClientCapabilities()

@Serializable
data class WorkspaceFolder(@Contextual val uri: URI, val name: String)