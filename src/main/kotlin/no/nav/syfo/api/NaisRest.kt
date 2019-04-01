package no.nav.syfo.api

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import javax.servlet.http.HttpServletRequest

class NaisRest : AbstractHandler() {

    @Throws(IOException::class)
    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        if ("/is_alive" == target) {
            handleIsAlive(response)
            baseRequest.setHandled(true)
        }

        if ("/is_ready" == target) {
            handleIsReady(response)
            baseRequest.setHandled(true)
        }
    }

    @Throws(IOException::class)
    private fun handleIsAlive(response: HttpServletResponse) {
        response.contentType = "text/html; charset=utf-8"
        response.status = HttpServletResponse.SC_OK

        val out = response.writer
        out.println("I'm alive!")
    }

    @Throws(IOException::class)
    private fun handleIsReady(response: HttpServletResponse) {
        response.contentType = "text/html; charset=utf-8"
        response.status = HttpServletResponse.SC_OK

        val out = response.writer
        out.println("I'm ready!")
    }
}