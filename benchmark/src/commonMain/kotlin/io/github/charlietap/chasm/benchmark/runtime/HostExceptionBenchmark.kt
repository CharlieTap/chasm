package io.github.charlietap.chasm.benchmark.runtime

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.benchmark.BenchmarkConfig
import io.github.charlietap.chasm.benchmark.StabilizedBenchmark
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i64
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostException
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.withExceptions
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.CompiledCatch
import io.github.charlietap.chasm.runtime.exception.ExceptionRegion
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.type.RTT
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = BenchmarkConfig.WARMUP_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
@Measurement(iterations = BenchmarkConfig.MEASUREMENT_ITERATIONS, time = BenchmarkConfig.ITERATION_TIME)
class HostExceptionBenchmark : StabilizedBenchmark() {

    @Param("0", "4")
    var payloadSize: Int = 0

    private lateinit var guestCaught: () -> Any
    private lateinit var hostCaught: () -> Any
    private lateinit var guestEscape: () -> Any
    private lateinit var passthrough: () -> Any

    @Setup
    fun setup() {
        guestCaught = caughtInvocation(hostRaised = false)
        hostCaught = caughtInvocation(hostRaised = true)
        guestEscape = escapingInvocation()
        passthrough = passthroughInvocation()
    }

    @Benchmark
    fun guestThrowCaught(blackhole: Blackhole) {
        blackhole.consume(guestCaught())
    }

    @Benchmark
    fun hostRaiseCaught(blackhole: Blackhole) {
        blackhole.consume(hostCaught())
    }

    @Benchmark
    fun guestEscapeAndInspect(blackhole: Blackhole) {
        blackhole.consume(guestEscape())
    }

    @Benchmark
    fun guestHostGuestPassthrough(blackhole: Blackhole) {
        blackhole.consume(passthrough())
    }

    private fun caughtInvocation(hostRaised: Boolean): () -> Any {
        val config = RuntimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(RTT(0), payloadTagType())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exception = HostException(
            runtimeStore.heap.allocateException(
                tagAddress,
                LongArray(payloadSize) { it.toLong() },
            ),
        )
        runtimeStore.heap.retain(exception.rawReference)
        val hostFunction = hostFunctionInstance(
            functionType = payloadFunctionType(),
            function = HostFunction { parameters, _ ->
                withTag(ModuleIndex.TagIndex(0)) {
                    raise(parameters)
                }
            },
        )
        val invocation = if (hostRaised) {
            CallDispatcher(
                ControlInstruction.HostCall(
                    instance = hostFunction,
                    caller = module,
                    operands = OperandTransfer(
                        sources = Array(payloadSize) { TransferSource.Slot(it) },
                        destinationSlotBase = 0,
                    ),
                    callFrameOffset = 0,
                ),
            )
        } else {
            val throwInstruction = ThrowRefDispatcher(ControlInstruction.ThrowRefS(exceptionSlot = 1))
            DispatchableInstruction { vstack, context, nextIp ->
                vstack.setFrameSlot(1, exception.rawReference)
                throwInstruction(vstack, context, nextIp)
            }
        }
        val interfaceSlotCount = if (hostRaised) payloadSize else 0
        val entryIp = program.append(
            arrayOf(
                invocation,
                EndFunctionDispatcher(AdminInstruction.EndFunction(resultCount = 0, activationHeaderSlot = interfaceSlotCount)),
            ),
        )
        program.registerExceptionTable(
            FunctionExceptionTable(
                entryIp = entryIp,
                instructionCount = 2,
                interfaceSlotCount = interfaceSlotCount,
                resultCount = 0,
                regions = arrayOf(
                    ExceptionRegion(
                        startOffset = 0,
                        endOffset = 1,
                        parentRegion = -1,
                        catches = arrayOf(
                            CompiledCatch(CompiledCatch.CATCH_ALL_TAG, 1, intArrayOf(), false, interfaceSlotCount + 1),
                        ),
                    ),
                ),
                tailCallOffsets = intArrayOf(),
            ),
        )
        val function = wasmFunctionInstance(
            module = module,
            functionType = if (hostRaised) payloadFunctionType() else functionType(),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = if (hostRaised) payloadSize + 1 else 2,
            ),
        )
        val parameters = if (hostRaised) {
            List(payloadSize) { i64(it.toLong()) }
        } else {
            emptyList()
        }
        return {
            FunctionInvoker(config, runtimeStore, module, function, parameters)
        }
    }

    private fun escapingInvocation(): () -> Any {
        val config = RuntimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(RTT(0), payloadTagType())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exception = HostException(
            runtimeStore.heap.allocateException(
                tagAddress,
                LongArray(payloadSize) { it.toLong() },
            ),
        )
        runtimeStore.heap.retain(exception.rawReference)
        val function = escapingFunction(program, module, exception.rawReference)
        return {
            val invocation = FunctionInvoker(config, runtimeStore, module, function, emptyList())
            check(invocation == Err(InvocationError.ThrownException))
            val marker = runtimeStore.heap.beginScope()
            val pending = runtimeStore.heap.takePending()
            val observed = if (payloadSize == 0) {
                runtimeStore.heap.tag(pending).rawAddress.toLong()
            } else {
                runtimeStore.heap.readPayload(pending, payloadSize - 1)
            }
            runtimeStore.heap.endScope(marker)
            observed
        }
    }

    private fun passthroughInvocation(): () -> Any {
        val config = RuntimeConfig()
        val program = Program()
        val runtimeStore = store(program = program)
        val tagAddress = runtimeStore.heap.registerTag(RTT(0), payloadTagType())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress))
        val exception = HostException(
            runtimeStore.heap.allocateException(
                tagAddress,
                LongArray(payloadSize) { it.toLong() },
            ),
        )
        runtimeStore.heap.retain(exception.rawReference)
        val nested = escapingFunction(program, module, exception.rawReference)
        runtimeStore.functions.add(nested)
        val bridge = hostFunctionInstance(
            function = HostFunction { _, _ ->
                val invocation = FunctionInvoker(config, runtimeStore, module, nested, emptyList())
                check(invocation == Err(InvocationError.ThrownException))
                withExceptions {
                    raisePending()
                }
            },
        )
        val entryIp = program.append(
            arrayOf(
                CallDispatcher(
                    ControlInstruction.HostCall(
                        instance = bridge,
                        caller = module,
                        operands = OperandTransfer(emptyArray(), destinationSlotBase = 0),
                        callFrameOffset = 0,
                    ),
                ),
                EndFunctionDispatcher(AdminInstruction.EndFunction(1, 1)),
            ),
        )
        program.registerExceptionTable(
            FunctionExceptionTable(
                entryIp = entryIp,
                instructionCount = 2,
                interfaceSlotCount = 1,
                resultCount = 1,
                regions = arrayOf(
                    ExceptionRegion(
                        startOffset = 0,
                        endOffset = 1,
                        parentRegion = -1,
                        catches = arrayOf(
                            CompiledCatch(CompiledCatch.CATCH_ALL_TAG, 1, intArrayOf(0), true, 2),
                        ),
                    ),
                ),
                tailCallOffsets = intArrayOf(),
            ),
        )
        val outer = wasmFunctionInstance(
            module = module,
            functionType = functionType(results = resultType(listOf(i64ValueType()))),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 1,
            ),
        )
        return {
            FunctionInvoker(config, runtimeStore, module, outer, emptyList())
        }
    }

    private fun escapingFunction(
        program: Program,
        module: ModuleInstance,
        exceptionReference: Long,
    ): FunctionInstance.WasmFunction {
        val throwInstruction = ThrowRefDispatcher(ControlInstruction.ThrowRefS(exceptionSlot = 1))
        return wasmFunctionInstance(
            module = module,
            function = runtimeFunction(
                body = runtimeExpression(
                    program.append(
                        DispatchableInstruction { vstack, context, nextIp ->
                            vstack.setFrameSlot(1, exceptionReference)
                            throwInstruction(vstack, context, nextIp)
                        },
                    ),
                ),
                frameSlots = 2,
            ),
        )
    }

    private fun payloadTagType() = tagType(functionType = payloadFunctionType())

    private fun payloadFunctionType() = functionType(
        params = resultType(List(payloadSize) { i64ValueType() }),
    )
}
