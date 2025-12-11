plugins { id("com.android.application"); kotlin("android") }
android {
    namespace = "com.example.whip"
    compileSdk = 34
    defaultConfig { applicationId="com.example.whip"; minSdk=21; targetSdk=34; versionCode=1; versionName="1.0" }
    buildTypes { release { isMinifyEnabled=false } }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
