package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.AdresseMetadata.MasterType
import no.nav.sbl.dialogarena.soknadinnsending.consumer.person.pdl.dto.PDLAdresse

object PDLAdresseOrderStrategy {
    private val ADRESSETYPE_RANKING = mapOf(
        AdresseMetadata.AdresseType.KONTAKTADRESSE to 0,
        AdresseMetadata.AdresseType.OPPHOLDSADRESSE to 1,
        AdresseMetadata.AdresseType.BOSTEDSADRESSE to 2
    )
    private val MASTERTYPE_RANKING = mapOf(
        MasterType.PDL to 0,
        MasterType.FREG to 1
    )

    fun List<PDLAdresse>.rank(): List<PDLAdresse> {
        return this
            .sortedWith(Comparator.comparing({ it.adresseMetadata }, byAdressetypeMasterRegistrertdato))
    }

    private val byAdressetypeMasterRegistrertdato = Comparator<AdresseMetadata>(
        Comparator
            .comparingInt { metadata: AdresseMetadata ->
                ADRESSETYPE_RANKING[metadata.adresseType]
                    ?: throw IllegalStateException("Unexpected value: " + metadata.adresseType)
            }
            .thenComparingInt { metadata: AdresseMetadata ->
                MASTERTYPE_RANKING[metadata.master]
                    ?: throw IllegalStateException("Unexpected value: " + metadata.master)
            }
            .thenComparing(AdresseMetadata::registreringsDato, Comparator.reverseOrder())::compare
    )
}
