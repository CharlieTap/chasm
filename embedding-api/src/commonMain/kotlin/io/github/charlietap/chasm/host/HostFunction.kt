package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag

typealias HostStack = LongArray

typealias HostReferenceStack = Array<Any?>

typealias HostParameters = Int

typealias HostResults = Int

interface HostModuleInstance {

    val instance: Instance
}

interface HostResources {

    val store: Store

    fun memory(
        module: HostModuleInstance,
        index: ModuleIndex.MemoryIndex,
    ): Memory

    fun table(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
    ): Table

    fun global(
        module: HostModuleInstance,
        index: ModuleIndex.GlobalIndex,
    ): Global

    fun tag(
        module: HostModuleInstance,
        index: ModuleIndex.TagIndex,
    ): Tag
}

fun interface HostFunction {

    context(
        _: HostStack,
        _: HostReferenceStack,
        _: HostModuleInstance,
        _: HostResources,
    )
    fun invoke(
        parameters: HostParameters,
        results: HostResults,
    )
}
