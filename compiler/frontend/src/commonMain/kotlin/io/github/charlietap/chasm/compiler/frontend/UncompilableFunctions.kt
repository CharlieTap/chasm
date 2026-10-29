package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.onFailure
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.createCompilerContext
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

typealias UncompilableFunctions = (Module, ModuleTypeResolver) -> Map<Int, CompilationError>

fun UncompilableFunctions(
    module: Module,
    types: ModuleTypeResolver = module.typeResolver(),
): Map<Int, CompilationError> = UncompilableFunctions(
    module = module,
    types = types,
    functionCompiler = ::FunctionCompiler,
)

internal inline fun UncompilableFunctions(
    module: Module,
    types: ModuleTypeResolver,
    crossinline functionCompiler: FunctionCompiler,
): Map<Int, CompilationError> {
    val context = createCompilerContext(module, types)
    val workspace = FunctionCompilerWorkspace()
    val failures = LinkedHashMap<Int, CompilationError>()
    module.functions.forEach { function ->
        functionCompiler(context, function, workspace).onFailure { error -> failures[function.idx.idx.toInt()] = error }
    }
    return failures
}
