package no.nav.dagpenger.pdl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import no.nav.dagpenger.pdl.dto.IdentGruppe
import no.nav.dagpenger.pdl.entity.IdentInformasjon
import no.nav.dagpenger.pdl.entity.Identliste
import org.junit.jupiter.api.Test

class PDLIdentlisteTest {
    @Test
    fun `kan opprette PDLIdentliste`() {
        val identInformasjon1 = mockk<IdentInformasjon>()
        every { identInformasjon1.ident } returns "1"
        every { identInformasjon1.gruppe } returns IdentGruppe.AKTORID
        every { identInformasjon1.historisk } returns true

        val identInformasjon2 = mockk<IdentInformasjon>()
        every { identInformasjon2.ident } returns "2"
        every { identInformasjon2.gruppe } returns IdentGruppe.NPID
        every { identInformasjon2.historisk } returns true

        val identInformasjon3 = mockk<IdentInformasjon>()
        every { identInformasjon3.ident } returns "3"
        every { identInformasjon3.gruppe } returns IdentGruppe.FOLKEREGISTERIDENT
        every { identInformasjon3.historisk } returns false

        val identliste = mockk<Identliste>()
        every { identliste.identer } returns listOf(identInformasjon1, identInformasjon2, identInformasjon3)

        val pdlIdentliste = PDLIdentliste(identliste)

        pdlIdentliste.identer shouldNotBe null
        pdlIdentliste.identer.size shouldBe 3
        pdlIdentliste.identer[0].ident shouldBe "1"
        pdlIdentliste.identer[0].gruppe shouldBe PDLIdent.PDLIdentGruppe.AKTORID
        pdlIdentliste.identer[0].historisk shouldBe true
        pdlIdentliste.identer[1].ident shouldBe "2"
        pdlIdentliste.identer[1].gruppe shouldBe PDLIdent.PDLIdentGruppe.NPID
        pdlIdentliste.identer[1].historisk shouldBe true
        pdlIdentliste.identer[2].ident shouldBe "3"
        pdlIdentliste.identer[2].gruppe shouldBe PDLIdent.PDLIdentGruppe.FOLKEREGISTERIDENT
        pdlIdentliste.identer[2].historisk shouldBe false
    }
}
