plugins {
    id("java")
}

group = "me.rafael5gr2.mythicmobsfixer"
val pluginVersion = "1.0.0"
val pluginDescription = "A simple plugin that fixes bugs from MythicMobs."
version = pluginVersion

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {

    compileJava {
        options.encoding = "UTF-8"
        //options.compilerArgs.add("-Xlint:deprecation")
        //options.compilerArgs.add("-Xlint:unchecked")
        dependsOn(clean)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "pluginVersion" to pluginVersion,
                "pluginDescription" to pluginDescription
            )
        }
    }
}
