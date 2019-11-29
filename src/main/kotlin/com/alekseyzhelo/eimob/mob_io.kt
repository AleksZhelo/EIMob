package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.MobFile.Companion.eiCharset
import com.alekseyzhelo.eimob.objects.MobLever
import com.alekseyzhelo.eimob.util.*
import loggersoft.kotlin.streams.StreamInput
import loggersoft.kotlin.streams.StreamOutput
import java.io.IOException
import kotlin.math.sqrt

const val entryHeaderSize = 8

fun Array<String>.totalLength(): Int = this.fold(0, { acc, x -> acc + x.length })
fun Array<String>.mobEntrySize(): Int = Int.SIZE_BYTES + entryHeaderSize * size + totalLength()
fun Array<Float2>.mobEntrySize(): Int = Int.SIZE_BYTES + size * 8
fun Array<Float3>.mobEntrySize(): Int = Int.SIZE_BYTES + size * 12
@ExperimentalUnsignedTypes
fun String.encodeMobString() = this.toByteArray(eiCharset)
@ExperimentalUnsignedTypes
fun ByteArray.decodeMobString() = this.toString(eiCharset)

@ExperimentalUnsignedTypes
fun testSignature(signature: UInt, expectedSignature: UInt, errorMessage: String) {
    if (signature != expectedSignature) {
        throw MobException(errorMessage)
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobEntryHeader(signature: UInt, entrySize: Int) {
    writeUInt(signature)
    writeInt(entrySize)
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobEntry(): Pair<UInt, ByteArray> {
    val signature = readUInt()
    val size = readInt()
    val blockBytesSize = size - entryHeaderSize
    val bytes = ByteArray(blockBytesSize)
    if (blockBytesSize > 0) {
        try {
            val bytesRead = readBytes(bytes)
            if (bytesRead != bytes.size) {
                throw MobException(
                    "Could not read block (or block entry) data " +
                            "with signature $signature and size $size from the input"
                )
            }
        } catch (x: IOException) {
            throw MobException(
                "Could not read block (or block entry) data " +
                        "with signature $signature and size $size: IO error", x
            )
        }
    }
    return Pair(signature, bytes)
}

@ExperimentalUnsignedTypes
private fun StreamInput.readMobEntryChecked(expectedSignature: UInt, errorMessage: String): ByteArray {
    with(readMobEntry()) {
        testSignature(first, expectedSignature, errorMessage)
        return second
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobString(expectedSignature: UInt, errorMessage: String): String =
    readMobEntryChecked(expectedSignature, errorMessage).decodeMobString()

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat(expectedSignature: UInt, errorMessage: String): Float =
    readMobEntryChecked(expectedSignature, errorMessage).binaryStream().readFloat()

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat2Plain(): Float2 {
    val x = readFloat()
    val y = readFloat()
    return Float2(x, y)
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat3Plain(): Float3 {
    val x = readFloat()
    val y = readFloat()
    val z = readFloat()
    return Float3(x, y, z)
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat2(expectedSignature: UInt, errorMessage: String): Float2 {
    with(readMobEntryChecked(expectedSignature, errorMessage).binaryStream()) {
        return readMobFloat2Plain()
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat3(expectedSignature: UInt, errorMessage: String): Float3 {
    with(readMobEntryChecked(expectedSignature, errorMessage).binaryStream()) {
        return readMobFloat3Plain()
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat4(expectedSignature: UInt, errorMessage: String): Float4 {
    with(readMobEntryChecked(expectedSignature, errorMessage).binaryStream()) {
        val x = readFloat()
        val y = readFloat()
        val z = readFloat()
        val w = readFloat()
        return Float4(x, y, z, w)
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobIntSquareMatrix(expectedSignature: UInt, errorMessage: String): Array<IntArray> {
    val bytes = readMobEntryChecked(expectedSignature, errorMessage)
    val dim = sqrt((bytes.size / Int.SIZE_BYTES).toDouble()).toInt()
    val matrix = array2dOfInt(dim, dim)
    with(bytes.binaryStream()) {
        // row-major
        for (row in matrix.indices) {
            for (column in matrix[row].indices) {
                matrix[row][column] = readInt()
            }
        }
    }
    return matrix
}

@ExperimentalUnsignedTypes
private inline fun <reified T> StreamInput.readMobArray(
    expectedSignature: UInt,
    errorMessageMain: String,
    errorMessageSub: String,
    entryReader: (StreamInput, UInt, String) -> T
): Array<T> {
    with(readMobEntryChecked(expectedSignature, errorMessageMain).binaryStream()) {
        val arrayLength = readInt()
        return Array(arrayLength) {
            entryReader(this, expectedSignature, errorMessageSub)
        }
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readMobStringArray(
    expectedSignature: UInt, errorMsgMain: String, errorMsgSub: String
): Array<String> =
    readMobArray(expectedSignature, errorMsgMain, errorMsgSub) { inp: StreamInput, sig: UInt, msg: String ->
        inp.readMobString(sig, msg)
    }

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat2Array(
    expectedSignature: UInt, errorMsgMain: String, errorMsgSub: String
): Array<Float2> =
    readMobArray(expectedSignature, errorMsgMain, errorMsgSub) { inp: StreamInput, _: UInt, _: String ->
        inp.readMobFloat2Plain()
    }

@ExperimentalUnsignedTypes
fun StreamInput.readMobFloat3Array(
    expectedSignature: UInt, errorMsgMain: String, errorMsgSub: String
): Array<Float3> =
    readMobArray(expectedSignature, errorMsgMain, errorMsgSub) { inp: StreamInput, _: UInt, _: String ->
        inp.readMobFloat3Plain()
    }

@ExperimentalUnsignedTypes
fun StreamInput.readMobByte(expectedSignature: UInt, errorMessage: String): Byte =
    readMobEntryChecked(expectedSignature, errorMessage)[0]

@ExperimentalUnsignedTypes
fun StreamInput.readMobBoolean(expectedSignature: UInt, errorMessage: String): Boolean =
    when (readMobByte(expectedSignature, errorMessage)) {
        0.toByte() -> false
        else -> true
    }

@ExperimentalUnsignedTypes
fun StreamInput.readMobInt(expectedSignature: UInt, errorMessage: String): Int =
    readMobEntryChecked(expectedSignature, errorMessage).binaryStream().readInt()

@ExperimentalUnsignedTypes
fun StreamInput.readMobLeverStats(expectedSignature: UInt, errorMessage: String): MobLever.LeverStats {
    with(readMobEntryChecked(expectedSignature, errorMessage).binaryStream()) {
        val int1 = readInt()
        val int2 = readInt()
        val int3 = readInt()
        return MobLever.LeverStats(int1, int2, int3)
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobString(signature: UInt, value: String) {
    val valueBytes = value.encodeMobString()
    writeMobEntryHeader(signature, entryHeaderSize + valueBytes.size)
    if (valueBytes.isNotEmpty()) {
        writeBytes(valueBytes)
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat(signature: UInt, value: Float) {
    writeMobEntryHeader(signature, entryHeaderSize + 4)
    writeFloat(value)
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat2(signature: UInt, value: Float2) {
    writeMobEntryHeader(signature, entryHeaderSize + 8)
    writeMobFloat2Plain(value)
}

private fun StreamOutput.writeMobFloat2Plain(value: Float2) {
    with(value) {
        writeFloat(component1())
        writeFloat(component2())
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat3(signature: UInt, value: Float3) {
    writeMobEntryHeader(signature, entryHeaderSize + 12)
    writeMobFloat3Plain(value)
}

private fun StreamOutput.writeMobFloat3Plain(value: Float3) {
    with(value) {
        writeFloat(component1())
        writeFloat(component2())
        writeFloat(component3())
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat4(signature: UInt, value: Float4) {
    writeMobEntryHeader(signature, entryHeaderSize + 16)
    with(value) {
        writeFloat(component1())
        writeFloat(component2())
        writeFloat(component3())
        writeFloat(component4())
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobIntSquareMatrix(signature: UInt, value: Array<IntArray>) {
    writeMobEntryHeader(signature, entryHeaderSize + value.mobEntrySize())
    for (row in value.indices) {
        for (column in value[row].indices) {
            writeInt(value[row][column])
        }
    }
}

@ExperimentalUnsignedTypes
private fun <T> StreamOutput.writeMobArray(
    signature: UInt,
    value: Array<T>,
    valueSize: Int,
    entryWriter: (UInt, T) -> Unit
) {
    writeMobEntryHeader(signature, entryHeaderSize + valueSize)
    writeInt(value.size)
    value.forEach {
        entryWriter(signature, it)
    }
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobStringArray(signature: UInt, value: Array<String>) =
    writeMobArray(signature, value, value.mobEntrySize()) { sig: UInt, vl: String -> writeMobString(sig, vl) }

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat2Array(signature: UInt, value: Array<Float2>) =
    writeMobArray(signature, value, value.mobEntrySize()) { _: UInt, vl: Float2 -> writeMobFloat2Plain(vl) }

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobFloat3Array(signature: UInt, value: Array<Float3>) =
    writeMobArray(signature, value, value.mobEntrySize()) { _: UInt, vl: Float3 -> writeMobFloat3Plain(vl) }

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobByte(signature: UInt, value: Byte) {
    writeMobEntryHeader(signature, entryHeaderSize + 1)
    writeByte(value)
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobBoolean(signature: UInt, value: Boolean) {
    writeMobByte(signature, if (value) 1.toByte() else 0.toByte())
}


@ExperimentalUnsignedTypes
fun StreamOutput.writeMobInt(signature: UInt, value: Int) {
    writeMobEntryHeader(signature, entryHeaderSize + 4)
    writeInt(value)
}

@ExperimentalUnsignedTypes
fun StreamOutput.writeMobLeverStats(signature: UInt, value: MobLever.LeverStats) {
    writeMobEntryHeader(signature, entryHeaderSize + 12)
    with(value) {
        writeInt(component1())
        writeInt(component2())
        writeInt(component3())
    }
}
