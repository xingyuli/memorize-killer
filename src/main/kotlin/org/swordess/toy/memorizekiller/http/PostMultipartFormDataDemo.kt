package org.swordess.toy.memorizekiller.http

import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

private val requestFactory = NetHttpTransport().createRequestFactory() {
    it.parser = JsonObjectParser(JacksonFactory())
}

fun String.httpPost(content: ()-> HttpContent) {
    val response = requestFactory.buildPostRequest(GenericUrl(this), content()).execute()
    try {
        println(response.parseAsString())
    } finally {
        response.disconnect()
    }
}

fun main(args: Array<String>) {
    val boundary = "myTestBoundary"
    val terminator = "\r\n"
    "http://localhost:2333/api/sns/publish".httpPost {
        ByteArrayContent("multipart/form-data; boundary=$boundary", ByteArrayOutputStream().apply {
            write("--$boundary$terminator".toByteArray())

            write("Content-Disposition: form-data; name=\"content\"$terminator$terminator".toByteArray())
            write("abc$terminator".toByteArray())

            write("--$boundary$terminator".toByteArray())

            write("Content-Disposition: form-data; name=\"files\"; filename=\"zz.png\"$terminator".toByteArray())
            write("Content-Type: image/png$terminator$terminator".toByteArray())
            write(FileInputStream("""C:\Users\Vic\Desktop\images\zz.png""").readBytes())
            write("$terminator".toByteArray())

            write("--$boundary--$terminator".toByteArray())
        }.toByteArray())
    }
}
