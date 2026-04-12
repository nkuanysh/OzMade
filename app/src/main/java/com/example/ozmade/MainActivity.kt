package com.example.ozmade

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.example.ozmade.navigation.AppNavHost
import com.example.ozmade.ui.theme.OzMadeTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import com.example.ozmade.main.user.profile.locale.AppLang
import com.example.ozmade.main.user.profile.locale.LanguageStore
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageStore: LanguageStore

    private val openChat = mutableStateOf(false)
    private val chatId = mutableStateOf(0)
    private val sellerId = mutableStateOf(0)
    private val productId = mutableStateOf(0)
    private val sellerName = mutableStateOf("Продавец")
    private val productTitle = mutableStateOf("Товар")
    private val price = mutableStateOf(0)
    private val deepLinkProductId = mutableStateOf(0)
    private val deepLinkChatId = mutableStateOf(0)
    
    // For Order notifications
    private val openOrderHistory = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        
        setContent {
            val lang by languageStore.langFlow.collectAsState(initial = AppLang.RU)
            
            CompositionLocalProvider(
                LocalContext provides remember(lang) {
                    val config = resources.configuration
                    val locale = Locale(lang.code)
                    Locale.setDefault(locale)
                    config.setLocale(locale)
                    val localizedContext = createConfigurationContext(config)
                    object : ContextWrapper(localizedContext) {
                        override fun getBaseContext(): Context = this@MainActivity
                    }
                }
            ) {
                OzMadeTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()

                        AppNavHost(
                            navController = navController,
                            openChatFromPush = openChat.value || (deepLinkChatId.value != 0),
                            pushChatId = if (deepLinkChatId.value != 0) deepLinkChatId.value else chatId.value,
                            pushSellerId = sellerId.value,
                            pushProductId = productId.value,
                            pushSellerName = sellerName.value,
                            pushProductTitle = productTitle.value,
                            pushPrice = price.value,
                            deepLinkProductId = deepLinkProductId.value,
                            openOrderHistory = openOrderHistory.value
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        // Push notifications data (from Extras)
        openChat.value = intent?.getBooleanExtra("open_chat", false) ?: false
        chatId.value = intent?.getIntExtra("chat_id", 0) ?: 0
        sellerId.value = intent?.getIntExtra("seller_id", 0) ?: 0
        productId.value = intent?.getIntExtra("productId", 0) ?: 0 // Fixed casing just in case
        if (productId.value == 0) productId.value = intent?.getIntExtra("product_id", 0) ?: 0
        
        sellerName.value = intent?.getStringExtra("seller_name") ?: "Продавец"
        productTitle.value = intent?.getStringExtra("product_title") ?: "Товар"
        price.value = intent?.getIntExtra("price", 0) ?: 0

        openOrderHistory.value = intent?.getBooleanExtra("open_order_history", false) ?: false

        // Deep Link data
        val data: Uri? = intent?.data
        deepLinkProductId.value = 0
        deepLinkChatId.value = 0
        
        data?.let { uri ->
            Log.d("DeepLink", "URI: $uri")
            
            // Handle custom scheme ozmade://products/123 or ozmade://chats/55
            if (uri.scheme == "ozmade") {
                val host = uri.host
                val segments = uri.pathSegments
                
                if (host == "products") {
                    deepLinkProductId.value = segments.firstOrNull()?.toIntOrNull() ?: 0
                } else if (host == "chats") {
                    deepLinkChatId.value = segments.firstOrNull()?.toIntOrNull() ?: 0
                }
            } else {
                // Handle HTTP/HTTPS: https://ozmade-applink.vercel.app/products/123
                val segments = uri.pathSegments
                if (segments.size >= 2) {
                    when (segments[0]) {
                        "products" -> deepLinkProductId.value = segments[1].toIntOrNull() ?: 0
                        "chats" -> deepLinkChatId.value = segments[1].toIntOrNull() ?: 0
                    }
                } else if (segments.size >= 1 && uri.host == "34.178.41.41") {
                    // Fallback for old IP links if needed
                    when (segments[0]) {
                        "products" -> deepLinkProductId.value = segments.getOrNull(1)?.toIntOrNull() ?: 0
                        "chats" -> deepLinkChatId.value = segments.getOrNull(1)?.toIntOrNull() ?: 0
                    }
                }
            }
        }
    }
}
