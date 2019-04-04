import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import no.nils.wsdl2java.Wsdl2JavaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutinesVersion = "1.0.1"
val jettyServerVersion = "9.4.15.v20190215"
val logbackVersion = "1.2.3"
val logstashEncoderVersion = "5.1"
val prometheusVersion = "0.6.0"
val jerseyVersion = "2.27"
val jaxwsToolsVersion = "2.3.1"
val javaxActivationVersion = "1.1.1"
val jaxbApiVersion = "2.4.0-b180830.0359"
val jaxbRuntimeVersion = "2.4.0-b180830.0438"
val jaxwsApiVersion = "2.3.1"
val javaxAnnotationApiVersion = "1.3.2"
val cxfVersion = "3.2.6"
val subscriptionVersion = "1.0.5"

group = "no.nav.syfo"
version = "1.0-SNAPSHOT"

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "no.nav.syfo.BootstrapKt"
}

plugins {
    java
    kotlin("jvm") version "1.3.21"
    id("no.nils.wsdl2java") version "0.10"
    id("org.jmailen.kotlinter") version "1.21.0"
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

buildscript {
    dependencies {
        classpath("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
        classpath("org.glassfish.jaxb:jaxb-runtime:2.4.0-b180830.0438")
        classpath("com.sun.activation:javax.activation:1.2.0")
        classpath("com.sun.xml.ws:jaxws-tools:2.3.1") {
            exclude(group = "com.sun.xml.ws", module = "policy")
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven (url= "https://repo.adeo.no/repository/maven-snapshots/")
    maven (url= "https://repo.adeo.no/repository/maven-releases/")
    maven (url= "https://dl.bintray.com/kotlin/ktor")
    maven (url= "https://dl.bintray.com/spekframework/spek-dev")
    maven (url= "http://packages.confluent.io/maven/")
    maven (url= "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val navWsdl= configurations.create("navWsdl") {
    setTransitive(false)
}

dependencies {
    implementation(kotlin("stdlib"))

    navWsdl("no.nav.tjenester:subscription-nav-emottak-eletter-web:$subscriptionVersion@zip")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("org.eclipse.jetty:jetty-server:$jettyServerVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyServerVersion")

    implementation("org.glassfish.jersey.core:jersey-server:$jerseyVersion")
    implementation("org.glassfish.jersey.containers:jersey-container-jetty-servlet:$jerseyVersion")
    implementation("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:$jerseyVersion")

    implementation ("ch.qos.logback:logback-classic:$logbackVersion")
    implementation ("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")

    implementation ("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation ("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")

    implementation ("com.sun.xml.ws:jaxws-tools:$jaxwsApiVersion") {
        exclude(group = "com.sun.xml.ws", module = "policy")
    }

    implementation ("org.apache.cxf:cxf-rt-ws-security:$cxfVersion") {
        exclude(group = "org.opensaml")
    }


    implementation ("javax.annotation:javax.annotation-api:$javaxAnnotationApiVersion")
    implementation ("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation ("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation ("javax.activation:activation:$javaxActivationVersion")
}

tasks {
    create("printVersion") {
        println(project.version)
    }

    withType<KotlinCompile> {
        dependsOn("wsdl2java")

        kotlinOptions.jvmTarget = "1.8"
    }

    val copyWsdlFromArtifacts =  create("copyWsdlFromArtifacts", Copy::class) {
        includeEmptyDirs = false

        navWsdl.asFileTree.forEach { artifact ->
            from(zipTree(artifact))
        }
        into("$buildDir/schema/")
        include("**/*.xsd", "**/*.wsdl")
    }

    withType<Wsdl2JavaTask> {
        dependsOn(copyWsdlFromArtifacts)
        wsdlDir = file("$buildDir/schema")
        wsdlsToGenerate = listOf(
            mutableListOf("-xjc", "-b", "$projectDir/src/main/resources/xjb/binding.xml", "$buildDir/schema/subscription.wsdl")
        )
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
        testLogging.showStandardStreams = true
    }
}