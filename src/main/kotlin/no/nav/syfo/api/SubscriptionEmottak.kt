package no.nav.syfo.api

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import javax.servlet.http.HttpServletRequest

class SubscriptionEmottak : AbstractHandler() {

    // TODO create ws response

    @Throws(IOException::class)
    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        if ("/nav-emottak-eletter-web/services/" == target) {
            startSubscription(response)
            baseRequest.setHandled(true)
        }
    }

    @Throws(IOException::class)
    private fun startSubscription(response: HttpServletResponse) {
        response.contentType = "text/html; charset=utf-8"
        response.status = HttpServletResponse.SC_OK

        val out = response.writer
        out.println("I'm updating emottak")
    }
}