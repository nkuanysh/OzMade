package com.example.ozmade

import android.os.Bundle
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
        setContent {
            OzMadeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val openChat = intent?.getBooleanExtra("open_chat", false) ?: false
                    val chatId = intent?.getIntExtra("chat_id", 0) ?: 0
                    val sellerId = intent?.getIntExtra("seller_id", 0) ?: 0
                    val productId = intent?.getIntExtra("product_id", 0) ?: 0
                    val sellerName = intent?.getStringExtra("seller_name") ?: "Продавец"
                    val productTitle = intent?.getStringExtra("product_title") ?: "Товар"
                    val price = intent?.getIntExtra("price", 0) ?: 0

                    AppNavHost(
                        navController = navController,
                        openChatFromPush = openChat,
                        pushChatId = chatId,
                        pushSellerId = sellerId,
                        pushProductId = productId,
                        pushSellerName = sellerName,
                        pushProductTitle = productTitle,
                        pushPrice = price
                    )
                }
            }
        }
    }
}