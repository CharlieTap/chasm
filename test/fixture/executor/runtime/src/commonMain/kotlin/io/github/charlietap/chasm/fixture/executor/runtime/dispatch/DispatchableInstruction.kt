package io.github.charlietap.chasm.fixture.executor.runtime.dispatch

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext

fun dispatchableInstruction(
    executor: ((ExecutionContext) -> Result<Unit, InvocationError>)? = null,
): DispatchableInstruction = executor?.let {
    { context ->
        executor(context)
    }
} ?: NoOpDispatchableInstruction

private object NoOpDispatchableInstruction : DispatchableInstruction {
    override fun invoke(p1: ExecutionContext): Result<Unit, InvocationError> {
        return Ok(Unit)
    }
}
