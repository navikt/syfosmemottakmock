package no.nav.syfo.api.soap

import org.apache.wss4j.dom.handler.RequestData
import org.apache.wss4j.dom.validate.Credential
import org.apache.wss4j.dom.validate.UsernameTokenValidator

class StsUntValidator : UsernameTokenValidator() {

    override fun validate(credential: Credential, data: RequestData): Credential {
        return credential
    }
}
