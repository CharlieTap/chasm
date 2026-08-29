package io.github.charlietap.chasm.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider

internal fun configureAndroid(
    project: Project,
    extension: ChasmExtension,
    workerClasspath: Provider<out Configuration>,
) {
    val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

    androidComponents.onVariants { variant ->
        val kotlinSources = variant.sources.kotlin
            ?: throw GradleException(
                "Chasm requires AGP built-in Kotlin for Android variant '${variant.name}'.",
            )

        extension.modules.configureEach { module ->
            val task = registerCodegenTask(
                project,
                module,
                variant.name,
                workerClasspath,
            )
            kotlinSources.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
        }
    }
}
