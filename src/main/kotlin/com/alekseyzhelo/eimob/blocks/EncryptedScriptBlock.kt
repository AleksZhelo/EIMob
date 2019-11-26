package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobFile.Companion.eiCharset
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_SCRIPT
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.util.binaryStream
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

    private fun decryptScript(data: ByteArray, fromIndex: Int = 0): String {
        return crypt(data, fromIndex).toString(eiCharset)
    }

    private fun encryptScript(scriptText: String): ByteArray {
        return crypt(scriptText.toByteArray(eiCharset))
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
}