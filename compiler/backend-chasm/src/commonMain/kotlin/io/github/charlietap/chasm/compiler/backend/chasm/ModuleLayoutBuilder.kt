package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext

internal typealias ModuleLayoutBuilder = (LinkContext, CirModule, Int) -> ModuleLinkPlan

internal fun ModuleLayoutBuilder(
    context: LinkContext,
    module: CirModule,
    baseIp: Int,
): ModuleLinkPlan {
    val functions = module.functions
    val entryIps = IntArray(functions.size + 1)
    var ip = baseIp
    for (index in functions.indices) {
        entryIps[index] = ip
        ip += context.programSize(functions[index])
    }
    entryIps[functions.size] = ip
    return ModuleLinkPlan(functions, baseIp, entryIps, arrayOfNulls(ip - baseIp))
}
