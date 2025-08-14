// Root build script

// Replace deprecated buildDir usage
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
