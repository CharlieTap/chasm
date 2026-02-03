package io.github.charlietap.chasm.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject
import kotlin.jvm.java

@DslMarker
annotation class ChasmDsl

enum class RuntimeDependencyConfiguration {
    API,
    IMPLEMENTATION,
}

enum class TypeVisibility {
    INTERNAL,
    PUBLIC,
}

@ChasmDsl
open class ChasmExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        val mode: Property<Mode> = objects.property(Mode::class.java).convention(Mode.CONSUMER)
        val modules: NamedDomainObjectContainer<WasmModule> = objects.domainObjectContainer(WasmModule::class.java)
        val runtimeDependencyConfiguration: Property<RuntimeDependencyConfiguration> =
            objects.property(RuntimeDependencyConfiguration::class.java).convention(RuntimeDependencyConfiguration.IMPLEMENTATION)
    }
