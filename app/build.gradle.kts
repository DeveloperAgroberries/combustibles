plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.agroberriesmx.combustiblesagroberries"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.agroberriesmx.combustiblesagroberries"
        minSdk = 21
        targetSdk = 35
        versionCode = 8 // ultima actualizacion de version: RICARDO DIMAS 29/07/2025
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            resValue("string", "AgroberriesMX", "[DEBUG]Combustibles Agroberries MX")
            //buildConfigField("String", "BASE_URL", "\"http://54.165.41.23:5053/api/CombustiblesApp/\"")
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.60:5011/api/CombustiblesApp/\"")
        }

        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "AgroberriesMX", "Combustibles Agroberries MX")
            //buildConfigField("String", "BASE_URL", "\"http://54.165.41.23:5053/api/CombustiblesApp/\"")
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.60:5011/api/CombustiblesApp/\"")
        }
    }
    testBuildType = "debug" // 👈 añade esto aquí
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    //NavComponent
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Sqlite
    implementation(libs.androidx.sqlite)

    //Glide
    implementation(libs.glide)
    implementation(libs.glide.annotations)
    kapt(libs.glide.compiler)

    //ZXing
    implementation(libs.zxing)

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // CameraX core library
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("androidx.camera:camera-extensions:1.3.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}