package no.nav.dagpenger.ktor.auth

import org.apache.commons.codec.binary.Hex
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ApiKeyVerifier(private val secret: String) {
    private val algorithm = "HmacSHA256"

    fun verify(
        apiKey: String,
        expectedApiKey: String,
    ): Boolean {
        return apiKey == generate(expectedApiKey)
    }

    fun generate(apiKey: String): String {
        return String(Hex.encodeHex(generateDigest(apiKey.toByteArray(StandardCharsets.UTF_8))))
    }

    private fun generateDigest(apiKey: ByteArray): ByteArray {
        val secret = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secret)
        return mac.doFinal(apiKey)
    }
}
