package fr.netstat.locationindicatorwhitelist

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = Preferences::class.qualifiedName
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledPackages(flags: Int = 0) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") packageManager.getInstalledPackages(flags)
        }

    private fun getInstalledPackagesNames(flags: Int = 0) =
            getInstalledPackages(flags).map { it.packageName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val whitelist = Preferences.getWhitelist(this)
        Log.d(TAG, "whitelist: ${whitelist.joinToString()}")

        val packageList =
                getInstalledPackagesNames()
                        .sortedWith(
                                compareBy(
                                        { !whitelist.contains(it) },
                                        { it },
                                )
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
            listView.setItemChecked(idx, whitelist.contains(item))
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val packageName = adapter.getItem(position)!!
            if (listView.isItemChecked(position)) {
                Preferences.addToWhitelist(this, packageName)
            } else {
                Preferences.removeFromWhitelist(this, packageName)
            }
        }
    }
}