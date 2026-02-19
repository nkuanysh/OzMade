package com.example.ozmade.main.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onClose: () -> Unit,
    onOpenSupportChat: () -> Unit = {}
) {
    val context = LocalContext.current

    val phoneNumber = "+77077077070"

    val faqSections = remember { buildFaq() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Служба поддержки") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
            )
        }
    ) { padding ->


        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Мы готовы помогать вам каждый день с 8:00 — 22:00",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SupportPrimaryButton(
                    text = "Позвонить по телефону",
                    icon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        context.startActivity(intent)
                    }
                )

            }

            item {
                SupportPrimaryButton(
                    text = "Чат поддержки",
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                    onClick = onOpenSupportChat
                )
            }

            item {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Часто задаваемые вопросы:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // темы
            items(faqSections, key = { it.title }) { section ->
                FaqSectionCard(section = section)
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun SupportPrimaryButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
    }
}

// ---------------- FAQ UI ----------------

private data class FaqSection(
    val title: String,
    val items: List<FaqItem>
)

private data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
private fun FaqSectionCard(section: FaqSection) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            section.items.forEachIndexed { index, item ->
                FaqQuestionRow(item = item)
                if (index != section.items.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                    Divider()
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun FaqQuestionRow(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(10.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------------- FAQ DATA ----------------

private fun buildFaq(): List<FaqSection> {
    return listOf(
        FaqSection(
            title = "Заказы и общение",
            items = listOf(
                FaqItem(
                    question = "Как оформить заказ, если оплата не в приложении?",
                    answer = "Нажмите «Заказать» или «Чат» на странице товара. Договоритесь с продавцом о цене, времени и способе оплаты и получения."
                ),
                FaqItem(
                    question = "Что делать, если продавец не отвечает?",
                    answer = "Попробуйте написать ещё раз через чат. Если ответа нет долго — выберите другого продавца и сообщите нам в поддержку."
                ),
                FaqItem(
                    question = "Можно ли отменить заказ?",
                    answer = "Да. Так как договорённость происходит напрямую с продавцом, отмена также согласуется в чате."
                )
            )
        ),
        FaqSection(
            title = "Отзывы и рейтинг",
            items = listOf(
                FaqItem(
                    question = "Кто может оставить отзыв?",
                    answer = "Обычно отзыв оставляет покупатель после общения/заказа. Позже можно добавить правило «после заказа»."
                ),
                FaqItem(
                    question = "Как считаются звёзды и рейтинг?",
                    answer = "Рейтинг — это среднее значение всех оценок. Половинка звезды появляется, когда оценка содержит .5."
                )
            )
        ),
        FaqSection(
            title = "Безопасность",
            items = listOf(
                FaqItem(
                    question = "Как не попасться на мошенников?",
                    answer = "Проверяйте рейтинг продавца, читайте отзывы, уточняйте детали в чате и не переводите деньги, если условия вам не понятны."
                ),
                FaqItem(
                    question = "Что делать, если возник конфликт?",
                    answer = "Сохраните переписку и детали заказа. Напишите в поддержку — мы подскажем, как действовать дальше."
                )
            )
        )
    )
}
