package no.nav.dagpenger.pdl.graphql.converter

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("unused")
class DateScalar : ScalarConverter<LocalDate> {
    override fun toJson(value: LocalDate) = value.toString()
    override fun toScalar(rawValue: Any): LocalDate =
        LocalDate.parse(rawValue.toString(), DateTimeFormatter.ISO_LOCAL_DATE)
}
