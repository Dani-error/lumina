@file:Suppress("unused")

package dev.dani.lumina.api.platform

import java.util.logging.Level
import java.util.logging.Logger


/*
 * Project: lumina
 * Created at: 23/06/2025 13:10
 * Created by: Dani-error
 */
interface PlatformLogger {

    fun info(message: String)
    fun warning(message: String)
    fun error(message: String)
    fun error(message: String, exception: Throwable?)

    companion object {

        fun nop(): PlatformLogger = PlatformLoggerNOP

        fun fromJul(delegate: Logger) = PlatformLoggerJul(delegate)

    }

}


class PlatformLoggerJul(private val delegate: Logger) : PlatformLogger {

    override fun info(message: String) = delegate.info(message)

    override fun warning(message: String) = delegate.warning(message)

    override fun error(message: String) = delegate.severe(message)

    override fun error(message: String, exception: Throwable?) = delegate.log(Level.SEVERE, message, exception)

}

object PlatformLoggerNOP : PlatformLogger {

    override fun info(message: String) { }

    override fun warning(message: String) { }

    override fun error(message: String) { }

    override fun error(message: String, exception: Throwable?) { }

}