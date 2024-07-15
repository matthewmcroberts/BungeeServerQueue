plugins {
    id("java")
}

dependencies {
    implementation("io.github.waterfallmc:waterfall-api:1.20-R0.3-SNAPSHOT")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            mapOf(
                "pluginName" to rootProject.extra["pluginName"],
                "pluginVersion" to rootProject.extra["pluginVersion"],
                "mainClass" to rootProject.extra["mainClass"],
                "description" to rootProject.extra["description"],
                "author" to rootProject.extra["author"]
            )
        )
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = rootProject.extra["mainClass"]
    }
}
