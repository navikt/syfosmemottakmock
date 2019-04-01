package no.nav.syfo

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import no.nav.syfo.api.NaisRest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerCollection
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

val log = LoggerFactory.getLogger("no.nav.syfo.emottakmock")!!

fun main(args: Array<String>) = runBlocking(Executors.newFixedThreadPool(2).asCoroutineDispatcher()) {
    val env = Environment()
    val applicationState = ApplicationState()

    val applicationServer = Server(env.applicationPort).apply {
        handler = HandlerCollection().apply {
            handlers = arrayOf(NaisRest()) // TODO add non-spring cxf ws handler here
        }
    }

    applicationServer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.initialized = false
        applicationServer.stop()
        applicationServer.destroy()
        Thread.sleep(10000)
    })
}
