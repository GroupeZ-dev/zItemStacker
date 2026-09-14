import org.gradle.kotlin.dsl.invoke

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("re.alwyn974.groupez.repository") version "1.0.0"
}

group = "fr.maxlego08.itemstacker"
version = "2.0.1"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.maxlego08.itemstacker"
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://jitpack.io")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven(url = "https://libraries.minecraft.net/")
        maven {
            name = "faststatsReleases"
            url = uri("https://repo.faststats.dev/releases")
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.shadowJar {
        exclude("com/cryptomorin/xseries/profiles/**")
        exclude("com/cryptomorin/xseries/profiles/*/*")
        exclude("com/cryptomorin/xseries/reflection/**")
        exclude("com/cryptomorin/xseries/reflection/*/*")
        exclude("com/cryptomorin/xseries/messages/*")
        exclude("com/cryptomorin/xseries/particles/*")
        exclude("com/cryptomorin/xseries/inventory/*")
        exclude("com/cryptomorin/xseries/art/*")
        exclude("com/cryptomorin/xseries/XAttribute*")
        exclude("com/cryptomorin/xseries/XItemFlag*")
        exclude("com/cryptomorin/xseries/XPatternType*")
        exclude("com/cryptomorin/xseries/XBiome*")
        exclude("com/cryptomorin/xseries/NMSExtras*")
        exclude("com/cryptomorin/xseries/NoteBlockMusic*")
        exclude("com/cryptomorin/xseries/SkullCacheListener*")
        exclude("com/cryptomorin/xseries/XTag*")
        exclude("com/cryptomorin/xseries/XPotion*")
        exclude("com/cryptomorin/xseries/XMaterial*")
        exclude("com/cryptomorin/xseries/XItemStack*")
        exclude("com/cryptomorin/xseries/XBlock*")
        exclude("com/cryptomorin/xseries/XEntity*")
        exclude("com/cryptomorin/xseries/XEnchantment*")
        exclude("com/cryptomorin/xseries/SkullUtils*")
        exclude("com/cryptomorin/xseries/ReflectionUtils*")
        exclude("com/cryptomorin/xseries/XWorldBorder*")

        archiveBaseName.set("zItemStacker")
        archiveAppendix.set(if (project.path == ":") "" else project.name)
        archiveClassifier.set("")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible)
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")
        compileOnly("com.mojang:authlib:1.5.26")
        compileOnly("me.clip:placeholderapi:2.11.6")
    }
}

repositories {
    maven(url = "https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(projects.api)
    implementation("dev.faststats.metrics:bukkit:0.30.1")
    // api(projects.hooks)
}

tasks {
    shadowJar {

        relocate("dev.faststats", "fr.maxlego08.itemstacker.libs.faststats")

        // Les 3 artefacts FastStats (bukkit, core, config) embarquent chacun leur propre
        // META-INF/faststats.properties : on ne garde que la premiere entree.
        filesMatching("META-INF/faststats.properties") {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        // La version est capturee a la configuration : y acceder a l'execution via
        // project.version est deprecie et deviendra une erreur avec Gradle 10.
        val pluginVersion = project.version.toString()
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to pluginVersion)
        }
    }
}
