package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PauseIfDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

internal fun FunctionCompilationContext.emitPause() {
    program.append(PauseDispatcher(AdminInstruction.Pause))
}

internal fun FunctionCompilationContext.emitPauseIf() {
    program.append(PauseIfDispatcher(AdminInstruction.PauseIf))
}
