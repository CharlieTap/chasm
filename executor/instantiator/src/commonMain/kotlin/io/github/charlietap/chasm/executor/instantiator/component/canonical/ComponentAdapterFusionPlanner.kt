package io.github.charlietap.chasm.executor.instantiator.component.canonical

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LiftParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LiftResultPassing
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing

internal fun CanFuseComponentAdapter(
    config: RuntimeConfig,
    layoutCompiler: Memory32LayoutCompiler,
    lower: LinearMemoryLowerPlan,
    lift: LinearMemoryLiftPlan,
): Boolean {
    if (!config.bytecodeFusion) return false
    if (
        lower.parameterPassing != LowerParameterPassing.Direct ||
        lower.resultPassing != LowerResultPassing.Direct ||
        lift.parameterPassing != LiftParameterPassing.Direct ||
        lift.resultPassing != LiftResultPassing.Direct
    ) {
        return false
    }
    if (!lower.parameterTuple.sameLayouts(lift.parameterTuple)) return false
    if (!lower.resultTuple.sameLayouts(lift.resultTuple)) return false

    return lower.parameterTuple.supportsFusion(layoutCompiler) && lower.resultTuple.supportsFusion(layoutCompiler)
}

private fun CanonicalValueTupleLayout.sameLayouts(other: CanonicalValueTupleLayout): Boolean =
    flatCount == other.flatCount && layouts.contentEquals(other.layouts)

private fun CanonicalValueTupleLayout.supportsFusion(layoutCompiler: Memory32LayoutCompiler): Boolean =
    layouts.all { layout -> layout.supportsFusion(layoutCompiler) }

private fun Int.supportsFusion(layoutCompiler: Memory32LayoutCompiler): Boolean {
    val layout = layoutCompiler[this]
    return when (layout.kind) {
        CanonicalLayoutKind.Bool,
        CanonicalLayoutKind.S8,
        CanonicalLayoutKind.U8,
        CanonicalLayoutKind.S16,
        CanonicalLayoutKind.U16,
        CanonicalLayoutKind.S32,
        CanonicalLayoutKind.U32,
        CanonicalLayoutKind.S64,
        CanonicalLayoutKind.U64,
        CanonicalLayoutKind.F32,
        CanonicalLayoutKind.F64,
        CanonicalLayoutKind.Char,
        CanonicalLayoutKind.Flags,
        CanonicalLayoutKind.Enum,
        -> true
        CanonicalLayoutKind.Record,
        CanonicalLayoutKind.Tuple,
        -> layout.children.all { child -> child.supportsFusion(layoutCompiler) }
        CanonicalLayoutKind.String,
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.List,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> false
    }
}
