package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.createCompilerContext
import io.github.charlietap.chasm.declarations.ModuleTypeResolver

class DefinedFunctionCompiler(
    private val module: Module,
    types: ModuleTypeResolver = module.typeResolver(),
) {
    private val context = createCompilerContext(module, types)
    private val workspace = FunctionCompilerWorkspace()

    fun compile(
        definedFunctionIndex: Int,
        recordProvenance: Boolean = false,
    ): Result<CirFunction, CompilationError> =
        FunctionCompiler(context, module.functions[definedFunctionIndex], workspace, recordProvenance)
}
