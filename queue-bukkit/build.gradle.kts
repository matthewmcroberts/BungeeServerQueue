plugins {
    id("java")
}

ext {
    set("mainClass", "com.matthew.plugin.BungeeQueueBukkitPlugin")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            mapOf(
                "pluginName" to rootProject.extra["pluginName"],
                "pluginVersion" to rootProject.extra["pluginVersion"],
                "mainClass" to extra["mainClass"],
                "description" to rootProject.extra["description"],
                "author" to rootProject.extra["author"]
            )
        )
    }
}
