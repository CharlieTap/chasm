package io.github.charlietap.corpus.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class WasmCorpusPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<WasmCorpusPluginExtension>("corpus")
        val runProperties = RunProperties(project.providers, extension, project.gradle.startParameter.taskNames)
        val fixtures = FixtureTasks(project, extension, runProperties).register()
        val environments = Environments(project, extension, runProperties)
        val hostEnvironments = HostEnvironments(project.providers.of(CorpusMachineValueSource::class.java) {})

        project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            KotlinEnvironments(project, extension, fixtures, environments, hostEnvironments).register()
        }
        project.pluginManager.withPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID) {
            AndroidTestResources(project, fixtures).register()
            AndroidDeviceEnvironment(project, extension, environments).register()
        }
    }

    private companion object {
        const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
        const val ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY_PLUGIN_ID = "com.android.kotlin.multiplatform.library"
    }
}
