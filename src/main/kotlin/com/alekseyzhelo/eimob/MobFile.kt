package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.*
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_AIGRAPH
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.system.measureNanoTime

@Suppress("MemberVisibilityCanBePrivate", "unused")
@ExperimentalUnsignedTypes
class MobFile @Throws(MobException::class) constructor(file: String, input: InputStream) {

    private val logger by LoggerDelegate()
    private val blocksInner: MutableList<Block> = ArrayList()
    private val listeners: MutableList<MobListener> = ArrayList()
    val filePath: Path = Paths.get(file)
    val backupFilePath: Path =
            filePath.resolveSibling("${FileNameUtils.getBaseName(filePath.fileName.toString())}-backup.mob")
    /**
     * Modifiable via addBlock/removeBlock methods.
     */
    val blocks: List<Block> = Collections.unmodifiableList(blocksInner)
    lateinit var type: MobType

    val scriptBlock: ScriptBlock?
        get() = getBlock()
    val diplomacyBlock: DiplomacyBlock?
        get() = getBlock()
    val worldSetBlock: WorldSetBlock?
        get() = getBlock()
    val objectsBlock: ObjectsBlock?
        get() = getBlock()

    init {
        val readTime = measureNanoTime {
            input.toBinaryBufferedStream().use {
                readFromStream(it)
            }
        }
        logger.info("Mob {} read in {} milliseconds", filePath, TimeUnit.NANOSECONDS.toMillis(readTime))
        logger.info("Mob size without AIGraph: {}", mainBlockSize())
    }

    @Throws(MobException::class)
    constructor(file: String) : this(file, Paths.get(file).toFile().inputStream())

    @Throws(MobException::class)
    constructor(file: String, bytes: ByteArray) : this(file, ByteArrayInputStream(bytes))

    private inline fun <reified T : Block> getBlock(): T? = blocks.find { b -> b is T } as T?

    private fun readFromStream(it: StreamInput) {
        try {
            val fileSignature = it.readUInt()
            if (fileSignature != SIG_MOB) {
                "Not a mob-file: $filePath. Aborting.".run {
                    logger.fatal(this)
                    throw MobException(this)
                }
            }
        } catch (e: EOFException) {
            logger.fatal(e)
            throw MobException("Unexpected end of file when reading $filePath. Aborting.", e)
        }

        it.readUInt() // read and discard the main block size
        type = MobType.fromSignature(it.readLong())

        while (!it.isEof) {
            try {
                val newBlock = Block.createTopLevelBlock(it)
                addBlock(newBlock)
                logger.info("Read block of type {}, size {}", newBlock::class.simpleName, newBlock.getSize())
            } catch (e: Exception) {
                throw MobException("Failed to read $filePath. Aborting.", e)
            }
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

    /**
     * @param position pass a negative value to add at the end of the block list.
     */
    fun addBlock(block: Block, position: Int = -1) {
        if (position < 0) {
            blocksInner.add(block)
        } else {
            blocksInner.add(position, block)
        }
        listeners.forEach { it.onBlockAdded(block) }
    }

    /**
     * @return `true` if the block has been successfully removed; `false` if it was not present in the mob file.
     */
    fun removeBlock(block: Block): Boolean {
        val result = blocksInner.remove(block)
        if (result) {
            listeners.forEach { it.onBlockRemoved(block) }
        }
        return result
    }

    fun addListener(listener: MobListener) = listeners.add(listener)

    fun removeListener(listener: MobListener) = listeners.remove(listener)

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