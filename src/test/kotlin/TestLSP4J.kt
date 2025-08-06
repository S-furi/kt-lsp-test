import org.eclipse.lsp4j.ClientCapabilities
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.MessageActionItem
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.ShowMessageRequestParams
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageClient
import org.junit.jupiter.api.Test
import java.net.Socket
import java.util.concurrent.CompletableFuture

class TestLSP4J {

    @Test
    fun testLSP4J() {
        val socket = Socket("127.0.0.1", 9999)
        val inStream = socket.inputStream
        val outStream = socket.outputStream

        val client = object: LanguageClient {
            override fun telemetryEvent(`object`: Any?) { }
            override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) { }
            override fun showMessage(messageParams: MessageParams?) { }
            override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem?>? = null
            override fun logMessage(message: MessageParams?) { message?.message?.apply(::println) }
        }

        val launcher = LSPLauncher.createClientLauncher(client, inStream, outStream)
        val server = launcher.remoteProxy ?: throw RuntimeException("Server is null")
        launcher.startListening()

        val initParams = InitializeParams()
        initParams.capabilities = ClientCapabilities()
        server.initialize(initParams)?.thenAccept { res ->
            println("Server initialized, got response:\n$res")
        } ?: RuntimeException("Something went wrong")
        Thread.sleep(5000)
    }
}