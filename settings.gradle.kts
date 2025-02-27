rootProject.name = "dp-biblioteker"

include("soap-client")
include("ktor-client-auth-bearer")
include("ktor-client-metrics")
include("ktor-utils")
include("sts-klient")
include("pdl-klient-kobby")
include("pdl-klient")
include("image-utils")
include("oauth2-klient")


dependencyResolutionManagement {
    repositories {
        maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    versionCatalogs {
        create("libs") {
            from("no.nav.dagpenger:dp-version-catalog:20250227.136.d15eef")
        }
    }
}

