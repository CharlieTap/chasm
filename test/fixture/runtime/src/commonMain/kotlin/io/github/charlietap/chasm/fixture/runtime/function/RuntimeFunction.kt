package io.github.charlietap.chasm.fixture.runtime.function

class RuntimeFunction(
    locals: LongArray = longArrayOf(),
    val body: RuntimeExpression = runtimeExpression(),
    val frameSlots: Int = 0,
) {
    val locals = locals.copyOf()
}

fun runtimeFunction(
    locals: LongArray = longArrayOf(),
    body: RuntimeExpression = runtimeExpression(),
    frameSlots: Int = 0,
) = RuntimeFunction(
    locals = locals,
    body = body,
    frameSlots = frameSlots,
)
