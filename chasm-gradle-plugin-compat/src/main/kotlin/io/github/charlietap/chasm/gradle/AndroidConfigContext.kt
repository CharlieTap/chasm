package io.github.charlietap.chasm.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

data class AndroidConfigContext(
    val project: Project,
    val extension: ChasmExtension,
    val workerClasspath: Configuration,
)
