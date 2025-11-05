import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.time.LocalDate
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.spotless)
  alias(libs.plugins.versions)
  alias(libs.plugins.shadow)
  // kotlin("jvm") version "2.2.21"
  // id("com.diffplug.spotless") version "8.0.0"
  // id("com.github.ben-manes.versions") version "0.53.0"
  // id("com.gradleup.shadow") version "9.2.2"
}

group = "de.flavormate"

version = "1.0.1"

repositories { mavenCentral() }

dependencies {
  testImplementation(kotlin("test"))

  implementation(libs.kotlin.exposed.core)
  implementation(libs.kotlin.exposed.dao)
  implementation(libs.kotlin.exposed.jdbc)
  implementation(libs.kotlin.exposed.java.time)
  implementation(libs.postgresql)
  implementation(libs.flyway.core)
  implementation(libs.flyway.postgresql)
  implementation(libs.apache.commons.io)
  implementation(libs.dotenv)
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

application { mainClass.set("de.flavormate.MainKt") }

tasks {
  shadowJar {
    archiveClassifier.set("standalone")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  build {
    dependsOn(shadowJar) // Trigger fat jar creation during build
  }
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(21) }

spotless {
  //    ratchetFrom("HEAD")
  format("misc") {
    // define the files to apply `misc` to
    target(".gitattributes", ".gitignore")

    // define the steps to apply to those files
    trimTrailingWhitespace()
    leadingSpacesToTabs()
    endWithNewline()
  }
  java {
    cleanthat()
    googleJavaFormat(libs.versions.google.format.get())
      .reflowLongStrings()
      .formatJavadoc(true)
      .reorderImports(true)
    formatAnnotations()
    removeUnusedImports()
    licenseHeader("/* Licensed under AGPLv3 2024 - ${LocalDate.now().year} */")
  }
  flexmark {
    target("**/*.md")
    flexmark()
  }
  format("styling") {
    target("**/*.css", "**/*.js", "**/*.json", "**/*.yaml")
    prettier()
  }
  kotlin {
    // by default the target is every '.kt' and '.kts' file in the java source sets
    ktfmt().googleStyle()
    licenseHeader(
      "/* Licensed under AGPLv3 2024 - ${LocalDate.now().year} */"
    ) // or licenseHeaderFile
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt().googleStyle()
  }
}

apply(from = "$projectDir/gradle/preCommit.gradle")

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
  fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
  }

  rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}
