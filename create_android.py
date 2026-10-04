import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content)

base_dir = "android-app"
os.makedirs(base_dir, exist_ok=True)

# Generate settings.gradle
create_file(f"{base_dir}/settings.gradle", """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Field Attendance"
include ':app'
""")

# Generate build.gradle
create_file(f"{base_dir}/build.gradle", """
plugins {
    id 'com.android.application' version '8.1.1' apply false
}
""")

# Generate gradle.properties
create_file(f"{base_dir}/gradle.properties", """
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
""")

# Generate app/build.gradle
create_file(f"{base_dir}/app/build.gradle", """
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.company.fieldattendance'
    compileSdk 34

    defaultConfig {
        applicationId "com.company.fieldattendance"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'
    implementation 'androidx.navigation:navigation-fragment:2.7.5'
    implementation 'androidx.navigation:navigation-ui:2.7.5'
    implementation 'androidx.room:room-runtime:2.6.0'
    annotationProcessor 'androidx.room:room-compiler:2.6.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'androidx.camera:camera-camera2:1.3.0'
    implementation 'androidx.camera:camera-lifecycle:1.3.0'
    implementation 'androidx.camera:camera-view:1.3.0'
    implementation 'com.google.android.gms:play-services-location:21.0.1'
}
""")

# Generate AndroidManifest.xml
create_file(f"{base_dir}/app/src/main/AndroidManifest.xml", """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.company.fieldattendance">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FieldAttendance">
        
        <activity
            android:name=".ui.common.SplashActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".ui.auth.LoginActivity" />
        <activity android:name=".ui.employee.EmployeeDashboardActivity" />
        <activity android:name=".ui.manager.ManagerDashboardActivity" />
        
    </application>

</manifest>
""")

# Generate values/strings.xml
create_file(f"{base_dir}/app/src/main/res/values/strings.xml", """<resources>
    <string name="app_name">Field Attendance</string>
</resources>
""")

# Generate values/themes.xml
create_file(f"{base_dir}/app/src/main/res/values/themes.xml", """<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.FieldAttendance" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
    </style>
</resources>
""")

# Generate values/colors.xml
create_file(f"{base_dir}/app/src/main/res/values/colors.xml", """<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
""")

# Create source folders
src_base = f"{base_dir}/app/src/main/java/com/company/fieldattendance"
os.makedirs(f"{src_base}/data/api", exist_ok=True)
os.makedirs(f"{src_base}/data/local", exist_ok=True)
os.makedirs(f"{src_base}/data/model", exist_ok=True)
os.makedirs(f"{src_base}/data/repository", exist_ok=True)
os.makedirs(f"{src_base}/domain/model", exist_ok=True)
os.makedirs(f"{src_base}/domain/usecase", exist_ok=True)
os.makedirs(f"{src_base}/ui/auth", exist_ok=True)
os.makedirs(f"{src_base}/ui/employee", exist_ok=True)
os.makedirs(f"{src_base}/ui/manager", exist_ok=True)
os.makedirs(f"{src_base}/ui/common", exist_ok=True)
os.makedirs(f"{src_base}/services", exist_ok=True)
os.makedirs(f"{src_base}/workers", exist_ok=True)
os.makedirs(f"{src_base}/utils", exist_ok=True)

# Generate basic activities
create_file(f"{src_base}/ui/common/SplashActivity.java", """package com.company.fieldattendance.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 1500);
    }
}
""")

create_file(f"{src_base}/ui/auth/LoginActivity.java", """package com.company.fieldattendance.ui.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_login);
    }
}
""")

create_file(f"{src_base}/ui/employee/EmployeeDashboardActivity.java", """package com.company.fieldattendance.ui.employee;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
""")

create_file(f"{src_base}/ui/manager/ManagerDashboardActivity.java", """package com.company.fieldattendance.ui.manager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
""")

print("Android Project created successfully.")
