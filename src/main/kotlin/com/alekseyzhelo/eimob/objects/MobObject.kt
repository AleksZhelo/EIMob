package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput

@ExperimentalUnsignedTypes
class MobObject(
    bytes: ByteArray
) : MobObjectBase() {

    override val signature: UInt = SIG_OBJECT

    init {
        readCommonObjectData(bytes.binaryStream())
    }

    override fun getSize(): Int = entryHeaderSize + getCommonObjectDataSize()

    override fun serialize(out: StreamOutput) {
        super.serialize(out)
        writeCommonObjectData(out)
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobObject(this)
    }

    override fun clone(): MobObject = MobObject(toByteArraySkipHeader())

    // TODO: equals works properly?
}