package io.posidon.android.cintalauncher.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import io.posidon.android.cintalauncher.R
import io.posidon.android.cintalauncher.storage.Settings
import io.posidon.android.cintalauncher.util.StackTraceActivity
import posidon.android.conveniencelib.getStatusBarHeight

class CintaSettings : SettingsActivity() {

    override fun init(savedInstanceState: Bundle?) {
        StackTraceActivity.init(applicationContext)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment(settings))
                .commit()
        }
        findViewById<View>(R.id.settings_container).setPadding(0, getStatusBarHeight(), 0, 0)
    }

    class SettingsFragment(val settings: Settings) : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = DataStore(requireContext(), settings)
            setPreferencesFromResource(R.xml.settings_main, rootKey)
            preferenceScreen.children.iterator().forEachRemaining {
                it
            }
            preferenceManager
        }

        class DataStore(
            val context: Context,
            val settings: Settings
        ) : PreferenceDataStore() {

            override fun putString(key: String, value: String?) =
                settings.edit(context) { key set value }

            override fun putInt(key: String, value: Int) =
                settings.edit(context) { key set value }

            override fun putBoolean(key: String, value: Boolean) =
                settings.edit(context) { key set value }

            override fun getString(key: String, defValue: String?) =
                settings.getString(key) ?: defValue

            override fun getInt(key: String, defValue: Int) =
                settings.getIntOr(key) { defValue }

            override fun getBoolean(key: String, defValue: Boolean) =
                settings.getBoolOr(key) { defValue }
        }
    }
}