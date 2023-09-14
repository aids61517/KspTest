plugins {
    kotlin("jvm")
}

dependencies {
    val kspVersion: String by project

    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
    implementation("com.squareup.okio:okio:3.5.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}