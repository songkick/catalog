package com.songkick.catalog

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project


class CatalogPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (project != null && project.plugins.hasPlugin('com.android.application')) {
            applyAndroidProject(project, (DomainObjectCollection<BaseVariant>) project.android.applicationVariants)
        } else if (project != null && project.plugins.hasPlugin('com.android.library')) {
            applyAndroidProject(project, (DomainObjectCollection<BaseVariant>) project.android.libraryVariants)
        } else {
            throw new UnsupportedOperationException('Catalog plugin Plugin requires the Android Application or Library plugin to be configured')
        }
    }

    private static void applyAndroidProject(Project project, DomainObjectCollection<BaseVariant> variants) {
        variants.all { variant ->
            def slug = variant.name.capitalize()
            def connectedTask = project.tasks.findByName("connected${slug}AndroidTest")

            if (connectedTask) {
                def recorderTask = project.tasks.create("recordConnected${slug}AndroidTest", LogCatRecorderTask)
                recorderTask.group = 'Verification'
                recorderTask.description = "Record logcat for ${variant.name} variant."
                recorderTask.adbExe = project.android.adbExe

                def printerTask = project.tasks.create("printConnected${slug}AndroidTest", LogCatPrinterTask)
                printerTask.group = 'Verification'
                recorderTask.description = "Print logcat for ${variant.name} variant."
                printerTask.devices = recorderTask.devices
                printerTask.outputDir = project.file("$project.buildDir/outputs/androidTest-results")

                connectedTask.dependsOn recorderTask
                connectedTask.finalizedBy printerTask
            }
        }
    }
}
