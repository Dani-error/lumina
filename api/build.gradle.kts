dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.2")
}

tasks.test {
    useJUnitPlatform()
}