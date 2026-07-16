package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

/**
 * Executes with parameters and results sharing frame slots from zero. Implementations preserve
 * the stack shape, frame pointer, and all slots outside that function interface.
 */
fun interface StackFunctionBody {

    operator fun invoke(
        vstack: ValueStack,
        cstack: ControlStack,
        store: Store,
        context: ExecutionContext,
    )
}
