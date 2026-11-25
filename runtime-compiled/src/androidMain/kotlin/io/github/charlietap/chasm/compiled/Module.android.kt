package io.github.charlietap.chasm.compiled

import android.os.Build
import io.github.charlietap.chasm.compiled.internal.DexInputOpener
import io.github.charlietap.chasm.compiled.internal.LoadedModules
import io.github.charlietap.chasm.compiled.internal.ParentClasses
import io.github.charlietap.chasm.compiled.internal.RuntimeLoadedCode
import io.github.charlietap.chasm.compiled.internal.compiledModuleReader
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module

fun module(
    input: DexInput,
    className: String,
): ChasmResult<Module, ModuleError> {

    val parent = CompiledModule::class.java.classLoader
    val modules = LoadedModules(ParentClasses(parent), RuntimeLoadedCode(), compiledModuleReader())
    val shadowed = modules.shadowed(className)
    if (shadowed != null) {
        return shadowed
    }

    val opened = when (val result = DexInputOpener(parent, Build.VERSION.SDK_INT).open(input)) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return result
    }

    return modules.module(opened, className)
}
