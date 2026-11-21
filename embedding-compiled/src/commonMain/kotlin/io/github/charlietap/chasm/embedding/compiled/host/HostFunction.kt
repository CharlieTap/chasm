package io.github.charlietap.chasm.embedding.compiled.host

import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Memory
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.Tag
import io.github.charlietap.chasm.host.ModuleIndex

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
