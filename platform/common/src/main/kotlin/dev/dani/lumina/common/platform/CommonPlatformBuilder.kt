package dev.dani.lumina.common.platform

import dev.dani.lumina.api.platform.*
import dev.dani.lumina.api.profile.ProfileResolver
import dev.dani.lumina.api.scoreboard.ScoreboardHandlerSettings
import dev.dani.lumina.api.tablist.TabListHandlerSettings


/*
 * Project: lumina
 * Created at: 24/06/2025 13:02
 * Created by: Dani-error
 */
abstract class CommonPlatformBuilder<P, E> : Platform.Builder<P, E> {

    protected var extension: E? = null
    protected var logger: PlatformLogger? = null
    protected var debug: Boolean = DEFAULT_DEBUG
    protected var profileResolver: ProfileResolver? = null
    protected var versionAccessor: PlatformVersionAccessor? = null
    protected var packetAdapter: PlatformPacketAdapter<P, E>? = null
    protected var taskManager: PlatformTaskManager? = null
    protected var tablistHandlerDecorator: (TabListHandlerSettings.Builder<P>.() -> Unit)? = null
    protected var scoreboardHandlerDecorator: (ScoreboardHandlerSettings.Builder<P>.() -> Unit)? = null

    override fun debug(debug: Boolean): Platform.Builder<P, E> {
        this.debug = debug
        return this
    }

    override fun extension(extension: E): Platform.Builder<P, E> {
        this.extension = extension
        return this
    }

    override fun logger(logger: PlatformLogger): CommonPlatformBuilder<P, E> {
        this.logger = logger
        return this
    }

    override fun profileResolver(profileResolver: ProfileResolver): Platform.Builder<P, E> {
        this.profileResolver = profileResolver
        return this
    }


    override fun versionAccessor(versionAccessor: PlatformVersionAccessor): Platform.Builder<P, E> {
        this.versionAccessor = versionAccessor
        return this
    }

    override fun packetFactory(packetFactory: PlatformPacketAdapter<P, E>): Platform.Builder<P, E> {
        this.packetAdapter = packetFactory
        return this
    }

    override fun taskManager(taskManager: PlatformTaskManager): Platform.Builder<P, E> {
        this.taskManager = taskManager
        return this
    }

    override fun tablistHandler(
        decorator: TabListHandlerSettings.Builder<P>.() -> Unit
    ): CommonPlatformBuilder<P, E> {
        this.tablistHandlerDecorator = decorator
        return this
    }

    override fun scoreboardHandler(
        decorator: ScoreboardHandlerSettings.Builder<P>.() -> Unit
    ): CommonPlatformBuilder<P, E> {
        this.scoreboardHandlerDecorator = decorator
        return this
    }

    override fun build(): Platform<P, E> {
        // validate that the required values are present
        requireNotNull(this.extension) { "extension" }

        // let the downstream builder set all default values if required
        this.prepareBuild()

        // validate that the required values are present
        requireNotNull(this.logger) { "logger" }

        // use the default profile resolver if no specific one was specified
        if (this.profileResolver == null) {
            this.profileResolver = DEFAULT_PROFILE_RESOLVER
        }

        return this.doBuild()
    }
    
    protected abstract fun prepareBuild()

    protected abstract fun doBuild(): Platform<P, E>

    companion object {

        protected val DEFAULT_DEBUG: Boolean = java.lang.Boolean.getBoolean("lumina.debug")
        protected val DEFAULT_PROFILE_RESOLVER = ProfileResolver.caching(ProfileResolver.mojang())

    }
    
}