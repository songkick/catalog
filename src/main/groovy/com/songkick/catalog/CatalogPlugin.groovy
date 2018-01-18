package com.songkick.catalog

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project


class CatalogPlugin implements Plugin<Project> {

    private static def adbBridge = new AdbBridgeManager();

    @Override
    void apply(Project project) {
        if (project != null && project.plugins.hasPlugin('com.android.application')) {
            applyAndroidProject(project, (DomainObjectCollection<BaseVariant>) project.android.applicationVariants)
        } else if (project != null && project.plugins.hasPlugin('com.android.library')) {
            applyAndroidProject(project, (DomainObjectCollection<BaseVariant>) project.android.libraryVariants)
        } else if (project != null && project.plugins.hasPlugin('com.android.test')) {
            applyAndroidProject(project, (DomainObjectCollection<BaseVariant>) project.android.applicationVariants)
        } else {
            throw new UnsupportedOperationException('Catalog plugin Plugin requires the Android Application or Library plugin to be configured')
        }
    }

    private static void applyAndroidProject(Project project, DomainObjectCollection<BaseVariant> variants) {
        variants.all { variant ->
            def slug = variant.name.capitalize()
            def connectedTask = project.tasks.findByName("connected${slug}AndroidTest")

            if (connectedTask) {
                adbBridge.initializeAdbExe(project.android.adbExe)
                def recorderGroup = adbBridge.createRecorderGroup()
                def printerTask = project.tasks.create("printConnected${slug}AndroidTest", LogCatPrinterTask)
                printerTask.logCatRecorderGroup = recorderGroup
                printerTask.group = 'Verification'
                printerTask.description = "Print logcat for ${variant.name} variant."
                printerTask.outputDir = project.file("$project.buildDir/outputs/androidTest-results")
                printerTask.dependsOn connectedTask

                connectedTask.doFirst {
                    recorderGroup.attach()
                }
                connectedTask.finalizedBy printerTask
            }
        }
    }
}
