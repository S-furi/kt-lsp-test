package server

import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.TextDocumentSyncOptions
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import java.net.ServerSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class SimpleLanguageServer: LanguageServer, LanguageClientAware {

    private lateinit var client: LanguageClient
    private val textDocumentService: MyTextDocumentService = MyTextDocumentService()

    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult?>? {
        val capabilities = ServerCapabilities()
        val syncOptions = TextDocumentSyncOptions()
        syncOptions.change = TextDocumentSyncKind.Full
        syncOptions.openClose = true
        capabilities.setTextDocumentSync(syncOptions)
        return CompletableFuture.completedFuture(InitializeResult(capabilities))
    }

    override fun shutdown(): CompletableFuture<in Any>? {
        return CompletableFuture.completedFuture(null)
    }

    override fun exit() {
        exitProcess(0)
    }

    override fun getTextDocumentService(): TextDocumentService? {
        return this.textDocumentService
    }

    override fun getWorkspaceService(): WorkspaceService? {
        return object: WorkspaceService { // no-op
            override fun didChangeConfiguration(params: DidChangeConfigurationParams?) { }
            override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) { }
        }
    }

    override fun connect(client: LanguageClient) {
        this.client = client
        this.textDocumentService.client = client
    }

    private class MyTextDocumentService: TextDocumentService {
        lateinit var client: LanguageClient

        override fun didOpen(params: DidOpenTextDocumentParams?) {
            val diagnostic = Diagnostic(
                Range(Position(0, 0), Position(0, 5)),
                "Dummy diagnostic",
                DiagnosticSeverity.Warning,
                "my-lsp"
            )

            client.publishDiagnostics(PublishDiagnosticsParams(
                params?.textDocument?.uri,
                listOf(diagnostic)
            ))
        }

        // We won't support these (as stated in capabilties)
        override fun didChange(params: DidChangeTextDocumentParams?) { }
        override fun didClose(params: DidCloseTextDocumentParams?) { }
        override fun didSave(params: DidSaveTextDocumentParams?) { }
    }
}

class SimpleLSPLauncher(val ls: SimpleLanguageServer, port: Int = 9081) {
    private val stopFlag = AtomicBoolean(false)
    private val sSocket = ServerSocket(port)

    fun start() {
        Thread {
            while (!stopFlag.get()) {
                val cSocket = sSocket.accept()
                val launcher = LSPLauncher.createServerLauncher(this.ls, cSocket.inputStream, cSocket.outputStream)
                    ?: throw RuntimeException("Cannot create server launcher..")
                this.ls.connect(launcher.remoteProxy)
                launcher.startListening()
            }
            this.sSocket.close()
        }.start()
    }

    fun stop() {
        this.stopFlag.set(true)
    }
}