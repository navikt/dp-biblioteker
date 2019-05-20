package no.nav.dagpenger.ktor.auth

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ApiKeyVerifierTest {

    @Test
    fun `Should be able to verify api key with same secret`() {
        val verifier = ApiKey("secret".toByteArray())
        val enc = verifier.generate("apikey".toByteArray())
        assertTrue { verifier.verify("apikey".toByteArray(), enc) }
    }

    @Test
    fun `Should not be able to verify api if key has changed key with same secret`() {
        val verifier = ApiKey("secret".toByteArray())
        val enc = verifier.generate("apikey".toByteArray())
        assertFalse { verifier.verify("Apikey".toByteArray(), enc) }
    }

    @Test
    fun `Should not be able to verify api if with secret changed`() {
        val generator = ApiKey("another secret".toByteArray())
        val enc = generator.generate("apikey".toByteArray())
        val verifier = ApiKey("secret".toByteArray())
        assertFalse { verifier.verify("apikey".toByteArray(), enc) }
    }
}