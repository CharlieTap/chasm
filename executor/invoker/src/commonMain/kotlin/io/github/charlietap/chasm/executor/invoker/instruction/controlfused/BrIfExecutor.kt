// package io.github.charlietap.chasm.executor.invoker.instruction.controlfused
//
// import io.github.charlietap.chasm.executor.invoker.instruction.control.BreakExecutor
// import io.github.charlietap.chasm.runtime.execution.ExecutionContext
// import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
// import io.github.charlietap.chasm.runtime.stack.ControlStack
// import io.github.charlietap.chasm.runtime.stack.ValueStack
// import io.github.charlietap.chasm.runtime.store.Store
//
// internal fun BrIfExecutor(
//    vstack: ValueStack,
//    cstack: ControlStack,
//    store: Store,
//    context: ExecutionContext,
//    instruction: FusedControlInstruction.BrIf,
// ) = BrIfExecutor(
//    vstack = vstack,
//    cstack = cstack,
//    store = store,
//    context = context,
//    instruction = instruction,
//    breakExecutor = ::BreakExecutor,
// )
//
// internal inline fun BrIfExecutor(
//    vstack: ValueStack,
//    cstack: ControlStack,
//    store: Store,
//    context: ExecutionContext,
//    instruction: FusedControlInstruction.BrIf,
//    crossinline breakExecutor: BreakExecutor,
// ) {
//    val shouldBreak = instruction.operand(vstack) != 0L
//
//    if (shouldBreak) {
//        breakExecutor(cstack, vstack, instruction.labelIndex)
//    }
// }
