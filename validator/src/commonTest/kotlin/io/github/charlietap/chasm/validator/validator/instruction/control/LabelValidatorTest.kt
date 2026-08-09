package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.ast.module.localIndex
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.type.LocalType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.ext.initializeLocal
import kotlin.test.Test
import kotlin.test.assertEquals

class LabelValidatorTest {

    @Test
    fun `restores local initialization when entering an else branch`() {
        val context = ModuleValidationContext(ModuleConfig(), module())
        val localType = LocalType(
            status = InitializationStatus.UNSET,
            type = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Func)),
        )
        val label = Label(
            kind = LabelKind.IfThen,
            inputs = ResultType(emptyList()),
            outputs = ResultType(emptyList()),
            operandsDepth = 0,
            localChangesDepth = 0,
            unreachable = false,
        )
        context.locals += localType
        context.labels.push(label)
        assertEquals(Ok(localType), context.initializeLocal(localIndex()))

        val result = TransitionElse(context, label)

        assertEquals(Ok(Unit), result)
        assertEquals(InitializationStatus.UNSET, localType.status)
        assertEquals(LabelKind.IfElse, label.kind)
        assertEquals(emptyList(), context.localChanges)
    }

    @Test
    fun `records repeated local initialization once`() {
        val context = context()
        val localType = localType()
        context.locals += localType

        assertEquals(Ok(localType), context.initializeLocal(localIndex()))
        assertEquals(Ok(localType), context.initializeLocal(localIndex()))

        assertEquals(listOf(0), context.localChanges)
    }

    @Test
    fun `an inner label preserves a local initialized by its parent`() {
        val context = context()
        val localType = localType()
        val outerLabel = label(localChangesDepth = 0)
        context.locals += localType
        context.labels.push(outerLabel)
        assertEquals(Ok(localType), context.initializeLocal(localIndex()))

        val innerLabel = label(localChangesDepth = context.localChanges.size)
        context.labels.push(innerLabel)
        assertEquals(Ok(Unit), FinishLabel(context, innerLabel, pushOutputs = false))

        assertEquals(InitializationStatus.SET, localType.status)
        assertEquals(listOf(0), context.localChanges)

        assertEquals(Ok(Unit), FinishLabel(context, outerLabel, pushOutputs = false))
        assertEquals(InitializationStatus.UNSET, localType.status)
        assertEquals(emptyList(), context.localChanges)
    }

    private fun context() = ModuleValidationContext(ModuleConfig(), module())

    private fun localType() = LocalType(
        status = InitializationStatus.UNSET,
        type = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Func)),
    )

    private fun label(
        localChangesDepth: Int,
    ) = Label(
        kind = LabelKind.Block,
        inputs = ResultType(emptyList()),
        outputs = ResultType(emptyList()),
        operandsDepth = 0,
        localChangesDepth = localChangesDepth,
        unreachable = false,
    )
}
