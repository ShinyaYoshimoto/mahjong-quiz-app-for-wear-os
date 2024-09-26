plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file(project.findProperty("SIGNING_CONFIGS_DEBUG_STORE_FILE").toString())
            storePassword = project.findProperty("SIGNING_CONFIGS_DEBUG_STORE_PASSWORD").toString()
            keyPassword = project.findProperty("SIGNING_CONFIGS_DEBUG_KEY_PASSWORD").toString()
            keyAlias = project.findProperty("SIGNING_CONFIGS_DEBUG_KEY_ALIAS").toString()
        }
    }
    namespace = "com.example.mahjongquizapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mahjongquizapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        val apiRoot = project.findProperty("API_ROOT")
        buildConfigField("String", "API_ROOT", "\"${apiRoot.toString()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.volley)
    implementation(libs.runtime.livedata)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}