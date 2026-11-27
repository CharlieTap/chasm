package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
sealed interface Index {

    val idx: UInt

    @JvmInline
    value class ComponentTypeIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentFunctionIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentValueIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentModuleIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentModuleInstanceIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentIndex(override val idx: UInt) : Index

    @JvmInline
    value class ComponentInstanceIndex(override val idx: UInt) : Index
}
