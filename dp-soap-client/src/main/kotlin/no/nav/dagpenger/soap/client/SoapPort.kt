package no.nav.dagpenger.soap.client

import javax.jws.WebService
import javax.xml.namespace.QName
import no.nav.cxf.metrics.MetricFeature
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.ws.addressing.WSAddressingFeature
import org.apache.cxf.ws.security.trust.STSClient

class Config {
    lateinit var sts: STSClient
    lateinit var endpoint: String
    lateinit var wsdl: String
    var stsAllowInsecure: Boolean = false
}

inline fun <reified T> createSoapClient(block: Config.() -> Unit): T {
    return with(Config().apply(block)) {
        val annotations = T::class.java.getAnnotation(WebService::class.java)
        val namespace = annotations.targetNamespace + "/Binding"
        val svcName = annotations.name
        val portName = svcName + "Port"

        JaxWsProxyFactoryBean().apply {
            address = endpoint
            wsdlURL = wsdl
            serviceName = QName(namespace, svcName)
            endpointName = QName(namespace, portName)
            serviceClass = T::class.java
            features = listOf(WSAddressingFeature(), MetricFeature())
            outInterceptors.add(CallIdInterceptor())
        }.create(T::class.java).also {
            if (stsAllowInsecure) {
                sts.configureFor(it, STS_SAML_POLICY_NO_TRANSPORT_BINDING)
            } else {
                sts.configureFor(it)
            }
        }
    }
}
