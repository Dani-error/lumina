package dev.dani.lumina.api.profile

import dev.dani.lumina.api.util.safeEquals
import java.util.*


/*
 * Project: lumina
 * Created at: 23/06/2025 13:18
 * Created by: Dani-error
 */
interface ProfileProperty {

    val name: String
    val value: String
    val signature: String?

    companion object {
        fun property(name: String, value: String, signature: String? = null): ProfileProperty =
            DefaultProfileProperty(name, value, signature)
    }
}

internal data class DefaultProfileProperty(
    override val name: String,
    override val value: String,
    override val signature: String?
) : ProfileProperty {

    override fun hashCode(): Int =
        Objects.hash(name, value, signature)

    override fun equals(other: Any?): Boolean = safeEquals<ProfileProperty>(this, other) { orig, comp ->
        orig.name == comp.name &&
                orig.value == comp.value &&
                Objects.equals(orig.signature, comp.signature)
    }
}