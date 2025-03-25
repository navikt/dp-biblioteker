package no.nav.dagpenger.texas

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object Config {
    val configuration: Configuration = ConfigurationProperties.systemProperties() overriding EnvironmentVariables()
    val tokenEndpoint = configuration.tokenEndpoint()
    val tokenExchangeEndpoint = configuration.tokenExchangeEndpoint()

    fun Configuration.tokenEndpoint() = this[Key("nais.token.endpoint", stringType)]

    fun Configuration.tokenExchangeEndpoint() = this[Key("nais.token.endpoint", stringType)]
}
