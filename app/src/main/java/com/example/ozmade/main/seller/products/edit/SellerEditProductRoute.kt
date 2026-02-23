package com.example.ozmade.main.seller.products.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun SellerEditProductRoute(
    productId: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val vm: SellerEditProductViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) { vm.load(productId) }

    LaunchedEffect(state.success) {
        if (state.success) {
            scope.launch { snackbarHostState.showSnackbar("Изменения сохранены") }
            onSaved()
            vm.consumeSuccess()
        }
    }

    SellerEditProductScreen(
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

        onCancel = onBack,
        onSave = { vm.save(productId) },
        onDismissError = vm::dismissError
    )
}