package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.entryHeaderSize
import loggersoft.kotlin.streams.StreamOutput

@ExperimentalUnsignedTypes
class UnknownBlock(
    override val signature: UInt,
    private val bytes: ByteArray
) : Block {

    override fun getSize() = entryHeaderSize + bytes.size

    override fun serialize(out: StreamOutput) {
        super.serialize(out)
        if (bytes.isNotEmpty()) {
            out.writeBytes(bytes)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitUnknownBlock(this)
    }
}