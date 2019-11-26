package com.alekseyzhelo.eimob

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths

@ExperimentalUnsignedTypes
internal class MobFileTest : StringSpec() {

    init {
        "canary test" {
            true shouldBe true
        }

        "susl_canyon_of_death should load and serialize" {
            testMob("/susl_canyon_of_death.mob", 471287)
        }

        "zone2 should load and serialize" {
            // size is one byte less than the original EI file due to MobSurgeon stripping empty last line
            testMob("/zone2.mob", 236385)
        }

        "gz1g should load and serialize" {
            testMob("/gz1g.mob", 12983)
        }

        "l: all original mobs should load and serialize with same sizes" {
            val eiMobsPath = "C:\\Program Files (x86)\\Проклятые Земли\\Maps"
            val inputDir = Paths.get(eiMobsPath).toFile()
            inputDir.list { _: File, name: String -> name.endsWith(".mob") }?.forEach {
                val file = MobFile(inputDir.resolve(it).absolutePath)
                testSerialization(inputDir.resolve(it).readBytes(), file, strict = false)
            }
        }
    }

    private fun testMob(mobPath: String, mainBlockSize: Int) {
        val mobResource = this.javaClass.getResource(mobPath)
        val file = MobFile(File(mobResource.path).absolutePath)  // workaround
        file.mainBlockSize() shouldBe mainBlockSize

        testSerialization(mobResource.readBytes(), file)
    }

    private fun testSerialization(mobBytes: ByteArray, file: MobFile, strict: Boolean = true) {
        ByteArrayOutputStream().use {
            file.serialize(it)
            if (strict) {
                it.toByteArray()!!.contentEquals(mobBytes) shouldBe true
            } else {
                it.size() shouldBe mobBytes.size
            }
        }
    }
}