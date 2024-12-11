package no.nav.dagpenger.pdl

import no.nav.dagpenger.pdl.entity.IdentInformasjon
import no.nav.dagpenger.pdl.entity.Identliste

class PDLIdentliste(identliste: Identliste) {
    class PDLException(msg: String?) : RuntimeException(msg)

    val identer: List<PDLIdent> = identliste.identer.map { ident -> PDLIdent(ident) }
}

class PDLIdent(identInformasjon: IdentInformasjon) {
    enum class PDLIdentGruppe {
        AKTORID,
        FOLKEREGISTERIDENT,
        NPID,
    }

    val ident: String = identInformasjon.ident
    val gruppe: PDLIdentGruppe = PDLIdentGruppe.valueOf(identInformasjon.gruppe.toString())
    val historisk: Boolean = identInformasjon.historisk
}
