package com.tma.data.retrofit

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Primitive cookie store that stores cookies in a (volatile) hash map.
 * Will be sufficient for session cookies.
 */
class MemoryCookieStore : CookieJar {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.toUri().host] = cookies
    }

    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookieStore[url.toUri().host]
        ?: Collections.emptyList<Cookie>()
}