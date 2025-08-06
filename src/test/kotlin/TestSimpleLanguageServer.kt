import LSP4JUtils.emptyInitialise
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.junit.jupiter.api.Test
import server.SimpleLanguageServer
import server.SimpleLSPLauncher
import java.net.Socket

class TestSimpleLanguageServer {

    @Test
    fun testSimpleLS() {
        val port = 9081
        val tcpServer = SimpleLSPLauncher(SimpleLanguageServer(), port)
            .also { it.start() }

        Thread.sleep(2000)

        Socket("127.0.0.1", port).use { socket ->
            val ls = LSP4JUtils.getRemoteLSService(socket)
            ls.emptyInitialise {
                println("Server initialized, got response:\n$it")
                kotlin.test.assertTrue { it.capabilities.completionProvider == null }
                kotlin.test.assertTrue { it.capabilities.textDocumentSync.left == TextDocumentSyncKind.Full }
            }
            tcpServer.stop()
            Thread.sleep(1000)
        }
    }
}