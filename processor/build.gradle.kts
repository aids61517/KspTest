plugins {
    kotlin("jvm")
}

dependencies {
    val kspVersion: String by project

    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.squareup:kotlinpoet:2.0.0")
    implementation("com.squareup:kotlinpoet-ksp:2.0.0")
    implementation("com.squareup.okio:okio:3.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}