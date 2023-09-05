package com.example.lifeplus

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.encode() = URLEncoder.encode(this, StandardCharsets.UTF_8.toString()) ?: ""
fun String.decode() = URLDecoder.decode(this, StandardCharsets.UTF_8.toString()) ?: ""