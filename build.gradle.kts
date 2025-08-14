// Root build script

plugins {
    // Declare plugin versions for subprojects (applied in modules)
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}

// Replace deprecated buildDir usage
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
