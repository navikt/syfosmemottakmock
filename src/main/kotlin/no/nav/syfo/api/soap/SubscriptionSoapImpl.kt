package no.nav.syfo.api.soap

import no.nav.emottak.subscription.FinnElektroniskSamhandlerstatusRequest
import no.nav.emottak.subscription.FinnElektroniskSamhandlerstatusResponse
import no.nav.emottak.subscription.StartSubscriptionRequest
import no.nav.emottak.subscription.StatusResponse
import no.nav.emottak.subscription.SubscriptionPort

class SubscriptionSoapImpl() : SubscriptionPort {
    override fun startSubscription(startSubscriptionRequest: StartSubscriptionRequest?): StatusResponse =
        StatusResponse().apply {
            key = startSubscriptionRequest!!.key
            status = 1
            description = "Added"
        }

    override fun finnElektroniskSamhandlerstatus(finnElektroniskSamhandlerstatusRequest: FinnElektroniskSamhandlerstatusRequest?): FinnElektroniskSamhandlerstatusResponse {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}