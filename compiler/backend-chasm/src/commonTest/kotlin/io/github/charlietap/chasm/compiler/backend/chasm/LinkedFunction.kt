package io.github.charlietap.chasm.compiler.backend.chasm

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.compiler.frontend.DefinedFunctionCompiler
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

internal class LinkedFunction(
    val program: Program,
    val function: CirFunction,
    val entryIp: Int,
    val frameSlots: Int,
)

internal fun linkFunction(
    module: Module,
    definedFunctionIndex: Int = 0,
    store: Store = store(),
    runtimeTypes: RuntimeTypeMap = store.heap.registerRuntimeTypes(module.definedTypes),
    instance: ModuleInstance = moduleInstance(runtimeTypes = runtimeTypes),
    functions: Array<FunctionInstance> = definedFunctionInstances(module, runtimeTypes, instance),
    program: Program = Program(),
): LinkedFunction {
    val function = DefinedFunctionCompiler(module).compile(definedFunctionIndex).unwrap()
    val context = LinkContext(
        instance = instance,
        runtimeTypes = runtimeTypes,
        functionAddresses = IntArray(functions.size) { index -> index },
        functions = functions,
        tables = emptyArray(),
        memories = emptyArray(),
        globals = emptyArray(),
        elements = emptyArray(),
        data = emptyArray(),
        definedTypes = module.definedTypes,
        fuel = null,
        interrupt = null,
    )
    ProgramFunctionLinker(context, function, program)
    return LinkedFunction(program, function, context.entryIp, context.frameSlots)
}

private fun definedFunctionInstances(
    module: Module,
    runtimeTypes: RuntimeTypeMap,
    instance: ModuleInstance,
): Array<FunctionInstance> {
    val types = module.typeResolver()
    return Array(module.functions.size) { index ->
        val typeIndex = module.functions[index].typeIndex
        wasmFunctionInstance(
            rtt = runtimeTypes[typeIndex.toInt()],
            functionType = types.functionType(typeIndex),
            module = instance,
        )
    }
}
