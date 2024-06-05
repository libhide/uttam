@file:Suppress("UnstableApiUsage")

import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    kotlin("kapt")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

fun getProperty(filename: String, propName: String): String? {
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.1"
    }

    defaultConfig {
        applicationId = "com.ratik.uttam.prod"
        minSdk = 24
        versionCode = 21
        versionName = "4.4"

        buildConfigField(
            "String",
            "CLIENT_ID",
            "\"${getProperty("local.properties", "client_id")}\""
        )

        // TODO: remove this after migration to Hilt
        javaCompileOptions.annotationProcessorOptions.arguments["dagger.hilt.disableModulesHaveInstallInCheck"] =
            "true"
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
                file("proguard-rules.pro")
            )
            signingConfig = signingConfigs["config"]
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Kotlin
    // TODO: figure out how to use this BOM correctly
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))

    // AppCompat
    implementation(libs.appcompat)
    implementation(libs.vectordrawable.animated)
    implementation(libs.media)
    implementation(libs.legacy.support)
    implementation(libs.material)

    // Android X
    implementation(libs.androidx.work.manager)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    api(libs.bundles.compose.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Networking
    implementation(libs.okhttp.logging.inspector)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.adpater.rxjava)

    api(libs.moshi.core)
    ksp(libs.moshi.codegen)
    implementation(libs.retrofit.converter.moshi)

    // Rx
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.rxbinding)

    // RxPermissions
    implementation(libs.rxpermissions)

    // DI
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // App Tour
    implementation(libs.app.tour)

    // Butter Knife
    implementation(libs.butterknife)
    annotationProcessor(libs.butterknife.compiler)

    // Firebase
    implementation(libs.firebase.core)
    implementation(libs.firebase.config)

    // Logging
    api(libs.timber)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}