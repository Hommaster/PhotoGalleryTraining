plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.navigationSafeargsKotlin)
}

android {
    namespace = "com.example.photogallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.photogallery"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Implementation for Fragment
    implementation(libs.androidx.fragment.ktx)
    // Implementation for RecyclerView
    implementation(libs.androidx.recyclerview)
    // Implementation for lifecycleViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Implementation for Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.okhttp)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Implementation for converter-Moshi
    implementation(libs.moshi)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.converter.moshi)

    // Implementation for KSP
    ksp(libs.androidx.room.compiler)

    // coil for load photo from uri-address
    implementation(libs.coil)

    // datastore for simple persistence
    implementation(libs.datastore)

    // implementation for work-er
    implementation(libs.work.runtime.ktx)

    // implementation for navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.androidx.webkit)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}