package io.github.charlietap.chasm.gradle

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject
import kotlin.jvm.java
import org.gradle.api.NamedDomainObjectContainer

@DslMarker
annotation class ChasmDsl

@ChasmDsl
open class ChasmExtension @Inject constructor(
    objects: ObjectFactory,
) {
    val modules: NamedDomainObjectContainer<WasmModule> = objects.domainObjectContainer(WasmModule::class.java)
}
