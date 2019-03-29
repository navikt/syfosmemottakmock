package no.nav.syfo.api

import no.nav.syfo.log
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.ServiceUnavailableException
import javax.ws.rs.core.Response

@Path("/")
class NaisRest(
    private val readynessCheck: () -> Boolean,
    private val livenessCheck: () -> Boolean = { true }
) {

    val isAlive: Response
        @GET
        @Path("is_alive")
        get() {
            if (livenessCheck()) {
                log.trace("isAlive called, returning not ok")
                throw ServiceUnavailableException()
            } else {

                log.trace("isAlive called, returning ok")
                return Response.noContent()
                    .build()
            }
        }

    val isReady: Response
        @GET
        @Path("is_ready")
        get() {
            if (readynessCheck()) {
                log.trace("isReady called, returning not ok")
                throw ServiceUnavailableException()
            } else {

                log.trace("isReady called, returning ok")
                return Response.noContent()
                    .build()
            }
        }
}