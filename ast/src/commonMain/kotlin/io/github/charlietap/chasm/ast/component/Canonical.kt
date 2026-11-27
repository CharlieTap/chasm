package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Index.ComponentFunctionIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex as ModuleMemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex as ModuleTableIndex
import io.github.charlietap.chasm.type.ValueType as CoreValueType

@InternalChasmApi
sealed interface CanonicalDefinition {

    data class Lift(
        val functionIndex: ModuleFunctionIndex,
        val options: List<CanonicalOption>,
        val typeIndex: ComponentTypeIndex,
    ) : CanonicalDefinition

    data class Lower(
        val functionIndex: ComponentFunctionIndex,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data class ResourceNew(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class ResourceDrop(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class ResourceRep(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data object BackpressureSet : CanonicalDefinition

    data object BackpressureInc : CanonicalDefinition

    data object BackpressureDec : CanonicalDefinition

    data class TaskReturn(
        val result: ValueType?,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data object TaskCancel : CanonicalDefinition

    data class ContextGet(
        val type: CoreValueType,
        val index: UInt,
    ) : CanonicalDefinition

    data class ContextSet(
        val type: CoreValueType,
        val index: UInt,
    ) : CanonicalDefinition

    data class SubtaskCancel(val async: Boolean) : CanonicalDefinition

    data object SubtaskDrop : CanonicalDefinition

    data class StreamNew(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class StreamRead(
        val typeIndex: ComponentTypeIndex,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data class StreamWrite(
        val typeIndex: ComponentTypeIndex,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data class StreamCancelRead(
        val typeIndex: ComponentTypeIndex,
        val async: Boolean,
    ) : CanonicalDefinition

    data class StreamCancelWrite(
        val typeIndex: ComponentTypeIndex,
        val async: Boolean,
    ) : CanonicalDefinition

    data class StreamDropReadable(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class StreamDropWritable(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class FutureNew(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class FutureRead(
        val typeIndex: ComponentTypeIndex,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data class FutureWrite(
        val typeIndex: ComponentTypeIndex,
        val options: List<CanonicalOption>,
    ) : CanonicalDefinition

    data class FutureCancelRead(
        val typeIndex: ComponentTypeIndex,
        val async: Boolean,
    ) : CanonicalDefinition

    data class FutureCancelWrite(
        val typeIndex: ComponentTypeIndex,
        val async: Boolean,
    ) : CanonicalDefinition

    data class FutureDropReadable(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class FutureDropWritable(val typeIndex: ComponentTypeIndex) : CanonicalDefinition

    data class ErrorContextNew(val options: List<CanonicalOption>) : CanonicalDefinition

    data class ErrorContextDebugMessage(val options: List<CanonicalOption>) : CanonicalDefinition

    data object ErrorContextDrop : CanonicalDefinition

    data object WaitableSetNew : CanonicalDefinition

    data class WaitableSetWait(
        val cancellable: Boolean,
        val memoryIndex: ModuleMemoryIndex,
    ) : CanonicalDefinition

    data class WaitableSetPoll(
        val cancellable: Boolean,
        val memoryIndex: ModuleMemoryIndex,
    ) : CanonicalDefinition

    data object WaitableSetDrop : CanonicalDefinition

    data object WaitableJoin : CanonicalDefinition

    data object ThreadIndex : CanonicalDefinition

    data class ThreadNewIndirect(
        val typeIndex: ComponentTypeIndex,
        val tableIndex: ModuleTableIndex,
    ) : CanonicalDefinition

    data object ThreadResumeLater : CanonicalDefinition

    data class ThreadSuspend(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadYield(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadSuspendThenResume(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadYieldThenResume(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadSuspendThenPromote(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadYieldThenPromote(val cancellable: Boolean) : CanonicalDefinition

    data class ThreadSpawnRef(
        val shared: Boolean,
        val typeIndex: ComponentTypeIndex,
    ) : CanonicalDefinition

    data class ThreadSpawnIndirect(
        val shared: Boolean,
        val typeIndex: ComponentTypeIndex,
        val tableIndex: ModuleTableIndex,
    ) : CanonicalDefinition

    data class ThreadAvailableParallelism(val shared: Boolean) : CanonicalDefinition
}

@InternalChasmApi
sealed interface CanonicalOption {

    data class StringEncoding(val encoding: ComponentStringEncoding) : CanonicalOption

    data class Memory(val index: ModuleMemoryIndex) : CanonicalOption

    data class Realloc(val index: ModuleFunctionIndex) : CanonicalOption

    data class PostReturn(val index: ModuleFunctionIndex) : CanonicalOption

    data object Async : CanonicalOption

    data class Callback(val index: ModuleFunctionIndex) : CanonicalOption
}

@InternalChasmApi
enum class ComponentStringEncoding {
    Utf8,
    Utf16,
    Latin1Utf16,
}
