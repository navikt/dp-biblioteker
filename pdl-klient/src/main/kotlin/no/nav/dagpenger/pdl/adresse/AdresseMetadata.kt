package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.dto.KontaktadresseType
import no.nav.dagpenger.pdl.entity.Bostedsadresse
import no.nav.dagpenger.pdl.entity.Kontaktadresse
import no.nav.dagpenger.pdl.entity.Oppholdsadresse
import java.time.LocalDate

class AdresseMetadata(
    val adresseType: AdresseType,
    val type: String? = null,
    gyldigFom: LocalDate? = null,
    gyldigTom: LocalDate? = null,
    angittFlytteDato: LocalDate? = null,
    val master: MasterType,
    val coAdresseNavn: String? = null
) {
    enum class AdresseType {
        BOSTEDSADRESSE, KONTAKTADRESSE, OPPHOLDSADRESSE
    }

    enum class MasterType {
        FREG, PDL
    }

    val gyldighetsPeriode: ClosedRange<LocalDate> by lazy {
        val lower: LocalDate = maxOf(
            gyldigFom ?: LocalDate.MIN,
            angittFlytteDato ?: LocalDate.MIN
        )
        val upper = gyldigTom ?: LocalDate.MAX
        lower..upper
    }

    val registreringsDato: LocalDate = gyldighetsPeriode.start

    val erNorskBostedsAdresse = adresseType == AdresseType.BOSTEDSADRESSE && master == MasterType.FREG

    val erGyldig: Boolean = LocalDate.now() in gyldighetsPeriode

    companion object {
        fun from(bostedsadresse: Bostedsadresse): AdresseMetadata {
            return AdresseMetadata(
                AdresseType.BOSTEDSADRESSE,
                KontaktadresseType.Innland.name,
                bostedsadresse.gyldigFraOgMed?.toLocalDate(),
                bostedsadresse.gyldigTilOgMed?.toLocalDate(),
                bostedsadresse.angittFlyttedato,
                MasterType.valueOf(bostedsadresse.metadata.master.uppercase()),
                bostedsadresse.coAdressenavn
            )
        }

        fun from(kontaktadresse: Kontaktadresse): AdresseMetadata {
            return AdresseMetadata(
                AdresseType.KONTAKTADRESSE,
                kontaktadresse.type.name,
                kontaktadresse.gyldigFraOgMed?.toLocalDate(),
                kontaktadresse.gyldigTilOgMed?.toLocalDate(),
                null,
                MasterType.valueOf(kontaktadresse.metadata.master.uppercase()),
                coAdresseNavn = kontaktadresse.coAdressenavn
            )
        }

        fun from(oppholdsadresse: Oppholdsadresse): AdresseMetadata {
            return AdresseMetadata(
                AdresseType.OPPHOLDSADRESSE,
                null,
                oppholdsadresse.gyldigFraOgMed?.toLocalDate(),
                oppholdsadresse.gyldigTilOgMed?.toLocalDate(),
                null,
                MasterType.valueOf(oppholdsadresse.metadata.master.uppercase()),
                coAdresseNavn = oppholdsadresse.coAdressenavn
            )
        }
    }

    override fun toString(): String {
        return buildString {
            append("AdresseMetadata(adresseType=")
            append(adresseType)
            append(", type=")
            append(type)
            append(", master=")
            append(master)
            append(", gyldighetsPeriode=")
            append(gyldighetsPeriode)
            append(", registreringsDato=")
            append(registreringsDato)
            append(", erNorskBostedsAdresse=")
            append(erNorskBostedsAdresse)
            append(", erGyldig=")
            append(erGyldig)
            append(", coAdresseNavn=")
            append(coAdresseNavn)
            append(")")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdresseMetadata

        if (adresseType != other.adresseType) return false
        if (type != other.type) return false
        if (master != other.master) return false
        if (gyldighetsPeriode != other.gyldighetsPeriode) return false
        if (registreringsDato != other.registreringsDato) return false
        if (erNorskBostedsAdresse != other.erNorskBostedsAdresse) return false
        if (erGyldig != other.erGyldig) return false
        if (coAdresseNavn != other.coAdresseNavn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = adresseType.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + master.hashCode()
        result = 31 * result + gyldighetsPeriode.hashCode()
        result = 31 * result + registreringsDato.hashCode()
        result = 31 * result + erNorskBostedsAdresse.hashCode()
        result = 31 * result + erGyldig.hashCode()
        result = 31 * result + (coAdresseNavn?.hashCode() ?: 0)
        return result
    }
}
