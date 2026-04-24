plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.google.dagger)
    alias(libs.plugins.androidx.navigation)

}

android {
    namespace = "com.example.recipebook"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recipebook"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("boolean", "TESTING", "true")
            buildConfigField("String", "BASE_URL", "\"https://api.spoonacular.com/\"")
            buildConfigField("String", "API_KEY", "\"401293d3dcc346729d8697c6f234f52c\"")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "TESTING", "false")
            buildConfigField("String", "BASE_URL", "\"https://api.spoonacular.com/\"")
            buildConfigField("String", "API_KEY", "\"401293d3dcc346729d8697c6f234f52c\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")

        }
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)




    // jetpack DataStore
    implementation(libs.androidx.dataStore)

    // room
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)



    //Hilt
    implementation(libs.google.dagger)
    kapt(libs.google.dagger.compiler)

    //viewModel
    implementation(libs.androidx.fragment)

    //Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.json)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coil (для загрузки изображений)
    implementation(libs.coil)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle Runtime
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("com.squareup:javapoet:1.13.0")

}
