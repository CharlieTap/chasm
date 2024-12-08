package io.github.charlietap.bolt.plugin

import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class BoltExtension @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory,
) {
    val library: Property<String> = objects.property(String::class.java)
    val url: Property<String> = objects.property(String::class.java)
    val archiveFormat: Property<String> = objects.property(String::class.java).convention("zip")
    val artifactsDir: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir(
        DIR_ARTIFACTS
    ))
    val defFile: RegularFileProperty = objects.fileProperty().convention(layout.buildDirectory.file(FILE_DEF))

    private companion object {
        private const val DIR_ARTIFACTS = "bolt-artifacts"
        private const val FILE_DEF = "bolt.def"
    }
}
