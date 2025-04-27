// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//}

// Top-level build file where you can add configuration options common to all sub-projects/modules. buildscript { dependencies { classpath 'com.google.gms:google-services:4.4.2' } }

plugins {
    //alias(libs.plugins.android.application) apply false
    //alias(libs.plugins.kotlin.android) apply false
    id("com.android.application") version "8.9.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

}
