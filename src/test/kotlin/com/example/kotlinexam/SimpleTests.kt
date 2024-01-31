package com.example.kotlinexam

import arrow.core.Either
import com.example.kotlinexam.chapter8.joinToString
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.lang.Math.ceil
import java.lang.Math.floor
import java.math.BigInteger
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import java.util.zip.CRC32
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.experimental.xor
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleTests {
    val isTimeChat: Boolean = true
    var chatLogType: ChatLogType = ChatLogType.PHOTO
    val EXTRA_KEY_FOR_MIMETYPE = "mt"
    val pattern = Pattern.compile("webp|gif")
    var extra = ""

    @Test
    fun simpleTest() {
        // true
        var isHost = false
        var timeChat = false
        var isAdmin = true
        println(!(timeChat && isAdmin))

        // false 나와야함
        isHost = false
        timeChat = true
        isAdmin = true
        println(isHost && !(timeChat && isAdmin))

        // true
        isHost = true
        timeChat = false
        isAdmin = false
        println(isHost && !(timeChat && isAdmin))
    }

    @Test
    fun simpleTest1() {
        var test = "/alarm/{}/test"
        test = test.format("data")
        println(test)
    }

    @Test
    fun tests() {
        extra = "{\"mt\":\"image/webp\"}"

        checkTimeChatPhotoExtension()

    }

    @Test
    fun test2() {
        extra = "{\"mt\":[\"image/webp\", \"image/jpg\"]}";
        chatLogType = ChatLogType.PHOTOS

        checkTimeChatPhotoExtension()
    }

    @Test
    fun test3() {
        val list = listOf("hamy-1", "hamy-2", "hamy-3", "hamy-4", "hamy-5", "hamy-6", "hamy-7", "hamy-8", "hamy-9")
        val partition = null ?: ""

        println(list.sortedDescending().takeWhile { it > partition })

        val list2 = listOf("hamy-1", "hamy-2", "hamy-3", "hamy-4", "hamy-6", "hamy-7", "hamy-8", "hamy-9")
        val partition2 = "hamy-5"

        println(list2.sortedDescending().takeWhile { it > partition2 })

        val list3 = listOf("hamy-1", "hamy-2", "hamy-3", "hamy-4", "hamy-5", "hamy-6", "hamy-7", "hamy-8", "hamy-9")

        println(list3.sortedDescending().takeWhile { it > partition2 })
    }

    @Test
    fun test4() {
        val minutes = "115"
        println(makeCronExpression(minutes)) // 1시간 55분

        val minutes2 = "1350"
        println(makeCronExpression(minutes2)) // 22시간 30분

        val minutes3 = "300"
        println(makeCronExpression(minutes3)) // 5시간

        val minutes4 = "60"
        println(makeCronExpression(minutes4)) // 1시간

        val minutes5 = "20"
        println(makeCronExpression(minutes5)) // 25분

        val minutes6 = "1440"
        println(makeCronExpression(minutes6)) // 24H
    }

    @Test
    fun test5() {
        val hours = Duration.ofDays(1)
        val ratio = 0.1
        val result = Duration.ofSeconds((hours.toSeconds() * Random.nextDouble(1.0 - ratio, 1.0)).toLong())
        println(result.toHours())
    }

    @Test
    fun test6() {
        val clusterId = "kakao"
        try {
            clusterId.toLong()
        } catch (e: Exception) {
            println("fail")
        }
        println("success")
    }

    @Test
    fun test7() {
        val EXTRA_SID_FOR_WRITABLE_LEVERAGE_MESSAGE = setOf<String>("namecard", "talkmusic", "gift")
        val sid = "\"talkmusic\""
        println(EXTRA_SID_FOR_WRITABLE_LEVERAGE_MESSAGE.contains(sid.replace("\"", "").toLowerCase()))


        val a = true
        val b = true
        val c = false
        println(a && b && c)
    }

    @Test
    fun test8() {
        val reportedCount: Int = 3
        var reportScore = (reportedCount - 1) * -0.1
        if (reportScore < -0.5) reportScore = -0.5
        println(if (reportedCount <= 1) 0 else reportScore)
    }

    fun makeCronExpression(minutesString: String): String {
        val duration = Duration.ofMinutes(minutesString.toLong())
        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val hoursCron = if (hours == 24L) {
            "${LocalDateTime.now().hour.toLong()}"
        } else if (hours != 0L) {
            "*/$hours"
        } else "*"
        val minutesCron = if (hours < 1 && minutes != 0L) {
            "*/$minutes"
        } else "${LocalDateTime.of(2023, 1, 26, 9, 19).minute}"

        return "0 $minutesCron $hoursCron * * ?"
    }

    private fun checkTimeChatPhotoExtension() {
        if (!isTimeChat ||
            (ChatLogType.PHOTO.type != chatLogType.type && ChatLogType.PHOTOS.type != chatLogType.type)
        ) return;
        try {
            val extraJson = ObjectMapper().readTree(extra);
            if (extraJson.has(EXTRA_KEY_FOR_MIMETYPE)) {
                val nodes = extraJson.get(EXTRA_KEY_FOR_MIMETYPE);
                if (nodes.isArray) {
                    val mimeTypes = nodes as ArrayNode
                    for (i in 0 until mimeTypes.size()) {
                        checkPhotoExtension(mimeTypes[i].toString())
                    }
                } else {
                    val mimeTypes = nodes.toString();
                    checkPhotoExtension(mimeTypes);
                }
            }
        } catch (e: JsonProcessingException) {
            throw RuntimeException("json parsing exception");
        }
    }


    private fun checkPhotoExtension(thumbnailUrl: String?) {
        if (thumbnailUrl.isNullOrEmpty()) return
        if (pattern.matcher(thumbnailUrl).find()) {
            throw RuntimeException("message type exception")
        }
    }

    @Test
    fun test9() {
        val l = listOf(
            mapOf("openchatScore" to 0.2),
            mapOf("recentActionScore" to 0.2),
            mapOf("hostPenaltyScore" to 0.1),
            mapOf("area1EnRatio" to 0.1)
        )

        val l2 = listOf(
            mapOf(
                "openchatScore" to 0.2,
                "recentActionScore" to 0.2,
                "hostPenaltyScore" to 0.1,
                "area1EnRatio" to 0.1
            )
        )

        println(l)
        println(l2)
    }

    @Test
    fun test10() {
        var linkReportedCount = 1
        var chatReportedCount = 3

        linkReportedCount = if (linkReportedCount - 1 < 1) 0 else linkReportedCount - 1
        chatReportedCount = if (chatReportedCount - 1 < 1) 0 else chatReportedCount - 1
        println("linkReportedCount = $linkReportedCount, chatReportedCount=$chatReportedCount")

        var reportScore = linkReportedCount * -0.05 + chatReportedCount * -0.02
        if (reportScore < -0.5) reportScore = -0.5

        println(reportScore)
    }

    @Test
    fun test11() {
        val focusCronExpression = makeFocusCronExpression(
            "10:00",
            "22:00",
            listOf(0, 1, 1, 1, 0, 0, 0),
            5
        )

        println(focusCronExpression)
    }

    fun makeFocusCronExpression(
        startTime: String,
        endTime: String,
        dayOfWeekList: List<Int>,
        duration: Int
    ): String {
        val startHour = startTime.split(":")[0]
        val endHour = endTime.split(":")[0]

        var hourCron = "${startHour}-${endHour}"
        if (startHour.toInt() > endHour.toInt()) {
            val startHours = (23 downTo startHour.toInt()).map { it }.sorted().joinToString(",")
            val endHours = (0..endHour.toInt()).map { it }.joinToString(",")
            hourCron = "$endHours,$startHours"
        }
        val minutesCron = "*/${duration}"

        val dayOfWeekCron = dayOfWeekList.mapIndexed { index, i ->
            if (i == 1) index else null
        }.filterNotNull().joinToString(",")
        return "0 $minutesCron $hourCron * * $dayOfWeekCron"
    }

    @Test
    fun test12() {

        val log = DeleteImageLog("i:cumFrY/bPAbaSlnaXG/dLuCcJ39C8Z07SPRTNyKSK")
        val presetKeys = setOf("ccfHMY/bPAa8STezfd/H4BM3BKDqJF2NhakoL4Qk0", "cumFrY/bPAbaSlnaXG/dLuCcJ39C8Z07SPRTNyKSK")

        println(isNotDeleteImage(log, "test line", presetKeys))

    }

    @Test
    fun test13() {
        val resultMap = mapOf("1" to 9)
        val testCount = resultMap["test"]?.toInt() ?: 0
        print(testCount)
    }

    private fun isNotDeleteImage(
        deleteImageLog: DeleteImageLog,
        line: String,
        imagePresetKeys: Set<String>,
    ): Boolean {
        if (deleteImageLog.kageAccessKey.startsWith("http")) {
            println("DeleteKageImages: not delete image(startWith http), line -> $line")
            return true
        } else if (deleteImageLog.kageAccessKey.startsWith("p:")) {
            println("DeleteKageImages: not delete image(startWith preset prefix), line -> $line")
            return true
        } else if (imagePresetKeys.firstOrNull { deleteImageLog.kageAccessKey.contains(it) } != null) {
            println("DeleteKageImages: not delete image(imagePresetKey), line -> $line")

            return true
        }

        return false
    }

    @Test
    fun test14() {
        makeAgeBandScore(30)
            .forEach { println(it) }
    }

    private fun makeAgeBandScore(ageBand: Int): List<Map<String, Double>> {
        val minAge = if (ageBand - 10 < 15) 15 else ageBand - 10
        val maxAge = ageBand + 10
        val scoreArr = listOf(0.05, 0.9, 0.05)
        return listOf(minAge, ageBand, maxAge)
            .mapIndexed { index, i ->
                mapOf("ageBand${i}Score" to scoreArr[index])
            }
    }

    @Test
    fun test15() {
        val openDirectChatCount = 97
        val directChatLimit = 100
        val rate: Double = openDirectChatCount.toDouble() / directChatLimit.toDouble()
        println(rate)
        println(
            when {
                directChatLimit <= openDirectChatCount || rate > 0.95 -> -0.2
                else -> 0.0
            }
        )
    }

    @Test
    fun test16() {
        val videoTitle = "안녕하세요. 타이틀이 잘 잘리는지 테스트를 하기 위한 문자열 입니다. "
        println(videoTitle.take(30) + "...")
    }

    @Test
    fun test17() {
        val minutes = LocalDateTime.of(2023, 1, 26, 9, 19).minute
        println(minutes)
    }

    @Test
    fun test18() {
        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        val summaryList: List<String> = runBlocking {
            (0..4).map { num ->
                async(Dispatchers.IO) { testSearch(num) }
            }.awaitAll()
        }

        println(summaryList)
        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    fun testSearch(type: Int): String {
        if (type != 3) {
            Thread.sleep(1500)
            return "TEST-$type"
        } else {
            return "ACCEPT-$type"
        }
    }

    @Test
    fun test19() {
        val map = mapOf(
            SearchWeightType.SIMILARITY_SIMPLE.title to mapOf(
                "title" to 2.0,
                "content" to 1.0,
                "openlinkUrl" to 1.0,
                "cardId" to 1.0,
                "hashTags" to 1.0,
                "latestJoinQuery" to 0.0
            ),
            SearchWeightType.BM25.title to mapOf(
                "title" to 2.0,
                "content" to 1.0,
                "openlinkUrl" to 1.0,
                "cardId" to 1.0,
                "hashTags" to 1.0,
                "latestJoinQuery" to 0.0
            ),
            SearchWeightType.BM25_B.title to mapOf(
                "title" to 0.0,
                "content" to 0.0,
                "openlinkUrl" to 0.0,
                "cardId" to 0.0,
                "hashTags" to 0.0,
                "latestJoinQuery" to 0.05
            ),
            SearchWeightType.BM25_K.title to mapOf(
                "title" to 2.0,
                "content" to 2.0,
                "openlinkUrl" to 2.0,
                "cardId" to 2.0,
                "hashTags" to 2.0,
                "latestJoinQuery" to 0.5
            ),
            SearchWeightType.PROXIMITY.title to mapOf(
                "title" to 2.0,
                "content" to 1.0,
                "openlinkUrl" to 1.0,
                "cardId" to 1.0,
                "hashTags" to 1.0,
            ),
            SearchWeightType.RECENCY.title to mapOf(
                "createdAt" to 0.0
            )
        )

        val first = map.keys.associateWith {
            listOf(map[it])
        }[SearchWeightType.BM25.title]

        val second = listOf(map[SearchWeightType.BM25.title])

        assertEquals(first, second)
    }

    @Test
    fun test20() {
        val keyword = "cc"

        println("acc".contains(keyword))

        val linkName: String? = null
        val name: String = "dff"

        val data = listOf("a", "b", "c")
            .firstOrNull { keyword -> name.contains(keyword) || linkName?.contains(keyword) ?: false } ?: ""
        println(data)
    }

    @Test
    fun test21() {
//        val sqlTimeUTC = LocalTime.of(14, 30).atOffset(ZoneOffset.)

        // UTC 시간대로 LocalDateTime 객체 생성
        val utcZoneId = ZoneId.of("UTC")
//        val utcLocalDateTime =
//
//        println(utcLocalDateTime)
    }

    @Test
    fun test22() {
        val description: String? = ""

        val data = if (description.isNullOrEmpty()) {
            null
        } else {
            description
        }

        val data2 = description?.takeIf { it.isNotEmpty() }

        assertTrue(data == data2)
    }

    @Test
    fun test23() {
        val l = listOf(1..10)

        println(l.subList(0, l.size))

        val l2 = listOf(1..1000)
        println(l2.subList(0, 100))
    }

    @Test
    fun test24() {
        val jsonStr = "[{\"type\":\"OT01\",\"k\":\"sr\"}]"
        val objectMapper = ObjectMapper()
        val data = objectMapper.readValue(jsonStr, object : TypeReference<List<Map<String, Any>>>() {})
        assertTrue(data != null)
    }

    @Test
    fun test25() {
        val ddds = listOf("test", "test", "test3")

        val d = ddds.mapNotNull {
            when (it) {
                "test" -> "filtering"
                else -> {
                    println("error")
                    return@mapNotNull null
                }
            }
        }.firstOrNull()

        println(d)
    }

    @Test
    fun test26() {
        val l = listOf(1, 2, 3, 4, 5)
        println(l.take(10))

    }

    @Test
    fun test27() {
        val chosung = "ㄴ날"
        val number = "1"
        val title = "ㄱ123ㄷ"

        val regex = Regex("[ㄱ-ㅎ0-9]+$")

        val matchResult = regex.find(chosung)
        if (matchResult != null) {
            println("초성 = " + matchResult.value)
        }

        val mr = regex.find(number)
        if (mr != null) {
            println("숫자 = " + mr.value)
        }

        val mr2 = regex.find(title)
        if (mr2 != null) {
            println("초성 + 숫자 = " + mr2.value)
        }
    }

    @Test
    fun test28() {
        val d1 = mapOf("type" to "OT01", "k" to "ca")
        val d1_1 = mapOf("type" to "OT01", "k" to "ca")
        val d2 = mapOf("type" to "OT02", "k" to "s", "query" to "테스트")
        val d2_1 = mapOf("type" to "OT02", "k" to "s", "query" to "테스트")
        val d2_2 = mapOf("type" to "OT02", "k" to "s")
        val d3 = mapOf("type" to "OT01", "k" to "ca", "query" to "테스트")
        val d3_1 = mapOf("type" to "OT01", "k" to "ca", "query" to "테스트")

        assertFalse(isSameReferer(d1, d2))
        assertTrue(isSameReferer(d1, d1_1))
        assertTrue(isSameReferer(d2, d2_1))
        assertFalse(isSameReferer(d2_1, d2_2))
        assertTrue(isSameReferer(d3, d3_1))
    }

    private fun isSameReferer(oldRef: Map<String, Any>, newRef: Map<String, Any>): Boolean {
        val compareKeys = listOf("type", "k", "query")

        return compareKeys.all {
            oldRef[it] == newRef[it]
        }
    }

    @Test
    fun test29() {
        val objectMapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val ot = objectMapper.readValue("{\"type\":\"OT04\",\"k\":\"ok\",\"test\":\"111\"}", OpenchatTab::class.java)
        println(ot.k)
    }

    @Test
    fun test30() {
        var a = true
        var b = false
        var c = true

        // true
        println((a && b) || c)

        a = false
        b = true
        c = false
        // false
        println((a && b) || c)

        a = false
        b = false
        c = false
        // false
        println((a && b) || c)

        a = true
        b = false
        c = false
        // false
        println((a && b) || c)
    }

    @Test
    fun test31() {
        val threeDays = (3 downTo 1).map {
            LocalDateTime.now().minusDays(it.toLong()).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        }
        println(threeDays)
    }

    @Test
    fun test32() {
        val min = 3
        val max = 10
        val levelCount = 4
        val step = 2


        val rangeList = (0 until levelCount).map { i ->
            (min + i * step) until min + (i + 1) * step
        }.toMutableList()

        val lastFirstElement = rangeList[levelCount - 1].first
        rangeList[levelCount - 1] = (lastFirstElement..max)

        val rangeMap = rangeList.withIndex().associate { (index, range) ->
            range to index + 1
        }
        // 3-5, 5-7, 7-9, 9-10
        println(rangeList)
        println(rangeMap.entries.find { it.key.contains(-1) }?.value)
    }

    @Test
    fun test33() {
        println(listOf(1, 2, 3, 4, 5, 6, 7, null).count {
            it ?: return@count false
            it < 3
        })
    }

    @Test
    fun test34() {
        val description: String? = "data"
        val data2 = description?.takeIf { it.isNotEmpty() }
        assertTrue(data2 == "data")
    }

    @Test
    fun test35() {
        val memberLimit = 21
        var updateMultiChatMemberCount = false

        var memberCount: Int = 20
        do {
            var unit = Math.round(memberLimit * 0.1).toInt()
            if (unit > 10) {
                unit = 10
            }
            if (unit == 0 || memberCount % unit != 0) {
                break
            }
            updateMultiChatMemberCount = true
        } while (false)

        assertTrue(updateMultiChatMemberCount)
    }

    @Test
    fun test36() {
        val objectMapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val userMentionRegex = Regex("\"type\":\"user\"")
        val jsonStr = """
            {\"link_id\":[\"21971365\"],\"content\":[\"[{\\\"id\\\":23591573917672174,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573917896375,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573917981508,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918030374,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918030621,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918031178,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918031179,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918031255,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033414,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033418,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033596,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033600,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033601,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033628,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918033630,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918082531,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918082544,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918083005,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918083043,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918414150,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918469563,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573918785068,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573919072697,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573919300803,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573920569330,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573920830006,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573920849747,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573921318910,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573921398344,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573922384416,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573922931362,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573922980311,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573923629817,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573923630897,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573923812997,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573923846622,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573925355995,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573926875278,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573926980253,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928343264,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928359842,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928360479,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928360543,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928394480,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928396478,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928508723,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928700085,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928706636,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928706731,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928706990,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928707058,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928707572,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928710262,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928726613,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928763224,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928763674,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928763916,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573928929833,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929020953,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929034491,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929269650,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929322718,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929422213,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929426446,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929426457,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929429919,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929429928,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929453204,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929514220,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929516185,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929554364,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929554510,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929554973,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929575357,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929589015,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929590167,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929590216,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929599277,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929599914,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929601325,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929601429,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929601669,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929601733,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929605784,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606125,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606198,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606278,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606357,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606412,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929606639,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929607442,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929607921,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929638517,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929638948,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929639051,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929639314,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929640534,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929640928,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929641121,\\\"type\\\":\\\"user\\\"},{\\\"id\\\":23591573929641449,\\\"type\\\":\\\"user\\\"},{\\\"text\\\":\\\"123\\\",\\\"type\\\":\\\"text\\\"}]\"]}
        """.trimIndent().replace("\\", "")

//        val result = objectMapper.readValue(jsonStr, object: TypeReference<Map<String, Any>>() {})

        val check = userMentionRegex.containsMatchIn(jsonStr)
        println(check)

//        val content = result["content"] as Map<String, Any>?
//        val userType = content!!["type"] as String
//        if (userType == "user") {
//            println("user!!")
//        }

    }

    @Test
    fun test37() {
//        val regex = Regex("""link_id:(?<=\[)\\?".*?\\?(?=\])""")
        val regex = Regex("""link_id:((?=\[).*?(?=\]))""")
        val input = "{link_id:[12345],content:[[{type:user}]]}"
        val matches = regex.find(input)

        println(matches?.groupValues?.getOrNull(1))
    }

    @Test
    fun test38() {
        val text =
            "{content:[[n  {n    text : 꼬물 채링 https://youtube.com/@user-jw5mq8uc8l,n    type : textn  }n]],link_id:[264927025]}"
        val linkId = "[264927025]".replace("[", "").replace("]", "")

        println(text.contains(linkId))
    }

    @Test
    fun test39() {
        val l = listOf(1, 2, 3, 4, 5, 6)

        val element = l.find { n -> n == 4 }

        println(element)

    }

    @Test
    fun test40() {
        val minutesString = 60
        val duration = Duration.ofMinutes(minutesString.toLong())
        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val hoursCron = if (hours == 24L) {
            "${LocalDateTime.now().hour.toLong()}"
        } else if (hours != 0L) {
            "*/$hours"
        } else "*"
        val minutesCron = if (hours < 1 && minutes != 0L) {
            "*/$minutes"
        } else "${LocalDateTime.now().minute}"

        println("0 $minutesCron $hoursCron * * ?")
    }

    @Test
    fun test41() {
        val method: String = ""
        println(method.takeIf { it.isNotEmpty() })
    }

    @Test
    fun test42() {
        val nickname = "\u2008"
        println(nickname)

        assertTrue("".equals(nickname.trim()))
    }

    @Test
    fun test43() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val count = 3
        val sliceCount = list.size / count

        (0..count).map { idx ->
            val start = idx * sliceCount
            var end = start + sliceCount
            if (end > list.size) {
                end = list.size
            }

            println("$idx list = " + list.subList(start, end))
        }
    }

    @Test
    fun test44() {
        val width = 1080
        val height = 2400
        var thumbnailHeight = 640
        var thumbnailWidth = 640
        // 120 x 80

        if (width > height) {
            thumbnailHeight = floor(height * 640.0 / width).toInt()
        } else {
            thumbnailWidth = floor(width * 640.0 / height).toInt()
        }

        println("thumbnail w x h = $thumbnailWidth x $thumbnailHeight")
    }

    @Test
    fun test45() {
        val cmtl: List<Int>? = null
        cmtl?.forEach {
            if (it > 3) {
                throw RuntimeException("ERROR!")
            }
        }
        println("RALLALALALALALA")
    }

    @Test
    fun test46() {
        val path: String? = "1234"
        val token: String? = null
        val tpath: String? = null

        val bFlag = (path.isNullOrEmpty() && token.isNullOrEmpty())

        assertFalse(bFlag)
    }

    @Test
    fun test47() {
//        val tpath = "/openchat/sandbox/message/50011/18391975864013744/HkdcMO1A317wZJaMCgTXdSHbSdlk741AmtLE5kI3f4EhC5YZYXP8OVbyjmS0eMgU.png"
        val tpath = "/openchat"

        val depth = 3
        val splitPath = tpath.split("/")
        val startIndex = depth.coerceAtMost(splitPath.size)
        val resultPath = if (splitPath.size > depth && startIndex < splitPath.size - 1) {
            val resultPath = splitPath.subList(startIndex, splitPath.size - 1).joinToString("/")
            "/$resultPath/"
        } else {
            tpath
        }

        println("$resultPath")
    }

    @Test
    fun test48() {
        val agreements = listOf("LOCAL", "BLACK")
        val agreementNames = listOf("BLACK", "WHITE", "LOCAL")

        val result = agreements.find { !agreementNames.contains(it) }
            ?.let { false }
            ?: true

        println("result = $result")
    }

    @Test
    fun test49() {
        val l1 = listOf(1L, 2L, 3L, 4L)
        val l2 = listOf(5L, 6L)

        println(l1 + l2)

        val mergedList = ArrayList<Long>(l1.size + l2.size)
        mergedList.addAll(l1)
        mergedList.addAll(l2)
        println(mergedList)
    }

    @Test
    fun test50() {
        val rc = RefererCheck()
        println(rc.getType())
    }

    @Test
    fun test51() {
        val check = isSupportedThumbnail("test.qqq", "VIDEO")
        assertTrue(check)
    }

    @Test
    fun test52() {
        val linkId = 43578
        val hash = aesEncrypt(linkId.toString(), "tenthopenchatkey".toByteArray())
        println(hash)

        val decryptHash = aesDecrypt(hash, "tenthopenchatkey".toByteArray())
        println(decryptHash)
    }

    @Test
    fun test53() {
        val entities = listOf(
            Entity("1", "nick", "hari"),
            Entity("1", "nick", "bo"),
            Entity("1", "nick", "leo"),
            Entity("2", "john", "lily"),
            Entity("2", "john", "beta")
        )

        val entityMap = entities.groupBy { it.id }
        println(entityMap)
    }

    @Test
    fun test54() {
        println(ChatLogType.of("TEST"))
    }

    @Test
    fun test55() {
        val list = listOf(1, 2, 3, 4, 5)
        list.any { check(it) }
    }

    fun check(i: Int): Boolean {
        return if (i % 2 == 0) {
            throw IllegalArgumentException()
        } else {
            true
        }
    }

    data class Entity(
        val id : String,
        val nickname: String,
        val catName: String,
    )

    private fun isSupportedThumbnail(filename: String, chatLogType: String?): Boolean {
        val supportedThumbnailExtensions = listOf(
            "jpeg", "jpg", "gif", "png", "webp",
            "ico", "bmp", "tiff", "tif", "svg", "heif", "heic"
        )
        val supportedThumbnailChatLogType = listOf("VIDEO")

        val ext = getFileExtension(filename)
        val supportedChatLogType: Boolean = chatLogType?.let {
            supportedThumbnailChatLogType.contains(it)
        } ?: false
        val checkExtensions = "PHOTO".equals(chatLogType) || "PHOTOS".equals(chatLogType)
        return (checkExtensions && supportedThumbnailExtensions.contains(ext)) || supportedChatLogType
    }

    private fun getFileExtension(filename: String): String? {
        val lastDotIndex = filename.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < filename.length - 1) {
            filename.substring(lastDotIndex + 1)
        } else {
            null
        }
    }

    fun aesEncrypt(input: String, key: ByteArray): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        val encryt = Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes)
        return URLEncoder.encode(encryt, "UTF-8")
    }

    fun aesDecrypt(encryptInput: String, key: ByteArray): String {
        val input = URLDecoder.decode(encryptInput, "UTF-8")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = Base64.getUrlDecoder().decode(input)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    fun checkEror(i: Int): Either<EitherError, Unit> {
        if (i % 2 == 0) {
            Either.Left(EitherError.UpdatedError)
        }
        return Either.Right(Unit)
    }

    @Test
    fun test56() {
        val l1 = listOf(1, 2, 3, 4, 5)
        val l2 = listOf(1, 2, 3, 4, 5)

        assertTrue(l1 == l2)
    }

    @Test
    fun test57() {
        val size = 31
        val durationTtl = 120 * 1000
        val ttl = (size + 9) / 10 * durationTtl
        println(Duration.ofMillis(ttl.toLong()))
    }

    @Test
    fun test58() {
        val VIDEO_FILE_NAME = "talkv_high.mp4"
        val AUDIO_FILE_NAME = "talka_aa.m4a"
        val regex = Regex("/$VIDEO_FILE_NAME|/$AUDIO_FILE_NAME")

        val filename = "/test/test/talkv_high.mp4"
        val filename2 = "/test/test/talka_aa.m4a"
        val filename3 = "/test/test/i_asdasdas.jpg"

        assertEquals("/test/test", filename.replace(regex, ""))
        assertEquals("/test/test", filename2.replace(regex, ""))
        assertEquals(filename3, filename3.replace(regex, ""))
    }

    @Test
    fun test59() {
        assertTrue(compareExtension("temp.temp.mp4", "test.mp4"))
        assertFalse(compareExtension("temp.temp.mp4.tmp", "test.mp4"))
        assertFalse(compareExtension("temp.tmp", "test.mp4"))
        assertFalse(compareExtension("temp.tmp", "test.mp4.jpg"))
    }

    private fun compareExtension(originFilename: String?, tenthPath: String): Boolean {
        if (originFilename.isNullOrEmpty()) return false
        val originalFilenames = originFilename.split(".")
        val tenthPaths = tenthPath.split(".")

        if (originalFilenames.last() != tenthPaths.last()) {
            return false
        }

        return true
    }

    fun generateUploadFilename(fileName: String, algorithm: String = "SHA-256"): String {
        val extension = extractExtension(fileName)
        val uuid = UUID.randomUUID().toString()
        val dataToHash = "${Instant.now().nano}$uuid$fileName"
        val md = MessageDigest.getInstance(algorithm)
        val hashBytes = md.digest(dataToHash.toByteArray())
        val sb = StringBuilder()
        for (b in hashBytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString() + extension
    }
    private fun extractExtension(fileName: String): String {
        val splitFileName = fileName.split(".")
        if (splitFileName.size <= 1 || splitFileName.last().isBlank()) {
            return ""
        }
        return ".${splitFileName.last()}"
    }

    private fun encodeToBase62(value: ByteArray): String {
        val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val base = BigInteger.valueOf(characters.length.toLong())
        var number = BigInteger(value)
        var encoded = ""

        value.map {
            val remainder = number.mod(base)
            encoded = characters[remainder.toInt()] + encoded
            number /= base
        }

        return encoded
    }

    @Test
    fun test60() {
        val filename = generateUploadFilename("k.test.mgn.mp4")
        println(filename + ", " + encodeToBase62(filename.toByteArray()))
        println(UUID.randomUUID())
    }

    @Test
    fun test61() {
        val jsonString = """{
            "method": "Hello",
            "pathRegex": "\\d{3}-\\d{2}-\\d{4}"
        }"""

        val objectMapper = ObjectMapper()
        val whiteList = objectMapper.readValue(jsonString, Whitelist::class.java)
        println(whiteList)
    }
}

data class Whitelist(
    @JsonDeserialize(using = PathRegexDeserializer::class)
    val pathRegex: Regex?,
    val method: String,
)


class PathRegexDeserializer : JsonDeserializer<Regex>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Regex? {
        val regexString = p?.text
        val pattern = regexString?.let { Pattern.compile(it) }
        return pattern?.toRegex()
    }
}

//@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenchatTab(
    val type: String,
    val k: String?
)

sealed interface EitherError {
    object UpdatedError
    object CreatedError
}

class RefererCheck {
    private val type: String?

    init {
        if (true) {
            this.type = "before"
        } else {
            this.type = "test"
        }
    }

    fun getType() = this.type

    private fun checkType() {
        if (type.isNullOrEmpty()) {
            return
        }
    }
}

data class DeleteImageLog(
    val kageAccessKey: String
)

enum class ChatLogType(val type: Int) {
    PHOTO(1),
    PHOTOS(2);

    companion object {
        fun of(type: String): ChatLogType = valueOf(type)
    }
}

data class TestClass(
    val data: Map<String, String>,
    var convertData: List<String>
) {
    init {
        convertData = data.keys.toList()
    }
}

enum class SearchWeightType(val title: String) {
    SIMILARITY_SIMPLE("similaritySimple"),
    BM25("bm25"),
    BM25_B("bm25B"),
    BM25_K("bm25K"),
    PROXIMITY("proximity"),
    QUALITY("quality"),
    RECENCY("recency")
}


interface Parent {
    val t1: String
    val t2: String?
}

data class Children(
    override val t1: String,
    override val t2: String?,
    val t3: String
) : Parent