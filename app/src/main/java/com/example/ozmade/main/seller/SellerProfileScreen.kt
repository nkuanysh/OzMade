package com.example.ozmade.main.seller

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SellerProfileScreen(
    onBecomeBuyer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Профиль продавца", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBecomeBuyer,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Стать покупателем")
        }

        Spacer(Modifier.height(14.dp))
    }
}