import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  kotlin("kapt")
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.spotless)
}

fun getProperty(
  filename: String,
  propName: String,
): String? {
  val propsFile = rootProject.file(filename)
  return if (propsFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(propsFile))
    props.getProperty(propName)
  } else {
    print("$filename does not exist!")
    null
  }
}

android {
  namespace = "com.ratik.uttam"
  compileSdk = 34

  buildFeatures {
    buildConfig = true
    compose = true
  }

  composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

  kotlinOptions { jvmTarget = "17" }

  defaultConfig {
    applicationId = "com.ratik.uttam.prod"
    minSdk = 24
    targetSdk = 34
    versionCode = 21
    versionName = "4.4"

    buildConfigField(
      "String",
      "CLIENT_ID",
      "\"${getProperty("local.properties", "client_id")}\"",
    )
  }

  val keystorePropertiesFile = rootProject.file("keystore.properties")
  val keystoreProperties = Properties()
  keystoreProperties.load(FileInputStream(keystorePropertiesFile))

  signingConfigs {
    create("config") {
      keyAlias = keystoreProperties.getProperty("keyAlias")
      keyPassword = keystoreProperties.getProperty("keyPassword")
      storeFile = file("$rootDir/${keystoreProperties.getProperty("storeFile")}")
      storePassword = keystoreProperties.getProperty("storePassword")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        file("proguard-rules.pro"),
      )
      signingConfig = signingConfigs["config"]
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kapt { correctErrorTypes = true }

spotless {
  val ktLintVersion = libs.versions.ktlint.get()
  kotlin {
    target("**/*.kt")
    ktlint(ktLintVersion)
      .editorConfigOverride(
        mapOf(
          "indent_size" to "2",
          "disabled_rules" to "filename",
          "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
          "ij_kotlin_allow_trailing_comma" to "true",
        ),
      )
  }

  kotlinGradle {
    target("**/*.gradle.kts")
    ktlint(ktLintVersion)
      .editorConfigOverride(
        mapOf(
          "indent_size" to "2",
          "disabled_rules" to "filename",
          "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
          "ij_kotlin_allow_trailing_comma" to "true",
        ),
      )
  }
}

dependencies {
  // Android X
  implementation(libs.androidx.work.manager)
  implementation(libs.androidx.hilt.work)
  kapt(libs.androidx.hilt.compiler)
  implementation(libs.androidx.splashscreen)

  // Compose
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)
  api(libs.bundles.compose.core)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.coil)
  implementation(libs.coil.compose)
  implementation(libs.androidx.compose.tooling.preview)
  debugImplementation(libs.androidx.compose.tooling)
  implementation(libs.accompanist.permissions)
  implementation(libs.accompanist.systemuicontroller)

  // Networking
  implementation(libs.okhttp.logging.inspector)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)

  // DI
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)

  // Logging
  api(libs.timber)

  // Testing
  testImplementation(libs.junit)
}
