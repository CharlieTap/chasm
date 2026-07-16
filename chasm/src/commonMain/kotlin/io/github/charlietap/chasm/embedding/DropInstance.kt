package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.ComponentInstance
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropError
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

fun dropInstance(
    store: Store,
    instance: Instance,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return dropInstance(
        store = store,
        instance = instance,
        instanceDropper = ::ModuleInstanceDropper,
    )
}

internal fun dropInstance(
    store: Store,
    instance: Instance,
    instanceDropper: ModuleInstanceDropper,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return instanceDropper(store.store, instance.instance)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}

fun dropInstance(
    store: Store,
    instance: ComponentInstance,
): ChasmResult<Unit, ChasmError.ExecutionError> = dropInstance(
    store = store,
    instance = instance,
    instanceDropper = ::ComponentInstanceDropper,
)

internal fun dropInstance(
    store: Store,
    instance: ComponentInstance,
    instanceDropper: ComponentInstanceDropper,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    if (store.identity !== instance.store) {
        return Error(ChasmError.ExecutionError(ComponentInvocationError.StoreMismatch.toString()))
    }

    return instanceDropper(store.store, store.componentStore(), instance.root)
        .mapError(ComponentInstanceDropError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
