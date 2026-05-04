package no.nav.dagpenger.texas

import tools.jackson.core.JsonParser
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer

object IntrospectResponseDeserializer : ValueDeserializer<IntrospectResponse>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): IntrospectResponse {
        return kotlin.runCatching {
            val map = p.readValueAs(object : TypeReference<Map<String, Any>>() {})
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
