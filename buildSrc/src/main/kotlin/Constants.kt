/***
 *  Avhengigheter for Dapgenger jvm prosjekter.
 *
 *  Denne fila skal kun editeres i fra https://github.com/navikt/dagpenger monorepo. Sjekk inn ny versjon og kjør
 *  repo sync
 *
 */
object Assertj {
    const val version = "3.12.2"
    const val core = "org.assertj:assertj-core:$version"
    fun library(name: String) = "org.assertj:assertj-$name:$version"
}
object Avro {
    const val avro = "org.apache.avro:avro:1.8.2"
}

object Bekk {
    const val nocommons = "no.bekk.bekkopen:nocommons:0.8.2"
}

object Cucumber {
    const val version = "4.7.2"
    const val java8 = "io.cucumber:cucumber-java8:$version"
    const val junit = "io.cucumber:cucumber-junit:$version"
    fun library(name: String) = "io.cucumber:cucumber-$name:$version"
}

object Dagpenger {

    object Biblioteker {
        const val version = "2019.10.04-11.45.e5eff2e37bb7"
        const val stsKlient = "com.github.navikt.dp-biblioteker:sts-klient:$version"
        const val grunnbeløp = "com.github.navikt.dp-biblioteker:grunnbelop:$version"
        const val ktorUtils = "com.github.navikt.dp-biblioteker:ktor-utils:$version"
    }

    const val Streams = "com.github.navikt:dagpenger-streams:2019.10.04-11.54.1af65bdd3862"
    const val Events = "com.github.navikt:dagpenger-events:2019.08.06-10.38.92d9930cd257"
}

object Database {
    const val Postgres = "org.postgresql:postgresql:42.2.8"
    const val Kotlinquery = "com.github.seratch:kotliquery:1.3.1"
    const val Flyway = "org.flywaydb:flyway-core:6.0.4"
    const val HikariCP = "com.zaxxer:HikariCP:3.4.1"
    const val VaultJdbc = "no.nav:vault-jdbc:1.3.1"
}

object Fuel {
    const val version = "2.2.1"
    const val fuel = "com.github.kittinunf.fuel:fuel:$version"
    const val fuelMoshi = "com.github.kittinunf.fuel:fuel-moshi:$version"
    fun library(name: String) = "com.github.kittinunf.fuel:fuel-$name:$version"
}

object GradleWrapper {
    const val version = "5.5"
}

object Junit5 {
    const val version = "5.5.2"
    const val api = "org.junit.jupiter:junit-jupiter-api:$version"
    const val params = "org.junit.jupiter:junit-jupiter-params:$version"
    const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
    const val vintageEngine = "org.junit.vintage:junit-vintage-engine:$version"
    const val kotlinRunner = "io.kotlintest:kotlintest-runner-junit5:3.3.0"
    fun library(name: String) = "org.junit.jupiter:junit-jupiter-$name:$version"
}

object Json {
    const val version = "20180813"
    const val library = "org.json:json:$version"
}

object JsonAssert {
    const val version = "1.5.0"
    const val jsonassert = "org.skyscreamer:jsonassert:$version"
}

object Kafka {
    const val version = "2.0.1"
    const val clients = "org.apache.kafka:kafka-clients:$version"
    const val streams = "org.apache.kafka:kafka-streams:$version"
    const val streamTestUtils = "org.apache.kafka:kafka-streams-test-utils:$version"
    fun library(name: String) = "org.apache.kafka:kafka-$name:$version"
    object Confluent {
        const val version = "5.0.3"
        const val avroStreamSerdes = "io.confluent:kafka-streams-avro-serde:$version"
        fun library(name: String) = "io.confluent:$name:$version"
    }
}

object KafkaEmbedded {
    const val env = "no.nav:kafka-embedded-env:2.0.2"
}

object Klint {
    const val version = "0.33.0"
}

object Konfig {
    const val konfig = "com.natpryce:konfig:1.6.10.0"
}

object Kotlin {
    const val version = "1.3.50"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"

    object Logging {
        const val version = "1.7.6"
        const val kotlinLogging = "io.github.microutils:kotlin-logging:$version"
    }
}

object Kotlinx {
    const val bimap = "com.uchuhimo:kotlinx-bimap:1.2"
}

object Ktor {
    const val version = "1.2.5"
    const val server = "io.ktor:ktor-server:$version"
    const val serverNetty = "io.ktor:ktor-server-netty:$version"
    const val auth = "io.ktor:ktor-auth:$version"
    const val authJwt = "io.ktor:ktor-auth-jwt:$version"
    const val locations = "io.ktor:ktor-locations:$version"
    const val micrometerMetrics = "io.ktor:ktor-metrics-micrometer:$version"
    const val ktorTest = "io.ktor:ktor-server-test-host:$version"
    fun library(name: String) = "io.ktor:ktor-$name:$version"
}

object Log4j2 {
    const val version = "2.12.1"
    const val api = "org.apache.logging.log4j:log4j-api:$version"
    const val core = "org.apache.logging.log4j:log4j-core:$version"
    const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:$version"

    fun library(name: String) = "org.apache.logging.log4j:log4j-$name:$version"
    object Logstash {
        private const val version = "0.19"
        const val logstashLayout = "com.vlkan.log4j2:log4j2-logstash-layout-fatjar:$version"
    }
}

object Micrometer {
    const val version = "1.3.0"
    const val prometheusRegistry = "io.micrometer:micrometer-registry-prometheus:$version"
}

object Moshi {
    const val version = "1.8.0"
    const val moshi = "com.squareup.moshi:moshi:$version"
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:$version"
    const val moshiAdapters = "com.squareup.moshi:moshi-adapters:$version"
    const val moshiKtor = "com.ryanharter.ktor:ktor-moshi:1.0.1"
    fun library(name: String) = "com.squareup.moshi:moshi-$name:$version"
}

object Mockk {
    const val version = "1.9.3"
    const val mockk = "io.mockk:mockk:$version"
}

object Nare {
    const val version = "768ae37"
    const val nare = "no.nav:nare:$version"
}

object Prometheus {
    const val version = "0.7.0"
    const val common = "io.prometheus:simpleclient_common:$version"
    const val hotspot = "io.prometheus:simpleclient_hotspot:$version"
    const val log4j2 = "io.prometheus:simpleclient_log4j2:$version"
    fun library(name: String) = "io.prometheus:simpleclient_$name:$version"
    object Nare {
        const val version = "0b41ab4"
        const val prometheus = "no.nav:nare-prometheus:$version"
    }
}

object Slf4j {
    const val version = "1.7.25"
    const val api = "org.slf4j:slf4j-api:$version"
}

object Spotless {
    const val version = "3.24.2"
    const val spotless = "com.diffplug.gradle.spotless"
}

object Shadow {
    const val version = "4.0.3"
    const val shadow = "com.github.johnrengelman.shadow"
}

object TestContainers {
    const val version = "1.12.2"
    const val postgresql = "org.testcontainers:postgresql:$version"
    const val kafka = "org.testcontainers:kafka:$version"
}

object Ulid {
    const val version = "8.2.0"
    const val ulid = "de.huxhorn.sulky:de.huxhorn.sulky.ulid:$version"
}

object Vault {
    const val javaDriver = "com.bettercloud:vault-java-driver:3.1.0"
}

object Wiremock {
    const val version = "2.21.0"
    const val standalone = "com.github.tomakehurst:wiremock-standalone:$version"
}
