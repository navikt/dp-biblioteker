package no.nav.dagpenger.ktor.auth

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ApiKeyVerifierTest {
    @Test
    fun `Should be able to verify api key with same secret`() {
        val verifier = ApiKeyVerifier("secret")
        val enc = verifier.generate("apikey")
        assertTrue { verifier.verify(enc, "apikey") }
    }

    @Test
    fun `Should not be able to verify api if key has changed key with same secret`() {
        val verifier = ApiKeyVerifier("secret")
        val enc = verifier.generate("apikey")
        assertFalse { verifier.verify(enc, "Apikey") }
    }

    @Test
    fun `Should not be able to verify api if with secret changed`() {
        val generator = ApiKeyVerifier("another secret")
        val enc = generator.generate("apikey")
        val verifier = ApiKeyVerifier("secret")
        assertFalse { verifier.verify(enc, "apikey") }
    }
}
