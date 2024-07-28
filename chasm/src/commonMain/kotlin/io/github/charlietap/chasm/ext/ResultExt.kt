package io.github.charlietap.chasm.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import kotlin.jvm.JvmName

@JvmName("decoderToChasmResult")
internal fun <S, E> Result<S, E>.toChasmResult(): ChasmResult<S, ChasmError>
    where E : ModuleDecoderError = fold(::Success) { error ->
    return ChasmResult.Error(ChasmError.DecodeError(error))
}

@JvmName("executorToChasmResult")
internal fun <S, E> Result<S, E>.toChasmResult(): ChasmResult<S, ChasmError>
where E : ModuleTrapError = fold(::Success) { error ->
    return ChasmResult.Error(ChasmError.ExecutionError(error))
}
