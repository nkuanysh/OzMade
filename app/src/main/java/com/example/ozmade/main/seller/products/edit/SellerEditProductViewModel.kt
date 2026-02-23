package com.example.ozmade.main.seller.products.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.products.add.AddProductState
import com.example.ozmade.main.seller.products.add.SellerCategory
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.ProductRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerEditProductViewModel @Inject constructor(
    private val api: OzMadeApi
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductState())
    val state: StateFlow<AddProductState> = _state

    private var loadedId: Int? = null

    fun load(productId: Int) {
        if (loadedId == productId) return
        loadedId = productId

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }

            runCatching {
                // 1) получить данные товара
                val resp = api.getProductDetails(productId)
                if (!resp.isSuccessful) error("Не удалось загрузить товар (${resp.code()})")
                val dto = resp.body() ?: error("Пустой ответ")

                // 2) заполнить state (ниже пример — подгони под свой dto)
                _state.update { st ->
                    st.copy(
                        loading = false,
                        error = null,
                        success = false,

                        title = dto.title ?: "",                 // если у тебя другое поле — поменяй
                        description = dto.description ?: "",
                        priceText = (dto.price ?: 0.0).toString(),

                        // фото: если у dto одно фото, просто добавим одно
                        photos = listOfNotNull(dto.imageUrl?.let { Uri.parse(it) }),

                        // категории: если у dto одна категория, подберём enum по title
                        selectedCategories = SellerCategory.entries
                            .filter { it.title == (dto.type ?: "") }
                            .toSet()
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message ?: "Ошибка загрузки") }
            }
        }
    }

    // === те же методы что в Add ===
    fun onPickPhotos(new: List<Uri>) {
        _state.update { st ->
            val merged = (st.photos + new).distinct().take(10)
            st.copy(photos = merged)
        }
    }

    fun removePhoto(uri: Uri) {
        _state.update { it.copy(photos = it.photos.filterNot { p -> p == uri }) }
    }

    fun movePhoto(fromIndex: Int, toIndex: Int) {
        _state.update { st ->
            val list = st.photos.toMutableList()
            if (fromIndex !in list.indices || toIndex !in list.indices) return@update st
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            st.copy(photos = list)
        }
    }

    fun setTitle(v: String) = _state.update { it.copy(title = v) }
    fun setPriceText(v: String) = _state.update { it.copy(priceText = v) }
    fun setDescription(v: String) = _state.update { it.copy(description = v) }
    fun setYoutube(v: String) = _state.update { it.copy(youtubeUrl = v) }

    fun setWeight(v: String) = _state.update { it.copy(weightText = v) }
    fun setHeight(v: String) = _state.update { it.copy(heightText = v) }
    fun setWidth(v: String) = _state.update { it.copy(widthText = v) }
    fun setDepth(v: String) = _state.update { it.copy(depthText = v) }
    fun setComposition(v: String) = _state.update { it.copy(composition = v) }

    fun toggleCategory(c: SellerCategory) {
        _state.update { st ->
            val set = st.selectedCategories.toMutableSet()
            if (!set.add(c)) set.remove(c)
            st.copy(selectedCategories = set)
        }
    }

    fun dismissError() = _state.update { it.copy(error = null) }
    fun consumeSuccess() = _state.update { it.copy(success = false) }

    fun save(productId: Int) {
        val st = _state.value
        if (!st.isValid || st.loading) return

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, success = false) }

            runCatching {
                val req = toProductRequest(st)
                val resp = api.updateProduct(productId, req)
                if (!resp.isSuccessful) {
                    val body = resp.errorBody()?.string()
                    error("Не удалось сохранить (${resp.code()})\n${body ?: resp.message()}")
                }
            }.onSuccess {
                _state.update { it.copy(loading = false, success = true) }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message ?: "Ошибка сохранения") }
            }
        }
    }

    private fun toProductRequest(st: AddProductState): ProductRequest {
        val price = st.priceValue ?: 0.0

        return ProductRequest(
            name = st.title.trim(),
            description = st.description.trim(),
            price = price,
            categories = st.selectedCategories.map { it.title },
            images = st.photos.map(Uri::toString),
            weight = st.weightText.trim().ifBlank { null },
            heightCm = st.heightText.trim().ifBlank { null },
            widthCm = st.widthText.trim().ifBlank { null },
            depthCm = st.depthText.trim().ifBlank { null },
            composition = st.composition.trim().ifBlank { null },
            youtubeUrl = st.youtubeUrl.trim().ifBlank { null }
        )
    }
}