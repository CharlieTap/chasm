package io.github.charlietap.chasm.runtime.component.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScope
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

sealed interface RuntimeComponentHostFunction {

    data class Linked(
        val root: ComponentRootAddress,
        val function: PreparedComponentFunctionIndex,
    ) : RuntimeComponentHostFunction

    fun interface Dynamic : RuntimeComponentHostFunction {

        operator fun invoke(
            context: RuntimeComponentHostFunctionContext,
            arguments: List<ComponentValue>,
        ): Result<List<ComponentValue>, ComponentInvocationError>
    }

    fun interface Prepared : RuntimeComponentHostFunction {

        operator fun invoke(
            context: RuntimeComponentHostFunctionContext,
            arguments: LongArray,
            argumentCount: Int,
            results: LongArray,
        ): Result<Int, ComponentInvocationError>
    }
}

class RuntimeComponentHostFunctionContext internal constructor(
    val scope: ComponentCallScope,
) {

    lateinit var store: StoreIdentity
        private set

    var root: ComponentRootAddress = ComponentRootAddress(UNINITIALIZED_ROOT)
        private set

    internal fun configure(
        store: StoreIdentity,
        root: ComponentRootAddress,
    ) {
        this.store = store
        this.root = root
    }
}

private const val UNINITIALIZED_ROOT = -1
