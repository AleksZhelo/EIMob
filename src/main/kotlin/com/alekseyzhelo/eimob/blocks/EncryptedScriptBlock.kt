package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_SCRIPT
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.encodeMobString
import com.alekseyzhelo.eimob.decodeMobString
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.intToByteArray
import loggersoft.kotlin.streams.StreamOutput
import kotlin.experimental.xor

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class EncryptedScriptBlock(
    bytes: ByteArray
) : ScriptBlock {

    override val signature: UInt = SIG_SCRIPT
    var scriptKey: Int = bytes.binaryStream(4, 0).readInt()
    override var script: String = decryptScript(bytes, fromIndex = 4)

    override fun getSize() = entryHeaderSize + 4 + script.length

    override fun serialize(out: StreamOutput) {
        val encryptedScript = encryptScript(script)
        with(out) {
            super.serialize(this)
            writeInt(scriptKey)
            writeBytes(encryptedScript)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitScriptBlock(this)
    }

    override fun clone(): EncryptedScriptBlock = EncryptedScriptBlock(toByteArray())

    private fun decryptScript(data: ByteArray, fromIndex: Int = 0): String {
        return crypt(data, fromIndex).decodeMobString()
    }

    private fun encryptScript(scriptText: String): ByteArray {
        return crypt(scriptText.encodeMobString())
    }

    private fun crypt(data: ByteArray, fromIndex: Int = 0): ByteArray {
        var key = scriptKey
        val result = data.copyOfRange(fromIndex, data.size)

        for (i in result.indices) {
            key += (((((key * 13) shl 4) + key) shl 8) - key) * 4 + 2531011
            result[i] = result[i] xor ((key shr 16).toByte())
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedScriptBlock

        if (scriptKey != other.scriptKey) return false
        if (script != other.script) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scriptKey
        result = 31 * result + script.hashCode()
        return result
    }

    companion object {
        fun createWithKey(key: Int) = EncryptedScriptBlock(intToByteArray(key))
    }
}