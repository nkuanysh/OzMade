package com.example.ozmade.main.locale

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
private val Context.dataStore by preferencesDataStore(name = "settings")

enum class AppLang(val code: String) {
    KK("kk"), RU("ru")
}

class LanguageStore(private val context: Context) {

    private val KEY_LANG = stringPreferencesKey("lang")

    val langFlow: Flow<AppLang> = context.dataStore.data.map { prefs ->
        when (prefs[KEY_LANG]) {
            AppLang.KK.code -> AppLang.KK
            else -> AppLang.RU // по умолчанию RU
        }
    }

    suspend fun setLang(lang: AppLang) {
        context.dataStore.edit { it[KEY_LANG] = lang.code }
    }

    suspend fun isLangChosen(): Boolean {
        val current = context.dataStore.data.map { it[KEY_LANG] }.map { it != null }
        return current.map { it }.first()
    }
}