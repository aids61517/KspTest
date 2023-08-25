plugins {
    kotlin("jvm")
}

dependencies {
    val kspVersion: String by project
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.squareup.okio:okio:3.5.0")
}