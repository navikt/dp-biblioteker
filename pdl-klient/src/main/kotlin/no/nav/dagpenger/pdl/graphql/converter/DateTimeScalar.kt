package no.nav.dagpenger.pdl.graphql.converter

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("unused")
class DateTimeScalar : ScalarConverter<LocalDateTime> {
    override fun toJson(value: LocalDateTime) = value.toString()
    override fun toScalar(rawValue: Any): LocalDateTime =
        LocalDateTime.parse(rawValue.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
