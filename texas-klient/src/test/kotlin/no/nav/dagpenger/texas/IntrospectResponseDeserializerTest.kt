package no.nav.dagpenger.texas

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import tools.jackson.databind.module.SimpleModule
import tools.jackson.module.kotlin.jacksonMapperBuilder

class IntrospectResponseDeserializerTest {
    private val mapper =
        jacksonMapperBuilder()
            .addModule(
                SimpleModule().also {
                    it.addDeserializer(IntrospectResponse::class.java, IntrospectResponseDeserializer)
                },
            ).build()

    @Test
    fun `deserialize valid response`() {
        //language=json
        val json =
            """
            {
                "active": true,
                "sub": "1234567890",
                "name": "John Doe",
                "int": 12223,
                "admin": true
            }
            """.trimIndent()

        mapper.readValue(json, IntrospectResponse::class.java) shouldBe
            IntrospectResponse.Valid(
                mapOf(
                    "sub" to "1234567890",
                    "int" to 12223,
                    "name" to "John Doe",
                    "admin" to true,
                ),
            )
    }

    @Test
    fun `deserialize invalid response`() {
        //language=json
        val json =
            """
            {
                "active": false,
                "error": "This is an error"
            }
            """.trimIndent()

        mapper.readValue(json, IntrospectResponse::class.java) shouldBe
            IntrospectResponse.Invalid(
                "This is an error",
            )
    }
}
