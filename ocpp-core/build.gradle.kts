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
    // Kotlinx
    implementation(libs.bundles.kotlinx)
    
    // Networking
    implementation(libs.bundles.networking)
    
    // Testing
    testImplementation(libs.bundles.testing)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.jaypatelbond.ocpp-kotlin"
            artifactId = "ocpp-core"
            version = project.version.toString()
            
            from(components["java"])
        }
    }
}
