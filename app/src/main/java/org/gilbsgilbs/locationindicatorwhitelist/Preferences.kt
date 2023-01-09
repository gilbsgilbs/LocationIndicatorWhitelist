package org.gilbsgilbs.locationindicatorwhitelist

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.XSharedPreferences

class Preferences {
    companion object {
        private val TAG = Preferences::class.qualifiedName
        private const val PREF_KEY_WHITELIST = "whitelist"

        private const val WHITELIST_DELIMITER = ";"

        // This is only allowed because LSPosed patches SharedPreferences for modules when running inside the application context.
        @SuppressLint("WorldReadableFiles")
        private fun getPref(context: Context? = null): SharedPreferences {
            if (context == null) {
                return XSharedPreferences(BuildConfig.APPLICATION_ID, PREF_KEY_WHITELIST)
            }

            try {
                @Suppress("DEPRECATION")
                return context.getSharedPreferences(PREF_KEY_WHITELIST, Context.MODE_WORLD_READABLE)
            } catch (exc: SecurityException) {
                Toast.makeText(context, "Error: please make sure the LSPosed module is installed.", Toast.LENGTH_LONG).show()
                throw exc
            }
        }

        fun getWhitelist(context: Context? = null): MutableSet<String> {
            val whitelistStr = getPref(context).getString(PREF_KEY_WHITELIST, null) ?: return mutableSetOf()
            return whitelistStr.split(WHITELIST_DELIMITER).filterNot { it.isEmpty() }.toMutableSet()
        }

        @Synchronized
        fun addToWhitelist(context: Context, packageName: String) {
            Log.d(TAG, "adding $packageName to whitelist.")

            val whitelist = getWhitelist(context)
            whitelist.add(packageName)

            val prefsEditor = getPref(context).edit()
            prefsEditor.putString(PREF_KEY_WHITELIST, whitelist.joinToString(WHITELIST_DELIMITER))
            prefsEditor.apply()
        }

        @Synchronized
        fun removeFromWhitelist(context: Context, packageName: String) {
            Log.d(TAG, "removing $packageName to whitelist.")

            val whitelist = getWhitelist(context)
            whitelist.remove(packageName)

            val prefsEditor = getPref(context).edit()
            prefsEditor.putString(PREF_KEY_WHITELIST, whitelist.joinToString(WHITELIST_DELIMITER))
            prefsEditor.apply()
        }
    }
}