plugins {
    id("java")
    `java-library`
}

ext {
    set("pluginName", "BungeeQueuePlugin")
    set("pluginVersion", "1.0.0")
    set("description", "A BungeeCord plugin with a queue system")
    set("author", "Matthew (GoofIt/Mahht)")
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
        testImplementation("org.mockito:mockito-core:5.11.0")
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }

    tasks.test {
        useJUnitPlatform()
    }
}