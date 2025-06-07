package io.github.charlietap.chasm.gradle

import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

enum class Mode {
    CONSUMER,
    PRODUCER
}

data class CodegenConfig(
    val transformStrings: Boolean = true,
    val generateTypesafeGlobalFunctions: Boolean = false,
)

open class ChasmExtension @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory,
) {
    val config: Property<CodegenConfig> = objects.property(CodegenConfig::class.java).convention(CodegenConfig())
    val mode: Property<Mode> = objects.property(Mode::class.java).convention(Mode.CONSUMER)
    val packageName: Property<String> = objects.property(String::class.java)
}
