import org.eclipse.lsp4j.ClientCapabilities
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.MessageActionItem
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.ShowMessageRequestParams
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import java.lang.RuntimeException
import java.net.Socket
import java.util.concurrent.CompletableFuture

object LSP4JUtils {
    val minimalClient = object: LanguageClient {
        override fun telemetryEvent(o: Any?) { }
        override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) { }
        override fun showMessage(messageParams: MessageParams?) { }
        override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem?>? = null
        override fun logMessage(message: MessageParams?) { message?.message?.apply(::println) }
    }

    fun getRemoteLSService(socket: Socket): LanguageServer {
        val inStream = socket.inputStream
        val outStream = socket.outputStream
        val launcher = LSPLauncher.createClientLauncher(minimalClient, inStream, outStream)
        launcher.startListening()
        return launcher?.remoteProxy ?: throw RuntimeException("Cannot connect to server")
    }

    fun LanguageServer.emptyInitialise(onReception: (InitializeResult) -> Unit) =
        this.initialize(InitializeParams().also { it.capabilities = ClientCapabilities() })
            ?.thenAccept(onReception)
            ?: throw RuntimeException("Cannot send initialisation to server!")
}