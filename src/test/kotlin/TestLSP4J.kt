import LSP4JUtils.emptyInitialise
import org.junit.jupiter.api.Test
import java.net.Socket
import kotlin.test.assertTrue

class TestLSP4J {

    @Test
    fun testLSP4J() {
        Socket("127.0.0.1", 9999).use { socket ->
            val server = LSP4JUtils.getRemoteLSService(socket)
            server.emptyInitialise {
                println("Server initialized, got response:\n$it")
                // random check if completion provider is defined (kotlin-lsp does)
                assertTrue { it.capabilities.completionProvider != null }
            }
            Thread.sleep(1000)
        }
    }
}