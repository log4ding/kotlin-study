package com.example.kotlinexam

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

class SimpleTests3 {
    @Test
    fun test1() {
        val jsonString = """{
            "method": "Hello",
            "pathRegex": "\\d{3}-\\d{2}-\\d{4}"
        }"""

        val objectMapper = ObjectMapper()
        val whiteList = objectMapper.readValue(jsonString, Whitelist::class.java)
        println(whiteList)
    }

    @Test
    fun test2() {
        val regex = Regex("/moim/chats/.*/posts")

        println("/moim/chats/1234/posts".matches(regex))
        println("/moim/chats/1234/1234/posts".matches(regex))
        println("/moim/chats/1234".matches(regex))
    }

    @Test
    fun test3() {
        assertTrue(checkExtensions.contains(getExtension("1.tar.lz")))
    }

    private fun getExtension(filename: String): String {
        return filename.split(".").last()
    }

    data class WhiteListInfo(
        val method: String = "",
        @JsonDeserialize(using = PathRegexDeserializer::class)
        val pathRegex: Regex? = null,
    )


    class PathRegexDeserializer : JsonDeserializer<Regex>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Regex? {
            val regexString = p?.text
            val pattern = regexString?.let { Pattern.compile(it) }
            return pattern?.toRegex()
        }
    }

    private val checkExtensions = listOf(
        "gif", "webp", "3g2", "3gp", "3gp2", "3gpp", "3gpp2",
        "asf", "asx", "avi", "dat", "divx", "dmb", "dmskm",
        "dvr-ms", "fly", "flv", "gom", "ifo", "imp4", "ivf",
        "k3g", "m2ts", "m1v", "m2v", "m4v", "mkv", "mov", "mp4",
        "m4v", "mp4v", "mjpeg", "mpe", "mpeg", "mpg", "mpv2", "mqv",
        "mts", "mwa", "ogm", "ogv", "pgm", "rm", "rmvb", "skm", "swf",
        "ts", "tp", "vob", "wax", "webm", "wm", "wma", "wmd", "wmp",
        "wmv", "wmx", "wpl", "wvx", "avif", "apng", "mng", "7z", "ace",
        "ais", "alz", "arj", "b64", "cab", "egg", "gz", "hgx", "jas",
        "lzh", "lzma", "pak", "psz", "rar", "sea", "tar.lz", "tgz",
        "xz", "zip", "zool"
    )

    @Test
    fun test4() {
        val keywords = listOf(MyClass(name = "1"), MyClass(name = "2"))

        println(keywords)
        println(keywords.map { it.name })

    }

    data class MyClass(
        val name: String,
    )

    @Test
    fun test5() {
        val now = Date().toInstant().minusSeconds(300)

        println(now.toEpochMilli())

    }

    @Test
    fun test6() {
        val duration: Double = 0.00

        assertTrue(duration == 0.0)
    }

    @Test
    fun test7() {
        val rule = null
        val rule2 = "LOW_COPY"

        val NORMAL_TRANSCODE_RULES = listOf("LOW_COPY", "LOW_ENC")

//        println(!NORMAL_TRANSCODE_RULES.contains(rule))
        println(rule2 !in NORMAL_TRANSCODE_RULES)

    }
}