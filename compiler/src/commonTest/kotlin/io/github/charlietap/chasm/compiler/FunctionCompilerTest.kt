package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.refNonNullReferenceType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class FunctionCompilerTest {

    @Test
    fun foldsDeferredConstantArithmeticIntoAFrameResult() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(20),
                NumericInstruction.I32Const(22),
                NumericInstruction.I32Add,
            ),
        )

        val compiled = compileFunction(
            context = compilerContext(module),
            function = function,
            baseIp = 7,
        )

        assertEquals(1, compiled.instructions.size)
        assertEquals(3, compiled.frameSlots)
        assertContentEquals(intArrayOf(0), compiled.returnSlots)

        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun reusesTheLowestConsumedSlotForChainedExpressions() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Const(2),
                NumericInstruction.I32Add,
                NumericInstruction.I32Const(3),
                NumericInstruction.I32Add,
                NumericInstruction.I32Const(4),
                NumericInstruction.I32Add,
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(3, compiled.frameSlots)
        assertEquals(10, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun fusesANumericResultDirectlyIntoALocal() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(),
                ),
            ),
        )
        val function = function(
            locals = listOf(Local(Index.LocalIndex(0u), i32ValueType())),
            body = Expression(
                NumericInstruction.I32Const(20),
                NumericInstruction.I32Const(22),
                NumericInstruction.I32Add,
                VariableInstruction.LocalSet(Index.LocalIndex(0u)),
            ),
        )

        val compiled = compileFunction(
            context = compilerContext(module),
            function = function,
            baseIp = 0,
        )

        assertEquals(2, compiled.instructions.size)
        assertEquals(3, compiled.frameSlots)

        val vstack = vstack().apply { reserveFrame(compiled.frameSlots) }
        val cstack = cstack()
        val store = store()
        val executionContext = executionContext(vstack = vstack, cstack = cstack, store = store)
        compiled.instructions.first()(vstack, cstack, store, executionContext, 1)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun preservesALiveLocalAliasBeforeOverwritingTheLocal() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            locals = listOf(Local(Index.LocalIndex(0u), i32ValueType())),
            body = Expression(
                NumericInstruction.I32Const(7),
                VariableInstruction.LocalSet(Index.LocalIndex(0u)),
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(9),
                VariableInstruction.LocalSet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Add,
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(8, vstack.getFrameSlot(0).toInt())
        assertEquals(9, vstack.getFrameSlot(1).toInt())
    }

    @Test
    fun compilesSelectFromDeferredImmediates() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(7),
                NumericInstruction.I32Const(9),
                NumericInstruction.I32Const(0),
                ParametricInstruction.Select,
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(9, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun eliminatesABitcastOfAMaterializedTemporary() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.F32Const(1f, 1f.toRawBits()),
                NumericInstruction.F32Neg,
                NumericInstruction.I32ReinterpretF32,
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals((-1f).toRawBits(), vstack.getFrameSlot(0).toInt())
        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun compilesDualResultWideArithmetic() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i64ValueType(), i64ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I64Const(-1),
                NumericInstruction.I64Const(0),
                NumericInstruction.I64Const(1),
                NumericInstruction.I64Const(0),
                NumericInstruction.I64Add128,
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(0, vstack.getFrameSlot(0))
        assertEquals(1, vstack.getFrameSlot(1))
    }

    @Test
    fun compilesIfArmsToPatchedProgramTargets() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(0),
                ControlInstruction.If(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                ControlInstruction.Else,
                NumericInstruction.I32Const(7),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(7, vstack.getFrameSlot(0).toInt())
        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun foldsAConstantTrueIfIntoTheReachableArm() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(1),
                ControlInstruction.If(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                ControlInstruction.Else,
                NumericInstruction.I32Const(7),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun foldsAnAlwaysTakenBranchIfIntoUnreachableFallthrough() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                ControlInstruction.Block(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                NumericInstruction.I32Const(1),
                ControlInstruction.BrIf(Index.LabelIndex(0u)),
                ParametricInstruction.Drop,
                NumericInstruction.I32Const(7),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun preservesAPreviouslyReachedTargetAfterFoldingANeverTakenBranch() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            params = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                ControlInstruction.BrIf(Index.LabelIndex(0u)),
                NumericInstruction.I32Const(0),
                ControlInstruction.BrIf(Index.LabelIndex(0u)),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        execute(compiled) { setFrameSlot(0, 1) }

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun preparesOnlyTheSelectedConstantBranchTableTarget() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                ControlInstruction.Block(BlockType.ValType(i32ValueType())),
                ControlInstruction.Block(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                NumericInstruction.I32Const(1),
                ControlInstruction.BrTable(
                    labelIndices = listOf(Index.LabelIndex(0u)),
                    defaultLabelIndex = Index.LabelIndex(1u),
                ),
                ControlInstruction.End(1),
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Add,
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun compilesBranchResultsThroughTakenOnlyPaths() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                ControlInstruction.Block(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                ControlInstruction.Br(Index.LabelIndex(0u)),
                NumericInstruction.I32Const(7),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun compilesDeeplyNestedBlocksIteratively() {
        val depth = 10_000
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        )
        val instructions = List(depth) {
            ControlInstruction.Block(BlockType.Empty)
        } + ControlInstruction.End(depth)
        val function = function(body = Expression(instructions))

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(1, compiled.instructions.size)
    }

    @Test
    fun materializesAGrowingStackAcrossNestedBlocksWithoutRescanningIt() {
        val depth = 5_000
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        )
        val instructions = buildList {
            repeat(depth) { value ->
                add(NumericInstruction.I32Const(value))
                add(ControlInstruction.Block(BlockType.Empty))
            }
            repeat(depth) {
                add(ControlInstruction.End(1))
                add(ParametricInstruction.Drop)
            }
        }
        val function = function(body = Expression(instructions))

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(depth + 1, compiled.instructions.size)
    }

    @Test
    fun materializesManyAliasesOfTheSameLocalWithoutScanningTheAliasChain() {
        val aliasCount = 5_000
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        )
        val function = function(
            locals = listOf(Local(Index.LocalIndex(0u), i32ValueType())),
            body = Expression(
                buildList {
                    add(NumericInstruction.I32Const(1))
                    add(VariableInstruction.LocalSet(Index.LocalIndex(0u)))
                    repeat(aliasCount) {
                        add(VariableInstruction.LocalGet(Index.LocalIndex(0u)))
                    }
                    add(ControlInstruction.Block(BlockType.Empty))
                    add(ControlInstruction.End(1))
                    repeat(aliasCount) {
                        add(ParametricInstruction.Drop)
                    }
                },
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun resolvesManyDeepBranchesWithoutScanningTheControlStack() {
        val depth = 10_000
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        )
        val instructions = buildList {
            repeat(depth) {
                add(ControlInstruction.Block(BlockType.Empty))
            }
            repeat(depth) {
                add(NumericInstruction.I32Const(0))
                add(ControlInstruction.BrIf(Index.LabelIndex(depth.toUInt())))
            }
            add(ControlInstruction.End(depth))
        }
        val function = function(body = Expression(instructions))

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(1, compiled.instructions.size)
    }

    @Test
    fun doesNotPreserveAMaterializedLocalAliasAfterAnIfResult() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            locals = listOf(Local(Index.LocalIndex(0u), i32ValueType())),
            body = Expression(
                NumericInstruction.I32Const(9),
                VariableInstruction.LocalSet(Index.LocalIndex(0u)),
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(1),
                NumericInstruction.I32And,
                ControlInstruction.If(BlockType.ValType(i32ValueType())),
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Add,
                ControlInstruction.Else,
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                ControlInstruction.End(1),
                NumericInstruction.I32Const(2),
                NumericInstruction.I32DivS,
                VariableInstruction.LocalTee(Index.LocalIndex(0u)),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(5, vstack.getFrameSlot(0).toInt())
    }

    @Test
    fun fusesANumericConditionIntoAnIfBranch() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.F32Const(Float.NaN, Float.NaN.toRawBits()),
                NumericInstruction.F32Const(0f, 0f.toRawBits()),
                NumericInstruction.F32Lt,
                ControlInstruction.If(BlockType.ValType(i32ValueType())),
                NumericInstruction.I32Const(42),
                ControlInstruction.Else,
                NumericInstruction.I32Const(7),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(7, vstack.getFrameSlot(0).toInt())
        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun fusesASingleBranchResultCopyIntoTheConditionalJump() {
        val module = module(
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(
                        functionType = functionType(
                            results = resultType(listOf(i32ValueType())),
                        ),
                    ),
                ),
            ),
        )
        val function = function(
            body = Expression(
                NumericInstruction.I32Const(20),
                NumericInstruction.I32Const(22),
                NumericInstruction.I32Add,
                ControlInstruction.Block(BlockType.Empty),
                NumericInstruction.I32Const(1),
                ControlInstruction.BrIf(Index.LabelIndex(1u)),
                ControlInstruction.End(1),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)
        val vstack = execute(compiled)

        assertEquals(42, vstack.getFrameSlot(0).toInt())
        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun fusesAReferenceCastIntoAStructGet() {
        val referenceType = refNullReferenceType(AbstractHeapType.Struct)
        val module = aggregateModule(
            parameterTypes = listOf(referenceValueType(referenceType)),
            resultValueType = i32ValueType(),
        )
        val function = function(
            typeIndex = Index.TypeIndex(2u),
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                io.github.charlietap.chasm.ast.instruction.ReferenceInstruction.RefCast(
                    refNonNullReferenceType(AbstractHeapType.Struct),
                ),
                io.github.charlietap.chasm.ast.instruction.AggregateInstruction.StructGet(
                    Index.TypeIndex(1u),
                    Index.FieldIndex(0u),
                ),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun fusesConsecutiveReferenceStructGets() {
        val referenceType = refNullReferenceType(AbstractHeapType.Struct)
        val module = aggregateModule(
            parameterTypes = listOf(referenceValueType(referenceType)),
            resultValueType = i32ValueType(),
        )
        val function = function(
            typeIndex = Index.TypeIndex(2u),
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                io.github.charlietap.chasm.ast.instruction.AggregateInstruction.StructGet(
                    Index.TypeIndex(0u),
                    Index.FieldIndex(0u),
                ),
                io.github.charlietap.chasm.ast.instruction.AggregateInstruction.StructGet(
                    Index.TypeIndex(1u),
                    Index.FieldIndex(0u),
                ),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun fusesALocalTeeIntoAStructGet() {
        val referenceType = refNullReferenceType(AbstractHeapType.Struct)
        val module = aggregateModule(
            parameterTypes = listOf(
                referenceValueType(referenceType),
                referenceValueType(referenceType),
            ),
            resultValueType = i32ValueType(),
        )
        val function = function(
            typeIndex = Index.TypeIndex(2u),
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(1u)),
                VariableInstruction.LocalTee(Index.LocalIndex(0u)),
                io.github.charlietap.chasm.ast.instruction.AggregateInstruction.StructGet(
                    Index.TypeIndex(1u),
                    Index.FieldIndex(0u),
                ),
            ),
        )

        val compiled = compileFunction(compilerContext(module), function, baseIp = 0)

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun manualGcDoesNotEmitCollectionInstructions() {
        val function = allocatingFunction()
        val module = allocatingModule(function)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.MANUAL)),
            function = function,
            baseIp = 0,
        )

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun traditionalGcEmitsAConditionalPauseAfterAnAllocation() {
        val function = allocatingFunction()
        val module = allocatingModule(function)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL)),
            function = function,
            baseIp = 0,
        )

        assertEquals(3, compiled.instructions.size)
    }

    @Test
    fun traditionalGcDoesNotPauseAfterANonAllocatingGcInstruction() {
        val function = function(
            typeIndex = Index.TypeIndex(1u),
            body = Expression(
                NumericInstruction.I32Const(1),
                AggregateInstruction.RefI31,
                ParametricInstruction.Drop,
            ),
        )
        val module = allocatingModule(function)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL)),
            function = function,
            baseIp = 0,
        )

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun arenaGcDoesNotEmitACollectionInstructionIntoAnExportedFunction() {
        val function = allocatingFunction()
        val module = allocatingModule(function, exported = true)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.ARENA)),
            function = function,
            baseIp = 0,
        )

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun arenaGcDoesNotEmitACollectionPointForAnInternalFunction() {
        val function = allocatingFunction()
        val module = allocatingModule(function)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.ARENA)),
            function = function,
            baseIp = 0,
        )

        assertEquals(2, compiled.instructions.size)
    }

    @Test
    fun arenaGcDoesNotEmitACollectionPointForAModuleWithoutGcInstructions() {
        val function = function(typeIndex = Index.TypeIndex(1u))
        val module = allocatingModule(function, exported = true)

        val compiled = compileFunction(
            context = compilerContext(module, RuntimeConfig(gcStrategy = GCStrategy.ARENA)),
            function = function,
            baseIp = 0,
        )

        assertEquals(1, compiled.instructions.size)
    }

    @Test
    fun preservesTheUnsupportedAtomicInstructionError() {
        assertUnsupportedInstruction(
            instruction = AtomicMemoryInstruction.Fence,
            expected = InstantiationError.UnsupportedThreadsModule,
        )
    }

    @Test
    fun preservesTheUnsupportedVectorInstructionError() {
        assertUnsupportedInstruction(
            instruction = VectorInstruction.V128Const(ByteArray(16)),
            expected = InstantiationError.UnsupportedSIMDModule,
        )
    }

    private fun assertUnsupportedInstruction(
        instruction: io.github.charlietap.chasm.ast.instruction.Instruction,
        expected: InstantiationError,
    ) {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
        )
        val function = function(body = Expression(instruction))
        val program = Program().apply {
            append(noOpInstruction)
        }

        val error = FunctionCompiler(compilerContext(module), function, program).unwrapError()

        assertEquals(expected, error)
        assertEquals(1, program.size)
    }
}

private fun aggregateModule(
    parameterTypes: List<io.github.charlietap.chasm.type.ValueType>,
    resultValueType: io.github.charlietap.chasm.type.ValueType,
): io.github.charlietap.chasm.ast.module.Module {
    val referenceType = refNullReferenceType(AbstractHeapType.Struct)
    val referenceStruct = definedType(
        recursiveType = recursiveType(
            listOf(
                finalSubType(
                    compositeType = structCompositeType(
                        structType(
                            listOf(
                                immutableFieldType(
                                    valueStorageType(referenceValueType(referenceType)),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
        typeIndex = 0,
    )
    val valueStruct = definedType(
        recursiveType = recursiveType(
            listOf(
                finalSubType(
                    compositeType = structCompositeType(
                        structType(
                            listOf(immutableFieldType(valueStorageType(i32ValueType()))),
                        ),
                    ),
                ),
            ),
        ),
        typeIndex = 1,
    )
    val functionType = definedType(
        recursiveType = functionRecursiveType(
            functionType(
                params = resultType(parameterTypes),
                results = resultType(listOf(resultValueType)),
            ),
        ),
        typeIndex = 2,
    )
    return module(definedTypes = listOf(referenceStruct, valueStruct, functionType))
}

private fun allocatingModule(
    function: Function,
    exported: Boolean = false,
): io.github.charlietap.chasm.ast.module.Module {
    val valueStruct = definedType(
        recursiveType = recursiveType(
            listOf(
                finalSubType(
                    compositeType = structCompositeType(
                        structType(
                            listOf(immutableFieldType(valueStorageType(i32ValueType()))),
                        ),
                    ),
                ),
            ),
        ),
        typeIndex = 0,
    )
    val functionType = definedType(
        recursiveType = functionRecursiveType(),
        typeIndex = 1,
    )
    return module(
        definedTypes = listOf(valueStruct, functionType),
        functions = listOf(function),
        exports = if (exported) listOf(export()) else emptyList(),
    )
}

private fun allocatingFunction() = function(
    typeIndex = Index.TypeIndex(1u),
    body = Expression(
        AggregateInstruction.StructNewDefault(Index.TypeIndex(0u)),
        ParametricInstruction.Drop,
    ),
)

private fun compilerContext(
    module: io.github.charlietap.chasm.ast.module.Module,
    config: RuntimeConfig = runtimeConfig(),
): CompilerContext {
    val store = store()
    return CompilerContext(
        config = config,
        module = module,
        types = ModuleTypeResolver(module),
        store = store,
        instance = moduleInstance(
            functionAddresses = MutableList(module.functions.size) { index -> Address.Function(index) },
        ),
        runtimeTypes = store.runtimeTypes.register(module.definedTypes),
    )
}

private fun compileFunction(
    context: CompilerContext,
    function: Function,
    baseIp: Int,
): TestCompiledFunction {
    val program = Program(maxOf(baseIp + 1, 256))
    repeat(baseIp) {
        program.append(noOpInstruction)
    }
    val compiled = FunctionCompiler(context, function, program).unwrap()
    val instructions = List(program.size - baseIp) { index -> program.instructions[baseIp + index] }
    return TestCompiledFunction(compiled, instructions)
}

private fun execute(
    compiled: TestCompiledFunction,
    configure: io.github.charlietap.chasm.runtime.stack.ValueStack.() -> Unit = {},
): io.github.charlietap.chasm.runtime.stack.ValueStack {
    val vstack = vstack().apply {
        reserveFrame(compiled.frameSlots)
        configure()
    }
    val cstack = cstack(
        frames = listOf(
            frame(
                arity = compiled.returnSlots.size,
                returnIp = EXIT_IP,
            ),
        ),
    )
    val store = store()
    val executionContext = executionContext(vstack = vstack, cstack = cstack, store = store)
    var ip = 0
    while (ip != EXIT_IP) {
        ip = compiled.instructions[ip](vstack, cstack, store, executionContext, ip + 1)
    }
    return vstack
}

private class TestCompiledFunction(
    compiled: io.github.charlietap.chasm.runtime.function.Function,
    val instructions: List<DispatchableInstruction>,
) {
    val frameSlots = compiled.frameSlots
    val returnSlots = compiled.returnSlots
}

private val noOpInstruction = DispatchableInstruction { _, _, _, _, nextIp -> nextIp }
