plugins {
    `java-library`
}

group = "com.matthew.plugin"
version = "1.0.0"

subprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

ext {
    set("pluginName", "BungeeQueuePlugin")
    set("pluginVersion", "1.0.0")
    set("mainClass", "com.matthew.plugin.BungeeQueuePlugin")
    set("description", "A BungeeCord plugin with a queue system")
    set("author", "Matthew")
}
