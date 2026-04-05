package com.example.ozmade.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ozmade.R

@Composable
fun LanguageScreen(
    onChooseKazakh: () -> Unit,
    onChooseRussian: () -> Unit,
    logo: @Composable () -> Unit = { DefaultLogo() }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Большой лого по центру
        Spacer(Modifier.weight(1f))

        Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
            logo()
        }

        Spacer(Modifier.weight(0.7f))

        Text(
            text = stringResource(R.string.choose_language_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChooseKazakh,
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Text(stringResource(R.string.lang_kk))
            }
            Button(
                onClick = onChooseRussian,
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Text(stringResource(R.string.lang_ru))
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DefaultLogo() {
    Image(
        painter = painterResource(id = R.drawable.image_removebg_preview),
        contentDescription = "OzMade Logo",
        modifier = Modifier.fillMaxSize()
    )
}
