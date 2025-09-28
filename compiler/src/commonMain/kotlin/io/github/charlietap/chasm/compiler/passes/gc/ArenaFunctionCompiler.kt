package io.github.charlietap.chasm.compiler.passes.gc

import io.github.charlietap.chasm.compiler.ext.containsGcInstructions
import io.github.charlietap.chasm.compiler.ext.exportedFunctions
import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Module

internal typealias ArenaFunctionCompiler = (PassContext, Module) -> Module

internal fun ArenaFunctionCompiler(
    context: PassContext,
    module: Module,
): Module {
    return if (module.containsGcInstructions()) {

        val exported = module.exportedFunctions

        module.copy(
            functions = module.functions.map { function ->
                if (function.idx in exported) {
                    function.copy(
                        body = Expression(
                            function.body.instructions + listOf(AdminInstruction.Pause),
                        ),
                    )
                } else {
                    function
                }
            },
        )
    } else {
        module
    }
}
