package no.nav.syfo

import io.prometheus.client.hotspot.DefaultExports
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory

data class ApplicationState(var ready: Boolean = false, var running: Boolean = true)

val log = LoggerFactory.getLogger("no.nav.syfo.emottakmock")!!

fun main(args: Array<String>) = runBlocking(Executors.newFixedThreadPool(2).asCoroutineDispatcher()) {
    val env = Environment()
    val applicationState = ApplicationState()

    DefaultExports.initialize()
    ResourceConfig()
    val applicationServer = Server(env.applicationPort).apply {
        handler = ServletContextHandler(ServletContextHandler.NO_SECURITY).apply {
            contextPath = "/"
            addServlet(ServletHolder(ServletContainer(ResourceConfig())), "/*")
        }
    }

    applicationServer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        // Kubernetes polls every 5 seconds for liveness, mark as not ready and wait 5 seconds for a new readyness check
        applicationState.ready = false
        Thread.sleep(10000)
        applicationServer.stop()
        applicationServer.destroy()
    })
}
