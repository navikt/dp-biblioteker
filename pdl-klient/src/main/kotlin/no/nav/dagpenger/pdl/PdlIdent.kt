package no.nav.dagpenger.pdl


enum class PdlIdentGruppe {
    AKTORID,
    FOLKEREGISTERIDENT,
    NPID,
}


data class PdlIdent constructor(val ident: String, val gruppe: PdlIdentGruppe, val historisk: Boolean) {

    constructor(ident: String, gruppe: String, historisk: Boolean) : this(ident,
        PdlIdentGruppe.valueOf(gruppe),
        historisk)
}
