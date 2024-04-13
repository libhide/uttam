@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.ksp) apply false
}