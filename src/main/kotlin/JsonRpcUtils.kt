import kotlinx.serialization.json.Json
import java.io.InputStream

object JsonRpcUtils {

    fun buildJSONRPCMessage(msg: Message): ByteArray {
        val body = Json.encodeToString(msg);
        return "Content-Length: ${body.length}\r\n\r\n$body".toByteArray(Charsets.UTF_8)
    }

    fun readRPCResponse(input: InputStream): String {
        val reader = input.bufferedReader(Charsets.UTF_8)
        val headers = mutableMapOf<String, String>()
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isEmpty()) break

            val (key, value) = line.trim().split(": ")
            headers[key] = value
        }

        val contentLength = headers["Content-Length"]?.toIntOrNull()
            ?: throw IllegalArgumentException("No Content-Length header found")

        val payloadBuffer = CharArray(contentLength)
        var totalRead = 0
        while (totalRead < contentLength) {
            val read = reader.read(payloadBuffer, totalRead, contentLength - totalRead)
            if (read == -1) break
            totalRead += read
        }

        return String(payloadBuffer, 0, totalRead)
    }
}