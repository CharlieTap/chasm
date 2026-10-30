@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.chasm.link.createLinkContext
import io.github.charlietap.chasm.executor.invoker.thread.Interpreter
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

typealias ModuleLinker = (Store, ModuleInstance, Module, CirModule) -> Unit

fun ModuleLinker(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    code: CirModule,
) = ModuleLinker(
    store = store,
    instance = instance,
    module = module,
    code = code,
    layoutBuilder = ::ModuleLayoutBuilder,
    functionLinker = ::FunctionLinker,
    programInstaller = ::ModuleProgramInstaller,
)

internal inline fun ModuleLinker(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    code: CirModule,
    crossinline layoutBuilder: ModuleLayoutBuilder,
    noinline functionLinker: FunctionLinker,
    crossinline programInstaller: ModuleProgramInstaller,
) {
    val context = createLinkContext(store, instance, module.definedTypes)
    val plan = layoutBuilder(context, code, store.program.size)
    for (index in code.functions.indices) {
        plan.link(context, index, functionLinker)
    }
    programInstaller(store.program, context, plan, listOf(context.pendingWasmCalls))
    store.interpreter = Interpreter
}
