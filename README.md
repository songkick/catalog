# Catalog

Catalog is a gradle plugin for Android that records logs when running Espresso tests.

To include it in your project:
```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.songkick:catalog:1.0-SNAPSHOT"
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.songkick.catalog'
```