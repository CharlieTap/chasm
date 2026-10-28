package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.AnyConvertExternSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDefaultIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDefaultSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewFixedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ExternConvertAnySDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetSignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetUnsignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.LocalSetStructGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.RefCastStructGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.RefI31IDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.RefI31SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetSignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetStructGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetUnsignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructNewDefaultSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructNewSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructSetIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructSetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.ElemDropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopyIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopyIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopyIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopyIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopySiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopySisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopySsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopySssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableFillIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableFillIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableFillSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableFillSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGetIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGrowIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGrowSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSetSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSetSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSizeSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalSetIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalSetSDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal fun linkInstructions12(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    576 -> {
        val i = instruction as AggregateInstruction.ArrayNewIs
        ArrayNewIsDispatcher(i.size, i.valueSlot, i.destinationSlot, context.runtimeTypes[i.type.index])
    }
    577 -> {
        val i = instruction as AggregateInstruction.ArrayNewSi
        ArrayNewSiDispatcher(i.sizeSlot, i.value, i.destinationSlot, context.runtimeTypes[i.type.index])
    }
    578 -> {
        val i = instruction as AggregateInstruction.ArrayNewSs
        ArrayNewSsDispatcher(i.sizeSlot, i.valueSlot, i.destinationSlot, context.runtimeTypes[i.type.index])
    }
    579 -> {
        val i = instruction as AggregateInstruction.ArrayNewDefaultI
        ArrayNewDefaultIDispatcher(i.size, i.destinationSlot, context.runtimeTypes[i.type.index], context.arrayDefault(i.type.index))
    }
    580 -> {
        val i = instruction as AggregateInstruction.ArrayNewDefaultS
        ArrayNewDefaultSDispatcher(i.sizeSlot, i.destinationSlot, context.runtimeTypes[i.type.index], context.arrayDefault(i.type.index))
    }
    581 -> {
        val i = instruction as AggregateInstruction.ArrayNewDataIi
        ArrayNewDataIiDispatcher(i.sourceOffset, i.arrayLength, i.destinationSlot, context.runtimeTypes[i.type.index], context.data[i.data.index], i.fieldWidthInBytes)
    }
    582 -> {
        val i = instruction as AggregateInstruction.ArrayNewDataIs
        ArrayNewDataIsDispatcher(i.sourceOffset, i.arrayLengthSlot, i.destinationSlot, context.runtimeTypes[i.type.index], context.data[i.data.index], i.fieldWidthInBytes)
    }
    583 -> {
        val i = instruction as AggregateInstruction.ArrayNewDataSi
        ArrayNewDataSiDispatcher(i.sourceOffsetSlot, i.arrayLength, i.destinationSlot, context.runtimeTypes[i.type.index], context.data[i.data.index], i.fieldWidthInBytes)
    }
    584 -> {
        val i = instruction as AggregateInstruction.ArrayNewDataSs
        ArrayNewDataSsDispatcher(i.sourceOffsetSlot, i.arrayLengthSlot, i.destinationSlot, context.runtimeTypes[i.type.index], context.data[i.data.index], i.fieldWidthInBytes)
    }
    585 -> {
        val i = instruction as AggregateInstruction.ArrayNewElementIi
        ArrayNewElementIiDispatcher(i.sourceOffset, i.arrayLength, i.destinationSlot, context.runtimeTypes[i.type.index], context.elements[i.element.index])
    }
    586 -> {
        val i = instruction as AggregateInstruction.ArrayNewElementIs
        ArrayNewElementIsDispatcher(i.sourceOffset, i.arrayLengthSlot, i.destinationSlot, context.runtimeTypes[i.type.index], context.elements[i.element.index])
    }
    587 -> {
        val i = instruction as AggregateInstruction.ArrayNewElementSi
        ArrayNewElementSiDispatcher(i.sourceOffsetSlot, i.arrayLength, i.destinationSlot, context.runtimeTypes[i.type.index], context.elements[i.element.index])
    }
    588 -> {
        val i = instruction as AggregateInstruction.ArrayNewElementSs
        ArrayNewElementSsDispatcher(i.sourceOffsetSlot, i.arrayLengthSlot, i.destinationSlot, context.runtimeTypes[i.type.index], context.elements[i.element.index])
    }
    589 -> {
        val i = instruction as AggregateInstruction.ArrayNewFixedS
        ArrayNewFixedSDispatcher(i.firstElementSlot, i.destinationSlot, context.runtimeTypes[i.type.index], i.size)
    }
    590 -> {
        val i = instruction as AggregateInstruction.ArraySetIi
        ArraySetIiDispatcher(i.value, i.field, i.addressSlot)
    }
    591 -> {
        val i = instruction as AggregateInstruction.ArraySetIs
        ArraySetIsDispatcher(i.value, i.fieldSlot, i.addressSlot)
    }
    592 -> {
        val i = instruction as AggregateInstruction.ArraySetSi
        ArraySetSiDispatcher(i.valueSlot, i.field, i.addressSlot)
    }
    593 -> {
        val i = instruction as AggregateInstruction.ArraySetSs
        ArraySetSsDispatcher(i.valueSlot, i.fieldSlot, i.addressSlot)
    }
    594 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataIii
        ArrayInitDataIiiDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffset, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    595 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataIis
        ArrayInitDataIisDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffsetSlot, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    596 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataIsi
        ArrayInitDataIsiDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffset, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    597 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataIss
        ArrayInitDataIssDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffsetSlot, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    598 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataSii
        ArrayInitDataSiiDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffset, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    599 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataSis
        ArrayInitDataSisDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffsetSlot, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    600 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataSsi
        ArrayInitDataSsiDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffset, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    601 -> {
        val i = instruction as AggregateInstruction.ArrayInitDataSss
        ArrayInitDataSssDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffsetSlot, i.addressSlot, context.data[i.data.index], i.fieldWidthInBytes)
    }
    602 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementIii
        ArrayInitElementIiiDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffset, i.addressSlot, context.elements[i.element.index])
    }
    603 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementIis
        ArrayInitElementIisDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffsetSlot, i.addressSlot, context.elements[i.element.index])
    }
    604 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementIsi
        ArrayInitElementIsiDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffset, i.addressSlot, context.elements[i.element.index])
    }
    605 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementIss
        ArrayInitElementIssDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffsetSlot, i.addressSlot, context.elements[i.element.index])
    }
    606 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementSii
        ArrayInitElementSiiDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffset, i.addressSlot, context.elements[i.element.index])
    }
    607 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementSis
        ArrayInitElementSisDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffsetSlot, i.addressSlot, context.elements[i.element.index])
    }
    608 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementSsi
        ArrayInitElementSsiDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffset, i.addressSlot, context.elements[i.element.index])
    }
    609 -> {
        val i = instruction as AggregateInstruction.ArrayInitElementSss
        ArrayInitElementSssDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffsetSlot, i.addressSlot, context.elements[i.element.index])
    }
    610 -> {
        val i = instruction as AggregateInstruction.RefI31I
        RefI31IDispatcher(i.value, i.destinationSlot)
    }
    611 -> {
        val i = instruction as AggregateInstruction.RefI31S
        RefI31SDispatcher(i.valueSlot, i.destinationSlot)
    }
    612 -> {
        val i = instruction as AggregateInstruction.I31GetSignedS
        I31GetSignedSDispatcher(i.valueSlot, i.destinationSlot)
    }
    613 -> {
        val i = instruction as AggregateInstruction.I31GetUnsignedS
        I31GetUnsignedSDispatcher(i.valueSlot, i.destinationSlot)
    }
    614 -> {
        val i = instruction as AggregateInstruction.AnyConvertExternS
        AnyConvertExternSDispatcher(i.valueSlot, i.destinationSlot)
    }
    615 -> {
        val i = instruction as AggregateInstruction.ExternConvertAnyS
        ExternConvertAnySDispatcher(i.valueSlot, i.destinationSlot)
    }
    616 -> {
        val i = instruction as AggregateInstruction.StructGetS
        StructGetSDispatcher(i.addressSlot, i.destinationSlot, i.fieldIndex)
    }
    617 -> {
        val i = instruction as AggregateInstruction.StructGetSignedS
        StructGetSignedSDispatcher(i.addressSlot, i.destinationSlot, i.fieldIndex, i.packedType.toRuntime())
    }
    618 -> {
        val i = instruction as AggregateInstruction.StructGetUnsignedS
        StructGetUnsignedSDispatcher(i.addressSlot, i.destinationSlot, i.fieldIndex, i.packedType.toRuntime())
    }
    619 -> {
        val i = instruction as AggregateInstruction.RefCastStructGetS
        RefCastStructGetSDispatcher(i.referenceSlot, i.destinationSlot, context.typeTest(i.typeTest), i.fieldIndex)
    }
    620 -> {
        val i = instruction as AggregateInstruction.StructGetStructGetS
        StructGetStructGetSDispatcher(i.addressSlot, i.destinationSlot, i.firstFieldIndex, i.secondFieldIndex)
    }
    621 -> {
        val i = instruction as AggregateInstruction.LocalSetStructGetS
        LocalSetStructGetSDispatcher(i.sourceSlot, i.localSlot, i.destinationSlot, i.fieldIndex)
    }
    622 -> {
        val i = instruction as AggregateInstruction.StructNewS
        StructNewSDispatcher(i.firstFieldSlot, i.destinationSlot, context.runtimeTypes[i.type.index])
    }
    623 -> {
        val i = instruction as AggregateInstruction.StructNewDefaultS
        StructNewDefaultSDispatcher(i.destinationSlot, context.runtimeTypes[i.type.index], context.structDefaults(i.type.index))
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions13(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    624 -> {
        val i = instruction as AggregateInstruction.StructSetI
        StructSetIDispatcher(i.value, i.addressSlot, i.fieldIndex)
    }
    625 -> {
        val i = instruction as AggregateInstruction.StructSetS
        StructSetSDispatcher(i.valueSlot, i.addressSlot, i.fieldIndex)
    }
    626 -> {
        val i = instruction as TableInstruction.TableCopyIii
        TableCopyIiiDispatcher(i.elementsToCopy, i.srcOffset, i.dstOffset, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    627 -> {
        val i = instruction as TableInstruction.TableCopyIis
        TableCopyIisDispatcher(i.elementsToCopy, i.srcOffset, i.dstOffsetSlot, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    628 -> {
        val i = instruction as TableInstruction.TableCopyIsi
        TableCopyIsiDispatcher(i.elementsToCopy, i.srcOffsetSlot, i.dstOffset, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    629 -> {
        val i = instruction as TableInstruction.TableCopyIss
        TableCopyIssDispatcher(i.elementsToCopy, i.srcOffsetSlot, i.dstOffsetSlot, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    630 -> {
        val i = instruction as TableInstruction.TableCopySii
        TableCopySiiDispatcher(i.elementsToCopySlot, i.srcOffset, i.dstOffset, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    631 -> {
        val i = instruction as TableInstruction.TableCopySis
        TableCopySisDispatcher(i.elementsToCopySlot, i.srcOffset, i.dstOffsetSlot, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    632 -> {
        val i = instruction as TableInstruction.TableCopySsi
        TableCopySsiDispatcher(i.elementsToCopySlot, i.srcOffsetSlot, i.dstOffset, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    633 -> {
        val i = instruction as TableInstruction.TableCopySss
        TableCopySssDispatcher(i.elementsToCopySlot, i.srcOffsetSlot, i.dstOffsetSlot, context.tables[i.srcTable.index], context.tables[i.destTable.index])
    }
    634 -> {
        val i = instruction as TableInstruction.TableFillIsi
        TableFillIsiDispatcher(i.elementsToFill, i.fillValueSlot, i.tableOffset, context.tables[i.table.index])
    }
    635 -> {
        val i = instruction as TableInstruction.TableFillIss
        TableFillIssDispatcher(i.elementsToFill, i.fillValueSlot, i.tableOffsetSlot, context.tables[i.table.index])
    }
    636 -> {
        val i = instruction as TableInstruction.TableFillSsi
        TableFillSsiDispatcher(i.elementsToFillSlot, i.fillValueSlot, i.tableOffset, context.tables[i.table.index])
    }
    637 -> {
        val i = instruction as TableInstruction.TableFillSss
        TableFillSssDispatcher(i.elementsToFillSlot, i.fillValueSlot, i.tableOffsetSlot, context.tables[i.table.index])
    }
    638 -> {
        val i = instruction as TableInstruction.TableGrowIs
        TableGrowIsDispatcher(i.elementsToAdd, i.referenceValueSlot, i.destinationSlot, context.tables[i.table.index])
    }
    639 -> {
        val i = instruction as TableInstruction.TableGrowSs
        TableGrowSsDispatcher(i.elementsToAddSlot, i.referenceValueSlot, i.destinationSlot, context.tables[i.table.index])
    }
    640 -> {
        val i = instruction as TableInstruction.TableInitIii
        TableInitIiiDispatcher(i.elementsToInitialise, i.segmentOffset, i.tableOffset, context.elements[i.element.index], context.tables[i.table.index])
    }
    641 -> {
        val i = instruction as TableInstruction.TableInitIis
        TableInitIisDispatcher(i.elementsToInitialise, i.segmentOffset, i.tableOffsetSlot, context.elements[i.element.index], context.tables[i.table.index])
    }
    642 -> {
        val i = instruction as TableInstruction.TableInitIsi
        TableInitIsiDispatcher(i.elementsToInitialise, i.segmentOffsetSlot, i.tableOffset, context.elements[i.element.index], context.tables[i.table.index])
    }
    643 -> {
        val i = instruction as TableInstruction.TableInitIss
        TableInitIssDispatcher(i.elementsToInitialise, i.segmentOffsetSlot, i.tableOffsetSlot, context.elements[i.element.index], context.tables[i.table.index])
    }
    644 -> {
        val i = instruction as TableInstruction.TableInitSii
        TableInitSiiDispatcher(i.elementsToInitialiseSlot, i.segmentOffset, i.tableOffset, context.elements[i.element.index], context.tables[i.table.index])
    }
    645 -> {
        val i = instruction as TableInstruction.TableInitSis
        TableInitSisDispatcher(i.elementsToInitialiseSlot, i.segmentOffset, i.tableOffsetSlot, context.elements[i.element.index], context.tables[i.table.index])
    }
    646 -> {
        val i = instruction as TableInstruction.TableInitSsi
        TableInitSsiDispatcher(i.elementsToInitialiseSlot, i.segmentOffsetSlot, i.tableOffset, context.elements[i.element.index], context.tables[i.table.index])
    }
    647 -> {
        val i = instruction as TableInstruction.TableInitSss
        TableInitSssDispatcher(i.elementsToInitialiseSlot, i.segmentOffsetSlot, i.tableOffsetSlot, context.elements[i.element.index], context.tables[i.table.index])
    }
    648 -> {
        val i = instruction as TableInstruction.TableGetI
        TableGetIDispatcher(i.elementIndex, i.destinationSlot, context.tables[i.table.index])
    }
    649 -> {
        val i = instruction as TableInstruction.TableGetS
        TableGetSDispatcher(i.elementIndexSlot, i.destinationSlot, context.tables[i.table.index])
    }
    650 -> {
        val i = instruction as TableInstruction.TableSetSi
        TableSetSiDispatcher(i.valueSlot, i.elementIndex, context.tables[i.table.index])
    }
    651 -> {
        val i = instruction as TableInstruction.TableSetSs
        TableSetSsDispatcher(i.valueSlot, i.elementIndexSlot, context.tables[i.table.index])
    }
    652 -> {
        val i = instruction as TableInstruction.TableSizeS
        TableSizeSDispatcher(i.destinationSlot, context.tables[i.table.index])
    }
    653 -> {
        val i = instruction as TableInstruction.ElemDrop
        ElemDropDispatcher(context.elements[i.element.index])
    }
    654 -> {
        val i = instruction as ParametricInstruction.SelectIii
        SelectIiiDispatcher(i.condition, i.val1, i.val2, i.destinationSlot)
    }
    655 -> {
        val i = instruction as ParametricInstruction.SelectIis
        SelectIisDispatcher(i.condition, i.val1, i.val2Slot, i.destinationSlot)
    }
    656 -> {
        val i = instruction as ParametricInstruction.SelectIsi
        SelectIsiDispatcher(i.condition, i.val1Slot, i.val2, i.destinationSlot)
    }
    657 -> {
        val i = instruction as ParametricInstruction.SelectIss
        SelectIssDispatcher(i.condition, i.val1Slot, i.val2Slot, i.destinationSlot)
    }
    658 -> {
        val i = instruction as ParametricInstruction.SelectSii
        SelectSiiDispatcher(i.conditionSlot, i.val1, i.val2, i.destinationSlot)
    }
    659 -> {
        val i = instruction as ParametricInstruction.SelectSis
        SelectSisDispatcher(i.conditionSlot, i.val1, i.val2Slot, i.destinationSlot)
    }
    660 -> {
        val i = instruction as ParametricInstruction.SelectSsi
        SelectSsiDispatcher(i.conditionSlot, i.val1Slot, i.val2, i.destinationSlot)
    }
    661 -> {
        val i = instruction as ParametricInstruction.SelectSss
        SelectSssDispatcher(i.conditionSlot, i.val1Slot, i.val2Slot, i.destinationSlot)
    }
    662 -> {
        val i = instruction as VariableInstruction.GlobalGetS
        GlobalGetSDispatcher(context.globals[i.global.index], i.destinationSlot)
    }
    663 -> {
        val i = instruction as VariableInstruction.GlobalSetI
        GlobalSetIDispatcher(i.value, context.globals[i.global.index])
    }
    664 -> {
        val i = instruction as VariableInstruction.GlobalSetS
        GlobalSetSDispatcher(i.sourceSlot, context.globals[i.global.index])
    }
    else -> error("opcode $opcode is not linked here")
}
