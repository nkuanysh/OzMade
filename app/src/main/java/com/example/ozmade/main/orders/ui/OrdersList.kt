package com.example.ozmade.main.orders.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ozmade.main.orders.data.OrderUi
import com.example.ozmade.main.orders.data.deliveryTitle
import com.example.ozmade.main.orders.data.statusTitle

@Composable
fun OrdersList(
    orders: List<OrderUi>,
    onOpen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { o ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(o.id) }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = o.productTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))

                    Text("Статус: ${statusTitle(o.status)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Доставка: ${deliveryTitle(o.deliveryType)}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Кол-во: ${o.quantity}   Сумма: ${o.totalCost} ₸",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = o.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}