import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }

  dependencies {
    classpath(libs.plugin.android.cache.fix)
    classpath(libs.plugin.androidgradleplugin)
    classpath(libs.plugin.dokka)
    classpath(libs.plugin.kotlin)
    classpath(libs.plugin.licensee)
    classpath(libs.plugin.publish)
  }
}

plugins {
  alias(libs.plugins.codequalitytools)
}

codeQualityTools {
  lint {
    textReport = true
  }
  checkstyle {
    enabled = false
  }
  pmd {
    enabled = false
  }
  ktlint {
    toolVersion = "1.4.1"
  }
  detekt {
    enabled = false
  }
  cpd {
    enabled = false
  }
}

subprojects {
  repositories {
    mavenCentral()
    google()
  }

  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      freeCompilerArgs.addAll(
        "-Xannotation-default-target=param-property",
      )
    }
  }
}
