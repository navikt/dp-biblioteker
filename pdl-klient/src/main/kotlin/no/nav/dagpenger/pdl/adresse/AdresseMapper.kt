package no.nav.dagpenger.pdl.adresse

abstract class AdresseMapper<T> {
    fun formatertAdresse(pdlAdresse: PDLAdresse): T {
        return when (pdlAdresse) {
            is PDLAdresse.VegAdresse -> formatertAdresse(pdlAdresse)
            is PDLAdresse.MatrikkelAdresse -> formatertAdresse(pdlAdresse)
            is PDLAdresse.PostAdresseIFrittFormat -> formatertAdresse(pdlAdresse)
            is PDLAdresse.PostboksAdresse -> formatertAdresse(pdlAdresse)
            is PDLAdresse.UtenlandsAdresseIFrittFormat -> formatertAdresse(pdlAdresse)
            is PDLAdresse.UtenlandskAdresse -> formatertAdresse(pdlAdresse)
            is PDLAdresse.TomAdresse -> formatertAdresse(pdlAdresse)
        }
    }

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.TomAdresse): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.UtenlandskAdresse): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.UtenlandsAdresseIFrittFormat): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.PostboksAdresse): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.PostAdresseIFrittFormat): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.MatrikkelAdresse): T

    protected abstract fun formatertAdresse(pdlAdresse: PDLAdresse.VegAdresse): T
}
