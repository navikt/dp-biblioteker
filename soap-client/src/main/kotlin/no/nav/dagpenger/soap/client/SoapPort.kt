package no.nav.dagpenger.soap.client

import de.huxhorn.sulky.ulid.ULID
import no.nav.cxf.metrics.MetricFeature
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.ws.addressing.WSAddressingFeature
import org.apache.cxf.ws.security.trust.STSClient
import javax.jws.WebService
import javax.xml.namespace.QName

private val ulid = ULID()

class Config {
    lateinit var sts: STSClient
    lateinit var endpoint: String
    lateinit var wsdl: String
    var stsAllowInsecure: Boolean = false
    var callIdGenerator: () -> String = { ulid.nextULID() }
    var portName: String? = null
    var svcName: String? = null
}

inline fun <reified T> createSoapClient(block: Config.() -> Unit): T =
    with(Config().apply(block)) {
        val annotations = T::class.java.getAnnotation(WebService::class.java)
        val namespace = annotations.targetNamespace + "/Binding"
        val svcName = svcName ?: annotations.name
        val portName = (portName ?: svcName) + "Port"

        JaxWsProxyFactoryBean()
            .apply {
                address = endpoint
                wsdlURL = wsdl
                serviceName = QName(namespace, svcName)
                endpointName = QName(namespace, portName)
                serviceClass = T::class.java
                features = listOf(WSAddressingFeature(), MetricFeature())
                outInterceptors.add(CallIdInterceptor(callIdGenerator))
            }.create(T::class.java)
            .also {
                if (stsAllowInsecure) {
                    sts.configureFor(it, STS_SAML_POLICY_NO_TRANSPORT_BINDING)
                } else {
                    sts.configureFor(it)
                }
            }
    }
