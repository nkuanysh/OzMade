package com.example.ozmade.main.user.orderflow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    title: String,
    price: Double,
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onClose: () -> Unit,
    onChooseDelivery: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Заказ", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
            }

            Spacer(Modifier.height(10.dp))

            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("Цена: $price ₸")
                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = onMinus) { Text("-") }
                        Spacer(Modifier.width(12.dp))
                        Text(quantity.toString(), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(12.dp))
                        OutlinedButton(onClick = onPlus) { Text("+") }
                        Spacer(Modifier.weight(1f))
                        Text("Итого: ${price * quantity} ₸")
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onChooseDelivery,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Способы доставки") }

            Spacer(Modifier.height(18.dp))
        }
    }
}