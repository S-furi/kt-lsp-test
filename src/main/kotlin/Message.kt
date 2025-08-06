import kotlinx.serialization.Serializable

@Serializable
sealed interface Message {
    val jsonrpc: String
}