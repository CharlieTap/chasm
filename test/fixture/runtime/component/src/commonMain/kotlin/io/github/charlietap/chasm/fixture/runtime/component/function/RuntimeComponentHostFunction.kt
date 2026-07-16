package io.github.charlietap.chasm.fixture.runtime.component.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

fun runtimeComponentHostFunction(
    results: List<ComponentValue> = emptyList(),
): RuntimeComponentHostFunction = RuntimeComponentHostFunction.Dynamic { _, _ -> Ok(results) }
