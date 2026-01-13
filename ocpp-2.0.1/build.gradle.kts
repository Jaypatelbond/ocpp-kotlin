plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // Project dependencies
    api(project(":ocpp-core"))
    
    // Kotlinx
    implementation(libs.bundles.kotlinx)
    
    // Testing
    testImplementation(libs.bundles.testing)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.jaypatelbond.ocpp-kotlin"
            artifactId = "ocpp-2.0.1"
            version = project.version.toString()
            
            from(components["java"])
        }
    }
}
