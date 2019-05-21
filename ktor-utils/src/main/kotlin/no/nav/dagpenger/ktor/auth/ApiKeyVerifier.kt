package no.nav.dagpenger.ktor.auth

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ApiKeyVerifier(private val secret: ByteArray) {

    private val algorithm = "HmacSHA256"

    fun verify(apiKey: ByteArray, expectedApiKey: ByteArray): Boolean {

        val hmac = generate(apiKey)

        if (hmac.size != expectedApiKey.size) return false
        var result = 0
        for (i in 0 until hmac.size) {
            result = result.or(hmac[i].toInt().xor(expectedApiKey[i].toInt()))
        }

        return result == 0
    }

    fun generate(apiKey: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(secret, algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(keySpec)

        val hmac = mac.doFinal(apiKey)
        return hmac
    }
}