package com.alekseyzhelo.eimob

import com.alekseyzhelo.BuildConfig
import io.kotlintest.matchers.withClue
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
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

        "susl_canyon_of_death should load with correct size and serialize" {
            testMob("/susl_canyon_of_death.mob", 471287)
        }

        "zone2 should load with correct size and serialize" {
            // size is one byte less than the original EI file due to MobSurgeon stripping empty last line
            testMob("/zone2.mob", 236385)
        }

        "gz1g should load with correct size and serialize" {
            testMob("/gz1g.mob", 12983)
        }

        "should fail properly on non-mob" {
            val resource = this.javaClass.getResource("/canyon_script_bytes")
            shouldThrow<MobException> {
                MobFile(File(resource.path).absolutePath)
            }
        }

        "should fail properly on empty file" {
            val resource = this.javaClass.getResource("/empty")
            shouldThrow<MobException> {
                MobFile(File(resource.path).absolutePath)
            }
        }

        "l: all original mobs should load and serialize" {
            val eiMobsPath = Paths.get(BuildConfig.EI_PATH).resolve("Maps")
            val inputDir = eiMobsPath.toFile()
            withClue(
                "A correct Evil Islands installation path should be specified in " +
                        "gradle-local.properties property ei_path, " +
                        "example: ei_path=C:\\\\\\\\Program Files (x86)\\\\\\\\Проклятые Земли"
            ) {
                inputDir.exists() shouldBe true
            }
            inputDir.list { _: File, name: String -> name.endsWith(".mob") }?.forEach {
                val file = MobFile(inputDir.resolve(it).absolutePath)
                testSerialization(inputDir.resolve(it).readBytes(), file, strict = true)
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