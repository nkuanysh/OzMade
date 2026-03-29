package com.example.ozmade

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.ozmade.navigation.AppNavHost
import com.example.ozmade.ui.theme.OzMadeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        setContent {
            OzMadeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // Push notifications data (from Extras)
                    val openChat = intent?.getBooleanExtra("open_chat", false) ?: false
                    val chatId = intent?.getIntExtra("chat_id", 0) ?: 0
                    val sellerId = intent?.getIntExtra("seller_id", 0) ?: 0
                    val productId = intent?.getIntExtra("product_id", 0) ?: 0
                    val sellerName = intent?.getStringExtra("seller_name") ?: "Продавец"
                    val productTitle = intent?.getStringExtra("product_title") ?: "Товар"
                    val price = intent?.getIntExtra("price", 0) ?: 0

                    // Deep Link data
                    val data: Uri? = intent?.data
                    var deepLinkProductId = 0
                    var deepLinkChatId = 0
                    
                    data?.let { uri ->
                        Log.d("DeepLink", "URI: $uri")
                        
                        // Handle custom scheme ozmade://products/123 or ozmade://chats/55
                        if (uri.scheme == "ozmade") {
                            val host = uri.host
                            val segments = uri.pathSegments
                            
                            if (host == "products") {
                                deepLinkProductId = segments.firstOrNull()?.toIntOrNull() ?: 0
                            } else if (host == "chats") {
                                deepLinkChatId = segments.firstOrNull()?.toIntOrNull() ?: 0
                            }
                        } else {
                            // Handle HTTP/HTTPS: https://ozmade-applink.vercel.app/products/123
                            val segments = uri.pathSegments
                            if (segments.size >= 2) {
                                when (segments[0]) {
                                    "products" -> deepLinkProductId = segments[1].toIntOrNull() ?: 0
                                    "chats" -> deepLinkChatId = segments[1].toIntOrNull() ?: 0
                                }
                            } else if (segments.size >= 1 && uri.host == "34.178.41.41") {
                                // Fallback for old IP links if needed
                                when (segments[0]) {
                                    "products" -> deepLinkProductId = segments.getOrNull(1)?.toIntOrNull() ?: 0
                                    "chats" -> deepLinkChatId = segments.getOrNull(1)?.toIntOrNull() ?: 0
                                }
                            }
                        }
                    }

                    AppNavHost(
                        navController = navController,
                        openChatFromPush = openChat || (deepLinkChatId != 0),
                        pushChatId = if (deepLinkChatId != 0) deepLinkChatId else chatId,
                        pushSellerId = sellerId,
                        pushProductId = productId,
                        pushSellerName = sellerName,
                        pushProductTitle = productTitle,
                        pushPrice = price,
                        deepLinkProductId = deepLinkProductId
                    )
                }
            }
        }
    }
}
