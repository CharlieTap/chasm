package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.createCompilerContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LinkWasmCallDispatchers
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.function.classifyLocalInitialization
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
): Result<Unit, ModuleTrapError> {
    val firstModuleIp = store.program.size
    val result = binding<Unit, ModuleTrapError> {
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

            val callStrategy = functionInstance.callStrategy
            callStrategy.frameSlots = compiled.frameSlots
            callStrategy.localInitialization = classifyLocalInitialization(compiled.localInitialValues)
            callStrategy.entryIp = entryIp
        }
        val instructionObserver = diagnostics?.instructionObserver
        if (instructionObserver == null) {
            LinkWasmCallDispatchers(store.program, firstModuleIp)
        } else {
            LinkWasmCallDispatchers(store.program, firstModuleIp, instructionObserver::onInstruction)
        }
    }
    if (result.isErr) {
        store.program.truncate(firstModuleIp)
        for (function in module.functions) {
            val address = instance.functionAddresses[function.idx.toInt()]
            val compiled = store.functions[address.address] as FunctionInstance.WasmFunction
            compiled.callStrategy.entryIp = -1
        }
    }
    return result
}
