package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.createCompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

fun ModuleCompiler(
    store: Store,
    module: Module,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
    types: ModuleTypeResolver = ModuleTypeResolver(module),
    diagnostics: CompilerDiagnostics? = null,
): Result<Unit, ModuleTrapError> = binding {
    val context = createCompilerContext(
        module = module,
        types = types,
        store = store,
        instance = instance,
        runtimeTypes = runtimeTypes,
        diagnostics = diagnostics,
    )
    val workspace = FunctionCompilerWorkspace()

    for (functionIndex in module.functions.indices) {
        val function = module.functions[functionIndex]
        val functionInstance = context.functions[function.idx.toInt()] as FunctionInstance.WasmFunction
        val entryIp = store.program.size
        val compiled = FunctionCompiler(context, function, store.program, workspace).bind()

        functionInstance.callPlan.install(
            entryIp = entryIp,
            frameSlots = compiled.frameSlots,
        )
        functionInstance.function = compiled
    }
}
