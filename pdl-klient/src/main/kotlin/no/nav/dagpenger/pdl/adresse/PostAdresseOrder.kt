package no.nav.dagpenger.pdl.adresse

import no.nav.dagpenger.pdl.adresse.AdresseMetadata.MasterType

object PostAdresseOrder {
    private val ADRESSETYPE_RANKING =
        mapOf(
            AdresseMetadata.AdresseType.KONTAKTADRESSE to 0,
            AdresseMetadata.AdresseType.OPPHOLDSADRESSE to 1,
            AdresseMetadata.AdresseType.BOSTEDSADRESSE to 2,
        )
    private val MASTERTYPE_RANKING =
        mapOf(
            MasterType.PDL to 0,
            MasterType.FREG to 1,
        )
    private val byAdressetypeMasterRegistrertdato =
        Comparator<AdresseMetadata>(
            Comparator
                .comparingInt { metadata: AdresseMetadata ->
                    ADRESSETYPE_RANKING[metadata.adresseType]
                        ?: throw IllegalStateException("Unexpected value: " + metadata.adresseType)
                }
                .thenComparingInt { metadata: AdresseMetadata ->
                    MASTERTYPE_RANKING[metadata.master]
                        ?: throw IllegalStateException("Unexpected value: " + metadata.master)
                }
                .thenComparing(AdresseMetadata::registreringsDato, Comparator.reverseOrder())::compare,
        )

    val comparator: java.util.Comparator<PDLAdresse> =
        Comparator.comparing({ it.adresseMetadata }, byAdressetypeMasterRegistrertdato)

    fun List<PDLAdresse>.postAdresser(): List<PDLAdresse> = this.sortedWith(comparator)
}
