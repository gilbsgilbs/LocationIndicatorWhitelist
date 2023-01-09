package fr.netstat.locationindicatorwhitelist

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

fun xLog(msg: String) {
    XposedBridge.log(msg)
}

fun xLogDebug(msg: String) {
    if (BuildConfig.DEBUG) {
        xLog(msg)
    }
}

class IndicatorWhitelistHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(loadedPackage: LoadPackageParam) {
        when (loadedPackage.packageName) {
            "com.android.systemui" -> {
                XposedHelpers.findAndHookMethod(
                    "com.android.systemui.appops.AppOpsControllerImpl",
                    loadedPackage.classLoader,
                    "getActiveAppOps",
                    Boolean::class.javaPrimitiveType, // showPaused
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            @Suppress("UNCHECKED_CAST")
                            val result = param.getResult() as ArrayList<Any>
                            val filteredResult = result.filter { appOpItem ->
                                val packageName = (appOpItem::class.java.getField("mPackageName").get(appOpItem)) as String
                                val whitelist = Preferences.getWhitelist()
                                val isWhitelisted = whitelist.contains(packageName)
                                xLogDebug("(LocationIndicatorWhitelist) $packageName is active.")
                                xLogDebug("(LocationIndicatorWhitelist) whitelist: ${whitelist.joinToString()}.")
                                if (isWhitelisted) {
                                    xLogDebug("(LocationIndicatorWhitelist) $packageName is whitelisted, filtering it out.")
                                }
                                !isWhitelisted
                            }.toCollection(ArrayList())
                            param.setResult(filteredResult)
                        }
                    }
                )
            }
        }
    }
}
