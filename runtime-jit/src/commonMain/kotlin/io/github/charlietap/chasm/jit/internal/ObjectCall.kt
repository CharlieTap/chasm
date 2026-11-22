package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.PendingExceptions
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceCheck
import io.github.charlietap.chasm.runtime.compiled.gc.enterObjects
import io.github.charlietap.chasm.runtime.compiled.gc.takeException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.atomic.AtomicReference
import io.github.charlietap.chasm.runtime.error.InvocationError as RuntimeInvocationError
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class ObjectCall(
    private val store: StoreRuntime,
    private val runtime: RuntimeStore,
    private val instance: ModuleInstance,
    private val config: RuntimeConfig,
    private val address: Address.Function,
    private val function: FunctionInstance,
    private val referenceParameters: IntArray,
    private val parameterChecks: Array<ReferenceCheck>,
    private val pending: PendingExceptions,
) {

    val type: FunctionType = function.functionType

    private val slots = maxOf(type.params.types.size, type.results.types.size)

    private val carriesReferences = type.params.types.any { value -> value is ValueType.Reference } ||
        type.results.types.any { value -> value is ValueType.Reference }

    private val idle = AtomicReference<ExecutionContext?>()

    fun call(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {

        val rejection = rejection(stack, references)
        if (rejection != null) {
            return rejection
        }

        for (position in referenceParameters.indices) {
            val index = referenceParameters[position]
            if (!parameterChecks[position].accepts(references[index])) {
                return ChasmResult.Error(ExecutionError.Rejected("argument $index does not match its type"))
            }
        }

        return enter(stack, references)
    }

    fun unsafeCall(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {
        return rejection(stack, references) ?: enter(stack, references)
    }

    private fun rejection(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult.Error<ExecutionError.Rejected>? {

        val message = when {
            store.dropped -> RuntimeInvocationError.StoreDropped.toString()
            instance.deallocated -> RuntimeInvocationError.InvocationOfADeinstantiatedInstance.toString()
            runtime.functions.getOrNull(address.address) !== function -> RuntimeInvocationError.FunctionLookupFailed(address).toString()
            else -> null
        }
        if (message != null) {
            return ChasmResult.Error(ExecutionError.Rejected(message))
        }

        require(stack.size >= slots) { "the stack holds ${stack.size} slots but the call needs $slots" }
        require(!carriesReferences || references.size >= slots) { "the references hold ${references.size} slots but the call needs $slots" }

        return null
    }

    private fun enter(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError> {

        val context = idle.getAndSet(null) ?: try {
            context()
        } catch (exception: InvocationException) {
            return trapped(exception.error)
        }

        val error = enterObjects(context, function, stack, references, pending)
        if (error == RuntimeInvocationError.ThrownException) {
            return ChasmResult.Error(ExecutionError.Thrown(takeException(context)))
        }
        if (error != null) {
            return trapped(error)
        }

        idle.set(context)
        return SUCCESS
    }

    private fun context(): ExecutionContext {

        val stackSlots = if (function is FunctionInstance.HostFunction) slots else slots + 1

        return ExecutionContext(ValueStack(stackSlots, runtime.maxStackSlots), runtime, instance, config)
    }

    private fun trapped(error: RuntimeInvocationError): ChasmResult<Unit, InvocationError> {
        return ChasmResult.Error(ExecutionError.Trapped(store.trap(error)))
    }

    companion object {

        private val SUCCESS = ChasmResult.Success(Unit)

        private val NO_PARAMETER_CHECKS = emptyArray<ReferenceCheck>()

        fun prepare(
            store: StoreRuntime,
            instance: ModuleInstance,
            config: RuntimeConfig,
            address: Address.Function,
            managedStore: ManagedStore,
        ): ChasmResult<ObjectCall, ExecutionError.Rejected> {

            if (instance.deallocated) {
                return ChasmResult.Error(ExecutionError.Rejected(RuntimeInvocationError.InvocationOfADeinstantiatedInstance.toString()))
            }

            val runtime = store.liveStore()
            val function = try {
                runtime.function(address)
            } catch (exception: InvocationException) {
                return ChasmResult.Error(ExecutionError.Rejected(exception.error.toString()))
            }

            val parameters = function.functionType.params.types
            val referenceParameters = parameters.indices.filter { index -> parameters[index] is ValueType.Reference }.toIntArray()
            if (referenceParameters.isEmpty()) {
                val call = ObjectCall(store, runtime, instance, config, address, function, referenceParameters, NO_PARAMETER_CHECKS, managedStore.pending)
                return ChasmResult.Success(call)
            }

            val owner = managedStore.definedType(HostGcType(function.rtt.value))

            val parameterChecks = Array(referenceParameters.size) { position ->
                managedStore.checks.check((parameters[referenceParameters[position]] as ValueType.Reference).referenceType, owner)
            }

            return ChasmResult.Success(ObjectCall(store, runtime, instance, config, address, function, referenceParameters, parameterChecks, managedStore.pending))
        }
    }
}

internal fun managedStore(store: RuntimeStore): ManagedStore {
    synchronized(store) {
        return store.referenceStorage as? ManagedStore ?: ManagedStore(store, GeneratedShapeAccessors()).also { managedStore -> store.referenceStorage = managedStore }
    }
}
