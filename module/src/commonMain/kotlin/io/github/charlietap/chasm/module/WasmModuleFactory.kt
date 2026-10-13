package io.github.charlietap.chasm.module

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.FunctionBodyObserver
import io.github.charlietap.chasm.decoder.ModuleDecodeObserver
import io.github.charlietap.chasm.decoder.ParallelWasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.validator.IncrementalFunctionValidator
import io.github.charlietap.chasm.validator.IncrementalWasmModuleValidator
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

/**
 * Decodes [bytes] and validates the module in the same pass.
 *
 * A malformed binary is always reported as malformed, even when an earlier part of it is
 * also invalid.
 */
suspend fun WasmModuleFactory(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
): Result<Module, ModuleFactoryError> =
    WasmModuleFactory(
        config = config,
        bytes = bytes,
        taskExecutor = taskExecutor,
        decoder = ::ParallelWasmModuleDecoder,
    )

internal suspend fun WasmModuleFactory(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
    decoder: ObservedModuleDecoder,
): Result<Module, ModuleFactoryError> {
    val observer = ValidatingObserver(config)

    return decoder(config, bytes, taskExecutor, observer).fold(
        success = { module ->
            observer.result(module).fold(
                success = { Ok(module) },
                failure = { error -> Err(ModuleFactoryError.Invalid(error)) },
            )
        },
        failure = { error -> Err(ModuleFactoryError.Malformed(error)) },
    )
}

internal typealias ObservedModuleDecoder =
    suspend (ModuleConfig, ByteArray, ParallelTaskExecutor, ModuleDecodeObserver) -> Result<Module, ModuleDecoderError>

private class ValidatingObserver(
    private val config: ModuleConfig,
) : ModuleDecodeObserver {

    private var validator: IncrementalWasmModuleValidator? = null
    private var functions: List<Function> = emptyList()
    private var prelude: Result<Unit, ModuleValidatorError> = Ok(Unit)
    private var remainder: Result<Unit, ModuleValidatorError> = Ok(Unit)

    private val invalidBodies = EarliestFailure<ModuleValidatorError>()

    override fun prelude(module: Module, dataCount: UInt?): Boolean {
        val validator = IncrementalWasmModuleValidator(config, module, dataCount)
        this.validator = validator
        functions = module.functions
        prelude = validator.validatePrelude()

        return prelude.isOk
    }

    override fun bodyObserver(): FunctionBodyObserver = ValidatingBodyObserver(
        validator = checkNotNull(validator).functionValidator(),
        functions = functions,
        invalidBodies = invalidBodies,
    )

    override fun remainder(dataSegments: List<DataSegment>) {
        remainder = checkNotNull(validator).validateRemainder(dataSegments)
    }

    /** The first error a serial validator would have met, once [module] has decoded. */
    fun result(module: Module): Result<Unit, ModuleValidatorError> {
        // the decoder went without an observer, so nothing has been validated yet
        if (validator == null) return validateSerially(module)

        val result = when {
            prelude.isErr -> prelude
            else -> invalidBodies.error?.let { error -> Err(error) } ?: remainder
        }
        // bodies are validated before the data segments are seen, which can change what is
        // found, so on a module that has any the serial validator decides
        return if (result.isErr && module.dataSegments.isNotEmpty()) validateSerially(module) else result
    }

    private fun validateSerially(module: Module): Result<Unit, ModuleValidatorError> =
        WasmModuleValidator(config, module).fold(success = { Ok(Unit) }, failure = ::Err)
}

private class ValidatingBodyObserver(
    private val validator: IncrementalFunctionValidator,
    private val functions: List<Function>,
    private val invalidBodies: EarliestFailure<ModuleValidatorError>,
) : FunctionBodyObserver {

    private var bodyIndex = 0
    private var validating = false

    override fun begin(bodyIndex: Int, locals: List<Local>) {
        this.bodyIndex = bodyIndex
        validating = !invalidBodies.isBeyond(bodyIndex)
        if (validating) validator.begin(functions[bodyIndex].typeIndex, locals)
    }

    override fun instruction(opcode: Int, instruction: Instruction) {
        if (validating) validator.instruction(opcode, instruction)
    }

    override fun end() {
        if (!validating) return

        validator.end().fold(
            success = {},
            failure = { error -> invalidBodies.report(bodyIndex, error) },
        )
    }
}
