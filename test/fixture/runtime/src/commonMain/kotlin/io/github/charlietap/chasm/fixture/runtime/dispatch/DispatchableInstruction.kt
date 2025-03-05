package io.github.charlietap.chasm.fixture.runtime.dispatch

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext

fun dispatchableInstruction(
    executor: ((ExecutionContext) -> Result<Unit, InvocationError>)? = null,
): DispatchableInstruction = executor?.let {
    { context ->
        executor(context)
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction {
    override fun invoke(p1: ExecutionContext) {
        return
    }
}
