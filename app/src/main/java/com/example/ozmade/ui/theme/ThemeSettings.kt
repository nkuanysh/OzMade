package com.example.ozmade.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ozmade.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeSettings(private val context: Context) {

    private val KEY_THEME = stringPreferencesKey("theme_mode")

    val themeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[KEY_THEME]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME] = mode.name }
    }
}
