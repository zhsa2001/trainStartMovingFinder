import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")

//    kotlin("plugin.serialization")
}

group = "org.metropoliten.zhsa2001"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://releases.aspose.com/java/repo/")
    google()
}

val ktor_version="3.0.2"

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("com.aspose:aspose-ocr:24.8.0")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
//    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
    implementation("net.sourceforge.tess4j:tess4j:5.13.0")
    implementation("org.openpnp:opencv:4.9.0-0")


}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "trainStartMovingFinder"
            packageVersion = "1.0.1"

            val iconsRoot = project.file("src/main/resources")

            windows {
                iconFile.set(iconsRoot.resolve("icon.ico"))
                menuGroup = "Compose Examples"

                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
//                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFAAF"
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}
