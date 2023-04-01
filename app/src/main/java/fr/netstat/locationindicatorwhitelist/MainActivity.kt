package fr.netstat.locationindicatorwhitelist

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

private data class Pkg(val packageName: String, val appName: String) {
    override fun toString() = if (appName == packageName) appName else "$appName ($packageName)"
}

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = Preferences::class.qualifiedName
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledPackagesInfo(flags: Long = 0) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(flags),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(flags.toInt())
        }

    private fun getApplicationInfo(packageName: String, flags: Long = 0) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(flags),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, flags.toInt())
        }

    private fun getInstalledPackages(flags: Long = 0) =
        getInstalledPackagesInfo(flags).map { packageInfo ->
            val packageName = packageInfo.packageName
            val appInfo = getApplicationInfo(packageName)
            val appName = packageManager.getApplicationLabel(appInfo) as String
            Pkg(packageName, appName)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val whitelist = Preferences.getWhitelist(this)
        Log.d(TAG, "whitelist: ${whitelist.joinToString()}")

        val packageList =
            getInstalledPackages()
                .sortedWith(
                    compareBy(
                        { !whitelist.contains(it.packageName) },
                        { it.appName },
                        { it.packageName },
                    ),
                )
        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                packageList,
            )
        val listView = findViewById<ListView>(R.id.packages_list_view)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        for (idx in 0 until adapter.count) {
            val item = adapter.getItem(idx)
            listView.setItemChecked(idx, whitelist.contains(item!!.packageName))
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val packageName = adapter.getItem(position)!!.packageName
            if (listView.isItemChecked(position)) {
                Preferences.addToWhitelist(this, packageName)
            } else {
                Preferences.removeFromWhitelist(this, packageName)
            }
        }
    }
}
