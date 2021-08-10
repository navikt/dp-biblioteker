package no.nav.dagpenger.client.pdl;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static no.nav.dagpenger.pdl.HttpClientKt.createAccessTokenFun;
import static no.nav.dagpenger.pdl.HttpClientKt.createRequestBuilder;
import no.nav.dagpenger.pdl.PersonOppslag;
import static no.nav.dagpenger.pdl.PersonOppslagKt.createPersonOppslag;
import no.nav.pdl.hentperson.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JavaClientTest {

    @Test
    @Disabled
    public void test() {
        final Map<String, String> env = Stream.of(new String[][]{
            {"AZURE_APP_CLIENT_ID", "b2d1d0e4-e197-477b-bc9e-b50b148c4cb9"},
            {"AZURE_APP_CLIENT_SECRET", "hubba"},
            {"AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", "https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));


        final PersonOppslag personOppslagClient = createPersonOppslag(
            "https://pdl-api.dev.intern.nav.no/graphql",
            createRequestBuilder(createAccessTokenFun("api://dev-fss.pdl.pdl-api/.default", env)));

//        Person person = personOppslagClient.hentPerson("14108009242");
        Person person = personOppslagClient.hentPerson("01038401226");
        System.out.print(person);
    }
}
