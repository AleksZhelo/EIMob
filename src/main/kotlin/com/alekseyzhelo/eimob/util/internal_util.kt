package com.alekseyzhelo.eimob.util

import com.alekseyzhelo.eimob.blocks.Block
import loggersoft.kotlin.streams.toBinaryBufferedStream
import java.io.ByteArrayOutputStream

@ExperimentalUnsignedTypes
internal fun Block.toByteArraySkipHeader(): ByteArray {
    return ByteArrayOutputStream(getSize()).use {
        with(it.toBinaryBufferedStream()) {
            serialize(this)
            flush()
        }
        val bytesWithHeader = it.toByteArray()
        bytesWithHeader.copyOfRange(8, bytesWithHeader.size)
    }
}