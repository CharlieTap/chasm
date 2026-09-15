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

enum class InterfaceVisibility {
    INTERNAL,
    PUBLIC,
}

enum class FactoryVisibility {
    INTERNAL,
    PUBLIC,
}

enum class ImplementationVisibility {
    INTERNAL,
    PUBLIC,
    PRIVATE,
}

@ChasmDsl
open class ChasmExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        val modules: NamedDomainObjectContainer<WasmModule> = objects.domainObjectContainer(WasmModule::class.java)
        val runtimeDependencyConfiguration: Property<RuntimeDependencyConfiguration> =
            objects.property(RuntimeDependencyConfiguration::class.java).convention(RuntimeDependencyConfiguration.IMPLEMENTATION)
    }
