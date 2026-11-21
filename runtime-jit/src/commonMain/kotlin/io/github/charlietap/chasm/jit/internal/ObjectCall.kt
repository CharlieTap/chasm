package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.internal.ExportedFunction
import io.github.charlietap.chasm.embedding.internal.exportedFunction
import io.github.charlietap.chasm.embedding.internal.exportedTrap
import io.github.charlietap.chasm.embedding.internal.storeDropped
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedReferenceBridge
import io.github.charlietap.chasm.runtime.compiled.gc.PendingExceptions
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceCheck
import io.github.charlietap.chasm.runtime.compiled.gc.enterObjects
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.atomic.AtomicReference
import io.github.charlietap.chasm.runtime.error.InvocationError as RuntimeInvocationError
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class ObjectCall(
    private val store: Store,
    private val instance: Instance,
    private val exported: ExportedFunction,
    private val referenceParameters: IntArray,
    private val parameterChecks: Array<ReferenceCheck>,
    private val exceptions: PendingExceptions,
) {

    val type: FunctionType = exported.function.functionType

    private val slots = maxOf(type.params.types.size, type.results.types.size)

    private val carriesReferences = (type.params.types + type.results.types).any { value -> value is ValueType.Reference }

    private val idle = AtomicReference<ExecutionContext?>()

    fun call(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {

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
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return rejection(stack, references) ?: enter(stack, references)
    }

    private fun rejection(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult.Error<ExecutionError.Rejected>? {

        val message = when {
            storeDropped(store) -> RuntimeInvocationError.StoreDropped.toString()
            instance.instance.deallocated -> RuntimeInvocationError.InvocationOfADeinstantiatedInstance.toString()
            exported.store.functions.getOrNull(exported.address.address) !== exported.function -> RuntimeInvocationError.FunctionLookupFailed(exported.address).toString()
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
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {

        val context = idle.getAndSet(null) ?: try {
            context()
        } catch (exception: InvocationException) {
            return failure(exception.error)
        }

        val error = enterObjects(context, exported.function, stack, references)
        if (error != null) {
            return failure(error)
        }

        idle.set(context)
        return SUCCESS
    }

    private fun context(): ExecutionContext {
        return ExecutionContext(ValueStack(slots + 1, exported.store.maxStackSlots), exported.store, exported.instance, exported.config)
    }

    private fun failure(error: RuntimeInvocationError): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return if (error == RuntimeInvocationError.ThrownException) {
            ChasmResult.Error(ExecutionError.Thrown(exceptions.take()))
        } else {
            ChasmResult.Error(ExecutionError.Trapped(exportedTrap(store, error)))
        }
    }

    companion object {

        private val SUCCESS = ChasmResult.Success(Unit)

        fun prepare(
            store: Store,
            instance: Instance,
            name: String,
            bridge: ManagedReferenceBridge,
        ): ChasmResult<ObjectCall, ExecutionError.Rejected> {

            val exported = when (val lookup = exportedFunction(store, instance, name)) {
                is ChasmResult.Success -> lookup.result
                is ChasmResult.Error -> return ChasmResult.Error(ExecutionError.Rejected(lookup.error.error))
            }

            val owner = bridge.definedType(HostGcType(exported.function.rtt.value))
            val parameters = exported.function.functionType.params.types
            val referenceParameters = parameters.indices.filter { index -> parameters[index] is ValueType.Reference }.toIntArray()
            val parameterChecks = Array(referenceParameters.size) { position ->
                bridge.checks.check((parameters[referenceParameters[position]] as ValueType.Reference).referenceType, owner)
            }

            return ChasmResult.Success(ObjectCall(store, instance, exported, referenceParameters, parameterChecks, bridge))
        }
    }
}

internal fun managedBridge(store: RuntimeStore): ManagedReferenceBridge {
    synchronized(store) {
        return store.referenceBridge as? ManagedReferenceBridge ?: ManagedReferenceBridge(store).also { bridge -> store.referenceBridge = bridge }
    }
}
