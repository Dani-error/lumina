package dev.dani.lumina.api.util


/*
 * Project: lumina
 * Created at: 25/06/2025 11:31
 * Created by: Dani-error
 */
object ClassHelper {

    fun classExists(className: String): Boolean {
        try {
            val classLoader = ClassHelper::class.java.classLoader
            Class.forName(className, false, classLoader)
            return true
        } catch (exception: ClassNotFoundException) {
            return false
        }
    }

}