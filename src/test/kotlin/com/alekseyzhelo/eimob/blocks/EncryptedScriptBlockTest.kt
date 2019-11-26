package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobFile
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import loggersoft.kotlin.streams.toBinaryStream
import java.io.ByteArrayOutputStream

@ExperimentalUnsignedTypes
internal class EncryptedScriptBlockTest : StringSpec() {

    init {
        "test canyon script deserialization" {
            val testBytes = this.javaClass.getResourceAsStream("/canyon_script_bytes").use {
                it.readBytes()
            }
            val testText = this.javaClass.getResourceAsStream("/canyon_script.txt")
                .bufferedReader(MobFile.eiCharset)
                .use {
                    it.readText().replace("\r", "")
                }
            val scriptBlock = EncryptedScriptBlock(testBytes)
            scriptBlock.script shouldBe testText
        }

        "test canyon script serialization" {
            val testBytes = this.javaClass.getResourceAsStream("/canyon_script_bytes").use {
                it.readBytes()
            }
            val scriptBlock = EncryptedScriptBlock(testBytes)
            val serializedBytes = ByteArrayOutputStream().use {
                it.toBinaryStream().use { scriptBlock.serialize(it) }
                it.toByteArray()
            }
            serializedBytes.copyOfRange(8, serializedBytes.size).contentEquals(testBytes) shouldBe true
        }
    }
}