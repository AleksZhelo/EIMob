package com.alekseyzhelo.eimob.util

import com.alekseyzhelo.eimob.types.Float3
import loggersoft.kotlin.streams.*
import kotlin.math.pow
import kotlin.math.sqrt

@ExperimentalUnsignedTypes
fun StreamInput.readUInt(byteOrder: ByteOrder = defaultByteOrder) = this.readInt(byteOrder).toUInt()

@ExperimentalUnsignedTypes
fun StreamOutput.writeUInt(value: UInt, byteOrder: ByteOrder = defaultByteOrder) =
    this.writeInt(value.toInt(), byteOrder)

@ExperimentalUnsignedTypes
fun ByteArray.binaryStream(size: Int = this.size, offset: Int = 0, readonly: Boolean = true) =
    StreamByteArea(ByteArea(this, size, offset), readOnly = readonly)

fun intToByteArray(value: Int): ByteArray {
    return when (nativeByteOrder) {
        ByteOrder.Unknown -> throw UnsupportedOperationException()
        ByteOrder.LittleEndian -> byteArrayOf(
            value.toByte(),
            (value ushr 8).toByte(),
            (value ushr 16).toByte(),
            (value ushr 24).toByte()
        )
        ByteOrder.BigEndian -> byteArrayOf(
            (value ushr 24).toByte(),
            (value ushr 16).toByte(),
            (value ushr 8).toByte(),
            value.toByte()
        )
    }
}

fun norm3(x: Double, y: Double, z: Double) = sqrt(x.pow(2.0) + y.pow(2.0) + z.pow(2.0))
fun Float3.norm() = norm3(x.toDouble(), y.toDouble(), z.toDouble())