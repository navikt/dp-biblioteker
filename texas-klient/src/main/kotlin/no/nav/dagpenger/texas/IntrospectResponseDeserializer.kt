package no.nav.dagpenger.texas

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

object IntrospectResponseDeserializer : JsonDeserializer<IntrospectResponse>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): IntrospectResponse {
        return kotlin.runCatching {
            val map = p.codec.readValue(p, object : TypeReference<Map<String, Any>>() {})
            when (map["active"] as Boolean) {
                true -> {
                    IntrospectResponse.Valid(
                        map.filter {
                            it.key != "active"
                        },
                    )
                }

                false -> {
                    IntrospectResponse.Invalid(map["error"] as String)
                }
            }
        }.getOrElse { t ->
            throw ParseException("Failed to parse IntrospectResponse:", t)
        }
    }
}

class ParseException(message: String, cause: Throwable) : RuntimeException(message, cause)
