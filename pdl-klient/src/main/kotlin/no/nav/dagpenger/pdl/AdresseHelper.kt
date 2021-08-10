@file:Suppress("unused")

package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.DateMapper.localDateOrMax
import no.nav.dagpenger.pdl.DateMapper.localDateOrMin
import no.nav.pdl.hentperson.Bostedsadresse
import no.nav.pdl.hentperson.Kontaktadresse
import no.nav.pdl.hentperson.Oppholdsadresse
import no.nav.pdl.hentperson.Person
import no.nav.pdl.scalars.Date
import no.nav.pdl.scalars.DateTime
import java.time.LocalDate
import java.time.LocalDate.MAX
import java.time.LocalDate.MIN
import java.time.LocalDate.now

internal const val FREG = "FREG"
internal const val PDL = "PDL"

internal object OppholdsAdresseMapper {
    val Person.oppHoldsAdresseRegistrertDato: LocalDate
        get() = this.oppholdsadresse.mapNotNull { it.gyldigFraOgMed?.value?.toLocalDate() }.maxOrNull()
            ?: MIN

    val Person.harGyldigOppholdsAdresseFraPDL: Boolean
        get() = this.oppholdsadresse.harGyldigAdresseFra(PDL)

    val Person.harGyldigOppholdsAdresseFraFREG: Boolean
        get() = this.oppholdsadresse.harGyldigAdresseFra(FREG)

    private fun List<Oppholdsadresse>.harGyldigAdresseFra(master: String): Boolean =
        this.any {
            master == it.metadata.master && (now() in it.gyldigFraOgMed.localDateOrMin..it.gyldigTilOgMed.localDateOrMax)
        }
}

internal object DateMapper {
    val DateTime?.localDateOrMin: LocalDate
        get() = localDateOr(MIN)
    val DateTime?.localDateOrMax: LocalDate
        get() = localDateOr(MAX)
    val Date?.localDateOrMin: LocalDate
        get() = this?.value ?: MIN

    private fun DateTime?.localDateOr(default: LocalDate): LocalDate =
        this?.value?.toLocalDate() ?: default
}

internal object BostedsAdresseMapper {
    val Person.bostedAdresseRegistrertDato: LocalDate
        get() = this.bostedsadresse.flatMap { adresse ->
            listOfNotNull(
                adresse.angittFlyttedato.localDateOrMin,
                adresse.gyldigFraOgMed.localDateOrMin
            )
        }.maxOrNull() ?: MIN

    val Person.harGyldigNorskBostedAdresse: Boolean
        get() = this.bostedsadresse.harGyldigBostedAdresseFra(FREG)

    private fun List<Bostedsadresse>.harGyldigBostedAdresseFra(master: String): Boolean =
        this.any {
            master == it.metadata.master && (now() in it.gyldigFraOgMed.localDateOrMin..it.gyldigTilOgMed.localDateOrMax)
        }
}

internal object KontaktAdresseMapper {
    val Person.harGyldigKontaktAdresseFraPDL: Boolean
        get() = this.kontaktadresse.hargyldigKontaktAddresseFra(PDL)
    val Person.harGyldigKontaktAdresseFraFREG: Boolean
        get() = this.kontaktadresse.hargyldigKontaktAddresseFra(FREG)
    val Person.kontaktAdresseRegistrertDato: LocalDate
        get() = this.kontaktadresse.map { it.gyldigFraOgMed.localDateOrMin }.maxOrNull()
            ?: MIN

    private fun List<Kontaktadresse>.hargyldigKontaktAddresseFra(master: String): Boolean =
        this.any {
            master == it.metadata.master && (now() in it.gyldigFraOgMed.localDateOrMin..it.gyldigTilOgMed.localDateOrMax)
        }
}
