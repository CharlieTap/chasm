package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.internal.ClassInputOpener
import io.github.charlietap.chasm.compiled.internal.LoadedModules
import io.github.charlietap.chasm.compiled.internal.ParentClasses
import io.github.charlietap.chasm.compiled.internal.RuntimeLoadedCode
import io.github.charlietap.chasm.compiled.internal.compiledModuleReader
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module

fun module(
    input: ClassInput,
    className: String,
): ChasmResult<Module, ModuleError> {

    val parent = CompiledModule::class.java.classLoader
    val modules = LoadedModules(ParentClasses(parent), RuntimeLoadedCode(), compiledModuleReader())
    modules.shadowed(className)?.let { error -> return error }

    val opened = when (val result = ClassInputOpener(parent).open(input, className)) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return result
    }

    return modules.module(opened, className)
}
