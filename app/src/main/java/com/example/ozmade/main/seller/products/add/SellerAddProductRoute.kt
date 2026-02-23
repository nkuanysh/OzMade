package com.example.ozmade.main.seller.products.add

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun SellerAddProductRoute(
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val vm: SellerAddProductViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ✅ если успешно — показываем snackbar и уходим назад
    LaunchedEffect(state.success) {
        if (state.success) {
            // snackbar показываем на текущем экране, потом уходим назад
            scope.launch {
                snackbarHostState.showSnackbar("Товар успешно добавлен")
            }
            onCreated()
            vm.consumeSuccess() // ✅ чтобы success не срабатывал снова при пересоздании
        }
    }

    SellerAddProductScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onPickPhotos = vm::onPickPhotos,
        onRemovePhoto = vm::removePhoto,
        onMovePhoto = vm::movePhoto,

        onTitle = vm::setTitle,
        onPrice = vm::setPriceText,
        onToggleCategory = vm::toggleCategory,

        onWeight = vm::setWeight,
        onHeight = vm::setHeight,
        onWidth = vm::setWidth,
        onDepth = vm::setDepth,
        onComposition = vm::setComposition,

        onDescription = vm::setDescription,
        onYoutube = vm::setYoutube,

        onCreate = vm::createProduct,
        onDismissError = vm::dismissError
    )
}