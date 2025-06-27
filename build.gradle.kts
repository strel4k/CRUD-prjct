plugins {
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation ("mysql:mysql-connector-java:8.0.33")
    implementation ("org.liquibase:liquibase-core:4.27.0")
    implementation("com.google.code.gson:gson:2.10.1")


    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation ("org.mockito:mockito-core:5.12.0")
}
application {
    mainClass.set("com.crudApp.Main")
}
tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("liquibaseUpdate") {
    group = "liquibase"
    description = "Applies Liquibase migrations"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("liquibase.integration.commandline.Main")
    args = listOf(
        "--changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml",
        "--url=jdbc:mysql://localhost:3306/prjct_app",
        "--username=root",
        "--password=password",
        "--driver=com.mysql.cj.jdbc.Driver",
        "update"
    )
}