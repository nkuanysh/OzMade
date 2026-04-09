package com.example.ozmade.main.seller.delivery

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDeliveryRoute(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: SellerDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Настройки доставки", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is SellerDeliveryUiState.Data) {
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = {
                            viewModel.saveAll(
                                onSuccess = onSuccess,
                                onError = { msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Сохранить изменения", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            when (val st = uiState) {
                is SellerDeliveryUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }

                is SellerDeliveryUiState.Error -> {
                    ErrorContent(message = st.message, onRetry = { viewModel.load() })
                }

                is SellerDeliveryUiState.Data -> {
                    val s = st.ui
                    val scroll = rememberScrollState()

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        InfoCard(
                            text = "Настройте способы, которыми вы готовы передавать товары покупателям. Активные способы будут отображаться в ваших товарах."
                        )

                        DeliveryCard(
                            title = "Самовывоз",
                            description = "Покупатель сам забирает товар по вашему адресу",
                            icon = Icons.Default.Storefront,
                            enabled = s.pickupEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(pickupEnabled = on) } }
                        ) {
                            ModernTextField(
                                value = s.pickupAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupAddress = v) } },
                                label = "Адрес пункта выдачи",
                                icon = Icons.Outlined.LocationOn,
                                placeholder = "г. Алматы, ул. Абая 10, оф. 5"
                            )
                            Spacer(Modifier.height(16.dp))
                            ModernTextField(
                                value = s.pickupTime,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupTime = v) } },
                                label = "График работы",
                                icon = Icons.Outlined.Schedule,
                                placeholder = "Пн-Пт: 10:00 - 19:00"
                            )
                        }

                        DeliveryCard(
                            title = "Моя курьерская доставка",
                            description = "Доставка вашими силами в определенном радиусе",
                            icon = Icons.Default.LocalShipping,
                            enabled = s.myDeliveryEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(myDeliveryEnabled = on) } }
                        ) {
                            ModernTextField(
                                value = s.centerAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(centerAddress = v) } },
                                label = "Базовый адрес (склад/офис)",
                                icon = Icons.Outlined.LocationOn
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ModernTextField(
                                    value = s.centerLat,
                                    onValueChange = { v -> viewModel.updateLocal { it.copy(centerLat = v) } },
                                    label = "Широта",
                                    modifier = Modifier.weight(1f),
                                    keyboardType = KeyboardType.Decimal
                                )
                                ModernTextField(
                                    value = s.centerLng,
                                    onValueChange = { v -> viewModel.updateLocal { it.copy(centerLng = v) } },
                                    label = "Долгота",
                                    modifier = Modifier.weight(1f),
                                    keyboardType = KeyboardType.Decimal
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Радиус покрытия",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${s.radiusKm} км",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Slider(
                                    value = s.radiusKm.toFloat(),
                                    onValueChange = { v ->
                                        viewModel.updateLocal { it.copy(radiusKm = v.toInt().coerceIn(1, 100)) }
                                    },
                                    valueRange = 1f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                )
                                
                                Text(
                                    "Ваши товары будут доступны для заказа курьером в пределах этого радиуса от базового адреса.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        DeliveryCard(
                            title = "Межгород (ТК)",
                            description = "Доставка через сторонние транспортные компании",
                            icon = Icons.Default.Public,
                            enabled = s.intercityEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(intercityEnabled = on) } }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Outlined.Info, 
                                        contentDescription = null, 
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Покупатели из других регионов увидят возможность доставки ТК. Вы сможете обсудить детали отправки в чате.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = icon?.let { { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) } },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DeliveryCard(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.surface 
                     else MaterialTheme.colorScheme.surfaceVariant,
        label = "bgColor"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                       else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        label = "borderColor"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        label = "alpha"
    )
    
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        border = BorderStroke(1.dp, animatedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 6.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (enabled) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(Modifier.weight(1f)) {
                    Text(
                        title, 
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        description, 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
                
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        thickness = 0.5.dp
                    )
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Произошла ошибка", 
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message, 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Попробовать снова")
        }
    }
}
