package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_SCRIPT_PLAIN_TEXT
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.encodeMobString
import com.alekseyzhelo.eimob.decodeMobString
import loggersoft.kotlin.streams.StreamOutput

@ExperimentalUnsignedTypes
class PlainTextScriptBlock(
    bytes: ByteArray
) : ScriptBlock {

    override val signature: UInt = SIG_SCRIPT_PLAIN_TEXT
    override var script: String = bytes.decodeMobString()

    override fun getSize() = entryHeaderSize + script.length

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeBytes(script.encodeMobString())
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitScriptBlock(this)
    }
}