enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Floodgate, Cumulus etc.
        maven("https://repo.opencollab.dev/maven-releases") {
            mavenContent { releasesOnly() }
        }
        maven("https://repo.opencollab.dev/maven-snapshots") {
            mavenContent { snapshotsOnly() }
        }

        // Paper, Velocity
        maven("https://papermc.io/repo/repository/maven-public")
        // Spigot
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots") {
            mavenContent { snapshotsOnly() }
        }

        // BungeeCord
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent { snapshotsOnly() }
        }

        // Minecraft
        maven("https://libraries.minecraft.net") {
            name = "minecraft"
            mavenContent { releasesOnly() }
        }

        mavenLocal()
        mavenCentral()

        // ViaVersion
        maven("https://repo.viaversion.com") {
            name = "viaversion"
        }

        // Sponge
        maven("https://repo.spongepowered.org/repository/maven-public/")

        maven("https://jitpack.io") {
            content { includeGroupByRegex("com\\.github\\..*") }
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("net.kyori.blossom") version "1.2.0"
        id("net.kyori.indra")
        id("net.kyori.indra.git")
    }
    includeBuild("build-logic")
}

rootProject.name = "geyser-parent"

include(":ap")
include(":api")
include(":geyser-api")
include(":bungeecord")
include(":spigot")
include(":sponge")
include(":standalone")
include(":velocity")
include(":common")
include(":core")

// Specify project dirs
project(":api").projectDir = file("api/base")
project(":geyser-api").projectDir = file("api/geyser")
project(":bungeecord").projectDir = file("bootstrap/bungeecord")
project(":spigot").projectDir = file("bootstrap/spigot")
project(":sponge").projectDir = file("bootstrap/sponge")
project(":standalone").projectDir = file("bootstrap/standalone")
project(":velocity").projectDir = file("bootstrap/velocity")