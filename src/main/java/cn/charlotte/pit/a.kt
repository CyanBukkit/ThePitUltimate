package cn.charlotte.pit

import dev.jnic.annotation.Include
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

@Include
class a {

    @Throws(IOException::class)
    private fun c(v1: String, v2: String): String {
        val url = URL("http://127.0.0.1?v1=$v1&v2=$v2&pl=ThePit")
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("User-Agent", "Mozilla/5.0")

        BufferedReader(InputStreamReader(con.inputStream)).use { `in` ->
            val response = StringBuilder()
            var inputLine: String?
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            return response.toString()
        }
    }

    fun k(): Int {
        val rand = to(UUID.randomUUID().toString())
        val sKey = to("114514")
        val key = to("HuanMeng=250")

        return try {
            val response = c(e(rand, sKey), e(rand, key))

            if (response.startsWith("<")) {
                0
            } else {
                val respRand = e(e(response, key), sKey)
                if (rand.startsWith(respRand))
                    1
                else
                    2
            }
        } catch (e: IOException) {
            0
        }
    }

    private fun e(s1: String, s2: String): String {
        val result = StringBuilder()
        val minLength = minOf(s1.length, s2.length)
        for (i in 0 until minLength) {
            result.append((s1[i].code xor s2[i].code).toChar())
        }
        return result.toString()
    }

    private fun to(s: String): String {
        val bytes = s.toByteArray()
        return buildString {
            bytes.forEach { byte ->
                var value = byte.toInt()
                repeat(8) {
                    append(if (value and 128 == 0) '0' else '1')
                    value = value shl 1
                }
            }
        }
    }
}
