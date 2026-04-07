package com.example.ozmade.main.seller.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "seller_settings")

class SellerLocalStore(private val context: Context) {

    private val KEY_REGISTERED = booleanPreferencesKey("seller_registered")
    private val KEY_SELLER_MODE = booleanPreferencesKey("is_seller_mode")

    suspend fun isSellerRegistered(): Boolean {
        return context.dataStore.data.map { it[KEY_REGISTERED] ?: false }.first()
    }

    suspend fun setSellerRegistered(value: Boolean) {
        context.dataStore.edit { it[KEY_REGISTERED] = value }
    }

    val isSellerModeFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_SELLER_MODE] ?: false }

    suspend fun setSellerMode(value: Boolean) {
        context.dataStore.edit { it[KEY_SELLER_MODE] = value }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}