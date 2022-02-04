package no.nav.dagpenger.client.pdl;

import io.ktor.http.HttpHeaders;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.nav.dagpenger.oauth2.CachedOauth2Client;
import static no.nav.dagpenger.oauth2.HttpClientKt.defaultHttpClient;
import no.nav.dagpenger.oauth2.LoadingCacheBuilder;
import no.nav.dagpenger.oauth2.OAuth2Config.AzureAd;
import no.nav.dagpenger.pdl.PDLPerson;
import no.nav.dagpenger.pdl.PersonOppslagBolk;
import static no.nav.dagpenger.pdl.PersonOppslagKt.createPersonOppslagBolk;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JavaClientTest {

    @Test
    @Disabled
    public void test() {
        final Map<String, String> env = Stream.of(new String[][]{
            {"AZURE_APP_CLIENT_ID", ""},
            {"AZURE_APP_CLIENT_SECRET", ""},
            {"AZURE_OPENID_CONFIG_TOKEN_ENDPOINT", ""}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        AzureAd oAuth2Config = new AzureAd(env);
        CachedOauth2Client oauth2Client = new CachedOauth2Client(oAuth2Config.getTokenEndpointUrl(), oAuth2Config.clientSecret(), defaultHttpClient(), new LoadingCacheBuilder());

        PersonOppslagBolk personOppslagBolk = createPersonOppslagBolk("https://pdl-api.dev.intern.nav.no/graphql");
        List<PDLPerson> pdlPeople = personOppslagBolk.hentPersonerBlocking(Collections.singletonList("01038401226"),
            Collections.singletonMap(HttpHeaders.INSTANCE.getAuthorization(), "Bearer " + oauth2Client.clientCredentials("api://dev-fss.pdl.pdl-api/.default").getAccessToken()));

        System.out.println(pdlPeople);


    }
}
