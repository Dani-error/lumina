package dev.dani.lumina.api.profile

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import dev.dani.lumina.api.util.wrap
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern


/*
 * Project: lumina
 * Created at: 23/06/2025 13:19
 * Created by: Dani-error
 */
fun interface ProfileResolver {

    fun resolveProfile(profile: Profile): CompletableFuture<Profile.Resolved>

    companion object {

        fun mojang(): ProfileResolver = ProfileResolverMojang

        fun caching(delegate: ProfileResolver): Cached = ProfileResolverCached(delegate)

    }

    interface Cached : ProfileResolver {

        fun fromCache(name: String): Profile.Resolved?
        fun fromCache(uniqueId: UUID): Profile.Resolved?
        fun fromCache(profile: Profile): Profile.Resolved?

    }

}

internal object ProfileResolverMojang : ProfileResolver {

    private const val DEFAULT_TIMEOUT = 10_000
    private const val NAME_TO_UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft/%s"
    private const val UUID_TO_PROFILE_ENDPOINT = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"

    private val GSON: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(ProfileProperty::class.java, ProfilePropertyTypeAdapter())
        .create()


    private val PROFILE_PROPERTIES_TYPE: Type =
        object : TypeToken<Set<ProfileProperty>>() {}.type

    private val UUID_NO_DASH_PATTERN = Pattern.compile("-", Pattern.LITERAL)
    private val UUID_DASHER_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})")

    private fun makeRequest(endpoint: String): JsonObject {
        var connection = createBaseConnection(endpoint)
        var redirectCount = 0

        while (redirectCount++ < 10) {
            connection.connect()
            when (val status = connection.responseCode) {
                HttpURLConnection.HTTP_MOVED_TEMP,
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_SEE_OTHER -> {
                    val cookies = connection.getHeaderField("Set-Cookie")
                    val redirectTarget = connection.getHeaderField("Location")
                    connection = createBaseConnection(redirectTarget).apply {
                        setRequestProperty("Cookie", cookies)
                    }
                }
                HttpURLConnection.HTTP_OK -> {
                    return connection.inputStream.use {
                        InputStreamReader(it, StandardCharsets.UTF_8).use { reader ->
                            GSON.fromJson(reader, JsonObject::class.java)
                        }
                    }
                }
                else -> throw IllegalArgumentException("Unable to fetch data, server responded with $status")
            }
        }

        throw IllegalStateException("Endpoint request redirected more than 10 times!")
    }

    private fun createBaseConnection(endpoint: String): HttpURLConnection {
        return (URI(endpoint).toURL().openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Connection", "close")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "lumina")
            readTimeout = DEFAULT_TIMEOUT
            connectTimeout = DEFAULT_TIMEOUT
            useCaches = true
            instanceFollowRedirects = true
        }
    }

    override fun resolveProfile(profile: Profile): CompletableFuture<Profile.Resolved> =
        CompletableFuture.supplyAsync(wrap {
            var uniqueId = profile.uniqueId
            if (uniqueId == null) {
                val responseData = makeRequest(NAME_TO_UUID_ENDPOINT.format(profile.name))
                val rawUniqueId = responseData["id"].asString
                val dashedId = UUID_DASHER_PATTERN.matcher(rawUniqueId).replaceAll("$1-$2-$3-$4-$5")
                uniqueId = UUID.fromString(dashedId)
            }

            val profileId = UUID_NO_DASH_PATTERN.matcher(uniqueId.toString()).replaceAll("")
            val responseData = makeRequest(UUID_TO_PROFILE_ENDPOINT.format(profileId))
            val name = responseData["name"].asString
            val properties: Set<ProfileProperty> =
                GSON.fromJson(responseData["properties"], PROFILE_PROPERTIES_TYPE)

            return@wrap Profile.resolved(name, uniqueId!!, properties)
        })

    private class ProfilePropertyTypeAdapter : TypeAdapter<ProfileProperty>() {
        override fun write(out: JsonWriter, value: ProfileProperty?) {
            value?.let {
                out.beginObject()
                    .name("name").value(it.name)
                    .name("value").value(it.value)
                    .name("signature").value(it.signature)
                    .endObject()
            }
        }

        override fun read(input: JsonReader): ProfileProperty? {
            if (input.peek() == JsonToken.NULL) {
                input.nextNull()
                return null
            }

            var name: String? = null
            var value: String? = null
            var signature: String? = null

            input.beginObject()
            while (input.peek() != JsonToken.END_OBJECT) {
                when (input.nextName().lowercase()) {
                    "name" -> name = input.nextString()
                    "value" -> value = input.nextString()
                    "signature" -> {
                        signature = if (input.peek() == JsonToken.NULL) {
                            input.nextNull(); null
                        } else {
                            input.nextString()
                        }
                    }
                    else -> input.skipValue()
                }
            }
            input.endObject()

            return if (name != null && value != null)
                ProfileProperty.property(name, value, signature)
            else null
        }
    }

}

internal class ProfileResolverCached(private val delegate: ProfileResolver) : ProfileResolver.Cached {

    private val nameToUniqueIdCache = mutableMapOf<String, CacheEntry<UUID>>()
    private val uuidToProfileCache = mutableMapOf<UUID, CacheEntry<Profile.Resolved>>()

    override fun resolveProfile(profile: Profile): CompletableFuture<Profile.Resolved> {
        fromCache(profile)?.let { return CompletableFuture.completedFuture(it) }

        return delegate.resolveProfile(profile).whenComplete { resolved, exception ->
            if (exception == null && resolved != null) {
                nameToUniqueIdCache[resolved.name] =
                    CacheEntry(resolved.uniqueId, ENTRY_KEEP_ALIVE_TIME)
                uuidToProfileCache[resolved.uniqueId] =
                    CacheEntry(resolved, ENTRY_KEEP_ALIVE_TIME)
            }
        }
    }

    override fun fromCache(name: String): Profile.Resolved? {
        val uuid = nameToUniqueIdCache[name]?.takeIf { !it.expired }?.value
        return uuid?.let { fromCache(it) }
    }

    override fun fromCache(uniqueId: UUID): Profile.Resolved? {
        return uuidToProfileCache[uniqueId]?.takeIf { !it.expired }?.value
    }

    override fun fromCache(profile: Profile): Profile.Resolved? {
        return profile.uniqueId?.let { fromCache(it) }
            ?: profile.name?.let { fromCache(it) }
    }

    private data class CacheEntry<T>(
        val value: T,
        private val timeoutTime: Long = System.currentTimeMillis() + ENTRY_KEEP_ALIVE_TIME
    ) {
        val expired: Boolean get() = System.currentTimeMillis() > timeoutTime
    }

    companion object {

        private const val ENTRY_KEEP_ALIVE_TIME = 3 * 60 * 60 * 1000L // 3h in ms

    }
}