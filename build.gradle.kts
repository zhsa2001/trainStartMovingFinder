import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "org.metropoliten.zhsa2001"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://releases.aspose.com/java/repo/")
    google()
}

val ktor_version="3.0.2"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("com.aspose:aspose-ocr:24.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
    implementation("net.sourceforge.tess4j:tess4j:5.13.0")
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
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}
