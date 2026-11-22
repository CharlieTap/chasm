package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.reference.ExceptionReference

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTag(
    index: ModuleIndex.TagIndex,
    block: context(ModuleIndex.TagIndex) Tag.() -> T,
): T {
    return context(index) { resources.tag(module, index).block() }
}

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTag(
    index: Int,
    block: context(ModuleIndex.TagIndex) Tag.() -> T,
): T {
    return withTag(ModuleIndex.TagIndex(index), block)
}

fun Tag.createException(
    words: LongArray,
    references: Array<Any?>,
): ExceptionReference {
    return engine.createException(this, words, references)
}

context(resources: HostResources)
fun raise(exception: ExceptionReference): Nothing {
    resources.store.engine.raise(exception)
}

context(resources: HostResources)
fun raisePending(): Nothing {
    resources.store.engine.raisePending()
}

context(resources: HostResources)
fun takePending(): ExceptionReference {
    return resources.store.engine.takePending()
}

context(resources: HostResources)
val hasPending: Boolean
    get() = resources.store.engine.hasPending()
