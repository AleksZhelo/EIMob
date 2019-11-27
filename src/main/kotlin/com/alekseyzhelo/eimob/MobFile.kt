package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_AIGRAPH
import com.alekseyzhelo.eimob.blocks.ScriptBlock
import com.alekseyzhelo.eimob.util.FileNameUtils
import com.alekseyzhelo.eimob.util.LoggerDelegate
import com.alekseyzhelo.eimob.util.readUInt
import loggersoft.kotlin.streams.StreamInput
import loggersoft.kotlin.streams.toBinaryBufferedStream
import java.io.ByteArrayInputStream
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
class MobFile @Throws(MobException::class) constructor(file: String, input: InputStream) {

    private val logger by LoggerDelegate()
    val filePath: Path = Paths.get(file)
    val backupFilePath: Path =
        filePath.resolveSibling("${FileNameUtils.getBaseName(filePath.fileName.toString())}-backup.mob")
    private val blocks = ArrayList<Block>()
    lateinit var type: MobType
    private val scriptBlock: ScriptBlock?

    init {
        val readTime = measureTimeMillis {
            input.toBinaryBufferedStream().use {
                readFromStream(it)
            }
        }
        logger.info("Mob {} read in {} milliseconds", filePath, readTime)
        logger.info("Mob size without AIGraph: {}", mainBlockSize())

        scriptBlock = blocks.find { b -> b is ScriptBlock } as ScriptBlock?
    }

    @Throws(MobException::class)
    constructor(file: String) : this(file, Paths.get(file).toFile().inputStream())

    @Throws(MobException::class)
    constructor(file: String, bytes: ByteArray) : this(file, ByteArrayInputStream(bytes))

    private fun readFromStream(it: StreamInput) {
        try {
            val fileSignature = it.readUInt()
            if (fileSignature != SIG_MOB) {
                "Not a mob-file: $filePath. Aborting.".run {
                    logger.fatal(this)
                    throw MobException(this)
                }
            }
        } catch (e : EOFException){
            logger.fatal(e)
            throw MobException("Unexpected end of file when reading $filePath. Aborting", e)
        }

        it.readUInt() // read and discard the main block size
        type = MobType.fromSignature(it.readLong())

        while (!it.isEof) {
            val newBlock = Block.createBlock(it)
            blocks.add(newBlock)
            logger.info("Read block of type {}, size {}", newBlock::class.simpleName, newBlock.getSize())
        }
    }

    fun mainBlockSize(): Int = blocks.fold(16, { acc, block ->
        if (block.signature != SIG_AIGRAPH) acc + block.getSize() else acc
    })

    fun serialize(out: OutputStream) {
        with(out.toBinaryBufferedStream()) {
            writeMobEntryHeader(SIG_MOB, mainBlockSize())
            writeLong(type.signature)
            blocks.forEach { it.serialize(this) }
            flush()
        }
    }

    fun accept(visitor: MobVisitor) {
        blocks.forEach { x -> x.accept(visitor) }
    }

    fun getScriptBytes(): ByteArray {
        return scriptBlock?.script?.toByteArray(eiCharset) ?: ByteArray(0)
    }

    fun setScriptBytes(bytes: ByteArray) {
        scriptBlock?.script = bytes.toString(eiCharset)
    }

    fun backup() {
        val backupFile = backupFilePath.toFile()
        if (!backupFile.exists() && !backupFile.createNewFile()) {
            val msg = "Failed to create backup file $backupFilePath"
            logger.fatal(msg)
            throw MobException(msg)
        }
        backupFile.outputStream().use {
            serialize(it)
        }
    }

    companion object {
        const val SIG_MOB = 0xA000u
        val eiCharset: Charset = Charset.forName("windows-1251")
    }

    enum class MobType(val signature: Long) {
        QUEST(0x80000D000),
        ZONE(0x80000C000);

        companion object {
            private val map = values().associateBy(MobType::signature)
            fun fromSignature(type: Long): MobType = map[type] ?: throw MobException(
                "Unknown mob type, aborting."
            )
        }
    }
}