import LSP4JUtils.emptyInitialise
import org.junit.jupiter.api.Test
import java.net.Socket

class TestLSP4J {

    @Test
    fun testLSP4J() {
        Socket("127.0.0.1", 9999).use { socket ->
            val server = LSP4JUtils.getRemoteLSService(socket)
            server.emptyInitialise {
                println("Server initialized, got response:\n$it")
            }
            Thread.sleep(1000)
        }
    }
}