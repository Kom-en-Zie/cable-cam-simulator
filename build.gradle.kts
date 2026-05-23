plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
}

group = "nl.komenzie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server and Engine
    implementation("io.ktor:ktor-server-netty:3.4.0")
    implementation("io.ktor:ktor-server-websockets:3.4.0")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation:3.4.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

// --- TypeScript front-end build ------------------------------------------------
// Sources live in web/src and compile into src/main/resources/web, which Ktor
// serves via staticResources("/", "web"). Hooked into processResources so that
// `./gradlew run` (and IntelliJ's run config) regenerate the JS automatically.
//
// npm is resolved to an absolute path because Gradle daemons launched from
// IntelliJ frequently inherit a PATH that lacks /opt/homebrew/bin.

val isWindows = System.getProperty("os.name").lowercase().contains("windows")

fun findNpmExecutable(): File {
    val executableName = if (isWindows) "npm.cmd" else "npm"
    val pathDirs = System.getenv("PATH")
        ?.split(File.pathSeparator)
        ?: emptyList()
    val fallbackDirs = listOf("/opt/homebrew/bin", "/usr/local/bin", "/usr/bin")
    for (dir in pathDirs + fallbackDirs) {
        if (dir.isBlank()) continue
        val candidate = File(dir, executableName)
        if (candidate.isFile && candidate.canExecute()) return candidate
    }
    throw GradleException(
        "Could not locate `$executableName`. Install Node.js, or add its bin directory to PATH."
    )
}

val npmExecutable: File = findNpmExecutable()
val nodeBinDir: String = npmExecutable.parent

fun Exec.useNpm(vararg args: String) {
    commandLine(listOf(npmExecutable.absolutePath) + args)
    // npm shells out to `node`, which it locates via PATH — make sure both live there.
    environment("PATH", nodeBinDir + File.pathSeparator + (System.getenv("PATH") ?: ""))
}

val npmInstall = tasks.register<Exec>("npmInstall") {
    description = "Installs npm dependencies for the TypeScript front-end."
    group = "build"
    workingDir = file("web")
    useNpm("install")
    inputs.file("web/package.json")
    outputs.dir("web/node_modules")
}

val buildWeb = tasks.register<Exec>("buildWeb") {
    description = "Compiles the TypeScript front-end into src/main/resources/web/."
    group = "build"
    dependsOn(npmInstall)
    workingDir = file("web")
    useNpm("run", "build")
    inputs.dir("web/src")
    inputs.file("web/tsconfig.json")
    outputs.files(
        fileTree("src/main/resources/web") {
            include("**/*.js", "**/*.js.map")
        }
    )
}

tasks.named("processResources") {
    dependsOn(buildWeb)
}