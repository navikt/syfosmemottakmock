package no.nav.syfo

import org.apache.cxf.jaxws.EndpointImpl
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import no.nav.syfo.api.rest.NaisRest
import no.nav.syfo.api.soap.StsUntValidator
import no.nav.syfo.api.soap.SubscriptionSoapImpl
import org.apache.cxf.BusFactory
import org.apache.cxf.transport.servlet.CXFNonSpringServlet
import org.apache.cxf.ws.security.SecurityConstants
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor
import org.apache.wss4j.dom.WSConstants
import org.apache.wss4j.dom.handler.WSHandlerConstants
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import javax.xml.ws.Endpoint
import kotlin.reflect.jvm.jvmName

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

val log = LoggerFactory.getLogger("no.nav.syfo.emottakmock")!!

fun main(args: Array<String>) = runBlocking(Executors.newFixedThreadPool(2).asCoroutineDispatcher()) {
    val env = Environment()
    val applicationState = ApplicationState()

    val subscriptionEmottak = SubscriptionSoapImpl()

    // Configure Jax WS
    val cxfServlet = CXFNonSpringServlet().apply {
        bus = BusFactory.getDefaultBus(true)
    }

    val applicationServer = Server(env.applicationPort).apply {
        handler = HandlerCollection().apply {
            handlers = arrayOf(
                ContextHandler("/internal").apply {
                    handler = NaisRest()
                },
                ServletContextHandler(ServletContextHandler.NO_SECURITY or ServletContextHandler.NO_SESSIONS).apply {
                    addServlet(ServletHolder(cxfServlet), "/nav-emottak-eletter-web/*")
                })
        }
    }

    applicationServer.start()

    Endpoint.publish("/services/", subscriptionEmottak).let {
        it as EndpointImpl
        it.server.endpoint.inInterceptors.add(
            WSS4JInInterceptor(mapOf(
            WSHandlerConstants.ACTION to WSHandlerConstants.USERNAME_TOKEN,
            WSHandlerConstants.PASSWORD_TYPE to WSConstants.PW_TEXT
        ))
        )
        it.properties = mapOf(SecurityConstants.USERNAME_TOKEN_VALIDATOR to StsUntValidator::class.jvmName)
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.initialized = false
        applicationServer.stop()
        applicationServer.destroy()
        Thread.sleep(10000)
    })
}
