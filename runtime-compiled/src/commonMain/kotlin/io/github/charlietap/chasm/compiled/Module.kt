package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.internal.ClassLoaderResources
import io.github.charlietap.chasm.compiled.internal.RuntimeLoadedCode
import io.github.charlietap.chasm.compiled.internal.compiledModuleReader
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module

fun module(compiled: CompiledModule): ChasmResult<Module, ModuleError> {

    val resources = ClassLoaderResources(compiled.javaClass.classLoader)

    return compiledModuleReader().read(compiled, resources, artifact = null)
}

fun module(
    loader: ClassLoader,
    className: String,
): ChasmResult<Module, ModuleError> {

    val compiled = when (val loaded = RuntimeLoadedCode().compiled(loader, className)) {
        is ChasmResult.Success -> loaded.result
        is ChasmResult.Error -> return loaded
    }

    return module(compiled)
}
