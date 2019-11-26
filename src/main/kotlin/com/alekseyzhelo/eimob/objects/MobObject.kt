package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.ObjectsBlock.Companion.SIG_OBJECT
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@ExperimentalUnsignedTypes
class MobObject(
    bytes: ByteArray
) : Block, MobObjectDataHolder() {

    override val signature: UInt = SIG_OBJECT

    init {
        readObjectData(bytes.binaryStream())
    }

    override fun getSize(): Int = entryHeaderSize + getObjectDataSize()

    override fun serialize(out: StreamOutput) {
        super.serialize(out)
        writeObjectData(out)
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobObject(this)
    }
}